package com.lzb.common.util.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class Threads {
	
	/**
	 * sleep等待, 单位为毫秒, 已捕捉并处理InterruptedException.
	 */
	public static void sleep(long durationMillis) {
		try {
			Thread.sleep(durationMillis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * sleep等待，已捕捉并处理InterruptedException.
	 */
	public static void sleep(long duration, TimeUnit unit) {
		try {
			Thread.sleep(unit.toMillis(duration));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * 按照ExecutorService JavaDoc示例代码编写的Graceful Shutdown方法.
	 * 
	 * 先使用shutdown, 停止接收新任务并尝试完成所有已存在任务.
	 * 
	 * 如果1/2超时时间后, 则调用shutdownNow,取消在workQueue中Pending的任务,并中断所有阻塞函数.
	 * 
	 * 如果1/2超时仍然超時，則強制退出.
	 * 
	 * 另对在shutdown时线程本身被调用中断做了处理.
	 * 
	 * 返回线程最后是否被中断.
	 */
	public static boolean gracefulShutdown(ExecutorService threadPool, int shutdownTimeoutMills) {
		return MoreExecutors.shutdownAndAwaitTermination(threadPool, shutdownTimeoutMills, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * @see #gracefulShutdown(ExecutorService, int)
	 */
	public static boolean gracefulShutdown(ExecutorService threadPool, int shutdownTimeout, TimeUnit timeUnit) {
		return MoreExecutors.shutdownAndAwaitTermination(threadPool, shutdownTimeout, timeUnit);
	}

	/**
	 * 创建ThreadFactory，使得创建的线程有自己的名字而不是默认的"pool-x-thread-y"
	 * 
	 * 格式如"mythread-%d"，使用了Guava的工具类
	 */
	public static ThreadFactory buildThreadFactory(String nameFormat) {
		return new ThreadFactoryBuilder().setNameFormat(nameFormat).build();
	}

	/**
	 * 可设定是否daemon, daemon线程在主线程已执行完毕时, 不会阻塞应用不退出, 而非daemon线程则会阻塞.
	 * 
	 * @see #buildThreadFactory(String)
	 */
	public static ThreadFactory buildThreadFactory(String nameFormat, boolean daemon) {
		return new ThreadFactoryBuilder().setNameFormat(nameFormat).setDaemon(daemon).build();
	}

	/**
	 * 保证不会有Exception抛出到线程池的Runnable包裹类，防止用户没有捕捉异常导致中断了线程池中的线程, 使得SchedulerService无法执行.
	 */
	public static class WrapExceptionRunnable implements Runnable {

		private static Logger logger = LoggerFactory.getLogger(WrapExceptionRunnable.class);

		private Runnable runnable;

		public WrapExceptionRunnable(Runnable runnable) {
			this.runnable = runnable;
		}

		@Override
		public void run() {
			try {
				runnable.run();
			} catch (Throwable e) {
				// catch any exception, because the scheduled thread will break if the exception thrown to outside.
				logger.error("Unexpected error occurred in task", e);
			}
		}
	}

	private static RejectedExecutionHandler defaultRejectHandler = new AbortPolicy();

	/**
	 * 创建FixedThreadPool.
	 * 
	 * 1. 任务提交时, 如果线程数还没达到poolSize即创建新线程并绑定任务(即poolSize次提交后线程总数必达到poolSize，不会重用之前的线程)
	 * 
	 * 1.a poolSize是必填项，默认为1，不能忽略.
	 * 
	 * 2. 第poolSize次任务提交后, 新增任务放入Queue中, Pool中的所有线程从Queue中take任务执行.
	 * 
	 * 2.a Queue默认为无限长的LinkedBlockingQueue, 也可以设置queueSize换成有界的队列.
	 * 
	 * 2.b 如果使用有界队列, 当队列满了之后,会调用RejectHandler进行处理, 默认为AbortPolicy，抛出RejectedExecutionException异常.
	 * 其他可选的Policy包括静默放弃当前任务(Discard)，放弃Queue里最老的任务(DisacardOldest)，或由主线程来直接执行(CallerRuns).
	 * 
	 * 3. 因为线程全部为core线程，所以不会在空闲回收.
	 */
	public static class FixedThreadPoolBuilder {

		private int poolSize = 1;
		private int queueSize = 0;

		private ThreadFactory threadFactory = null;
		private RejectedExecutionHandler rejectHandler;

		public FixedThreadPoolBuilder setPoolSize(int poolSize) {
			this.poolSize = poolSize;
			return this;
		}

		public FixedThreadPoolBuilder setQueueSize(int queueSize) {
			this.queueSize = queueSize;
			return this;
		}

		public FixedThreadPoolBuilder setThreadFactory(ThreadFactory threadFactory) {
			this.threadFactory = threadFactory;
			return this;
		}

		public FixedThreadPoolBuilder setRejectHanlder(RejectedExecutionHandler rejectHandler) {
			this.rejectHandler = rejectHandler;
			return this;
		}
		
		protected void beforeExecuteOverride(Thread t, Runnable r) {};
		protected void afterExecuteOverride(Runnable r, Throwable t) {};

		public ExecutorService build() {
			if (poolSize < 1) {
				throw new IllegalArgumentException("size not set");
			}

			BlockingQueue<Runnable> queue = null;
			if (queueSize == 0) {
				queue = new LinkedBlockingQueue<Runnable>();
			} else {
				queue = new ArrayBlockingQueue<Runnable>(queueSize);
			}

			if (threadFactory == null) {
				threadFactory = Executors.defaultThreadFactory();
			}

			if (rejectHandler == null) {
				rejectHandler = defaultRejectHandler;
			}

			return new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, queue, threadFactory,
					rejectHandler) {
				@Override
				protected void beforeExecute(Thread t, Runnable r) {
					super.beforeExecute(t, r);
					beforeExecuteOverride(t, r);
				}
				
				@Override
				protected void afterExecute(Runnable r, Throwable t) {
					super.afterExecute(r, t);
					afterExecuteOverride(r, t);
				}
			};
		}
	}

	/**
	 * 创建CachedThreadPool.
	 * 
	 * 1. 任务提交时, 如果线程数还没达到coreSize即创建新线程并绑定任务(即coreSize次提交后线程总数必达到coreSize, 不会重用之前的线程)
	 * 
	 * 1.a coreSize默认为0, 可设置保证有基本的线程处理请求不被回收.
	 * 
	 * 2. 第coreSize次任务提交后, 新增任务提交进SynchronousQueue后，如果没有空闲线程立刻处理，则会创建新的线程, 直到总线程数达到上限.
	 * 
	 * 2.a maxSize默认为Integer.Max, 可进行设置.
	 * 
	 * 2.b 如果设置了maxSize, 当总线程数达到上限, 会调用RejectHandler进行处理, 默认为AbortPolicy, 抛出RejectedExecutionException异常.
	 * 其他可选的Policy包括静默放弃当前任务(Discard)，或由主线程来直接执行(CallerRuns).
	 * 
	 * 3. coreSize以上, maxSize以下的线程, 如果在keepAliveTime中都poll不到任务执行将会被结束掉, keeAliveTime默认为60秒, 可设置.
	 */
	public static class CachedThreadPoolBuilder {

		private int coreSize = 0;
		private int maxSize = Integer.MAX_VALUE;
		private int keepAliveSecs = 60;

		private ThreadFactory threadFactory = null;
		private RejectedExecutionHandler rejectHandler;

		public CachedThreadPoolBuilder setCoreSize(int coreSize) {
			this.coreSize = coreSize;
			return this;
		}

		public CachedThreadPoolBuilder setMaxSize(int maxSize) {
			this.maxSize = maxSize;
			return this;
		}

		public CachedThreadPoolBuilder setKeepAliveSecs(int keepAliveSecs) {
			this.keepAliveSecs = keepAliveSecs;
			return this;
		}

		public CachedThreadPoolBuilder setThreadFactory(ThreadFactory threadFactory) {
			this.threadFactory = threadFactory;
			return this;
		}

		public CachedThreadPoolBuilder setRejectHanlder(RejectedExecutionHandler rejectHandler) {
			this.rejectHandler = rejectHandler;
			return this;
		}

		protected void beforeExecuteOverride(Thread t, Runnable r) {};
		protected void afterExecuteOverride(Runnable r, Throwable t) {};
		
		public ExecutorService build() {

			if (threadFactory == null) {
				threadFactory = Executors.defaultThreadFactory();
			}

			if (rejectHandler == null) {
				rejectHandler = defaultRejectHandler;
			}

			return new ThreadPoolExecutor(coreSize, maxSize, keepAliveSecs, TimeUnit.SECONDS,
					new SynchronousQueue<Runnable>(), threadFactory, rejectHandler) {
				@Override
				protected void beforeExecute(Thread t, Runnable r) {
					super.beforeExecute(t, r);
					beforeExecuteOverride(t, r);
				}
				
				@Override
				protected void afterExecute(Runnable r, Throwable t) {
					super.afterExecute(r, t);
					afterExecuteOverride(r, t);
				}
			};
		}
	}

	/**
	 * 可同时设置min/max/queue Size的线程池, 仅用于特殊场景.
	 * 
	 * 比如并发要求非常高，觉得SynchronousQueue的性能太差.
	 * 
	 * 比如平常使用Core线程工作，如果满了先放queue，queue满再开临时线程，此时queue的长度一定要按项目需求设好.
	 */
	public static class ConfigurableThreadPoolBuilder {

		private int coreSize = 0;
		private int maxSize = Integer.MAX_VALUE;
		private int queueSize = 0;
		private int keepAliveSecs = 60;

		private ThreadFactory threadFactory = null;
		private RejectedExecutionHandler rejectHandler;

		public ConfigurableThreadPoolBuilder setCoreSize(int coreSize) {
			this.coreSize = coreSize;
			return this;
		}

		public ConfigurableThreadPoolBuilder setMaxSize(int maxSize) {
			this.maxSize = maxSize;
			return this;
		}

		public ConfigurableThreadPoolBuilder setQueueSize(int queueSize) {
			this.queueSize = queueSize;
			return this;
		}

		public ConfigurableThreadPoolBuilder setKeepAliveSecs(int keepAliveSecs) {
			this.keepAliveSecs = keepAliveSecs;
			return this;
		}

		public ConfigurableThreadPoolBuilder setThreadFactory(ThreadFactory threadFactory) {
			this.threadFactory = threadFactory;
			return this;
		}

		public ConfigurableThreadPoolBuilder setRejectHanlder(RejectedExecutionHandler rejectHandler) {
			this.rejectHandler = rejectHandler;
			return this;
		}

		protected void beforeExecuteOverride(Thread t, Runnable r) {};
		protected void afterExecuteOverride(Runnable r, Throwable t) {};
		
		public ExecutorService build() {

			BlockingQueue<Runnable> queue = null;
			if (queueSize == 0) {
				queue = new LinkedBlockingQueue<Runnable>();
			} else {
				queue = new ArrayBlockingQueue<Runnable>(queueSize);
			}

			if (threadFactory == null) {
				threadFactory = Executors.defaultThreadFactory();
			}

			if (rejectHandler == null) {
				rejectHandler = defaultRejectHandler;
			}

			return new ThreadPoolExecutor(coreSize, maxSize, keepAliveSecs, TimeUnit.SECONDS, queue, threadFactory,
					rejectHandler){
				
				@Override
				protected void beforeExecute(Thread t, Runnable r) {
					super.beforeExecute(t, r);
					beforeExecuteOverride(t, r);
				}
				
				@Override
				protected void afterExecute(Runnable r, Throwable t) {
					super.afterExecute(r, t);
					afterExecuteOverride(r, t);
				}
			};
		}
	}
}