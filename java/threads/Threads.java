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
	 * sleep�ȴ�, ��λΪ����, �Ѳ�׽������InterruptedException.
	 */
	public static void sleep(long durationMillis) {
		try {
			Thread.sleep(durationMillis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * sleep�ȴ����Ѳ�׽������InterruptedException.
	 */
	public static void sleep(long duration, TimeUnit unit) {
		try {
			Thread.sleep(unit.toMillis(duration));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * ����ExecutorService JavaDocʾ�������д��Graceful Shutdown����.
	 * 
	 * ��ʹ��shutdown, ֹͣ���������񲢳�����������Ѵ�������.
	 * 
	 * ���1/2��ʱʱ���, �����shutdownNow,ȡ����workQueue��Pending������,���ж�������������.
	 * 
	 * ���1/2��ʱ��Ȼ���r���t�����˳�.
	 * 
	 * �����shutdownʱ�̱߳��������ж����˴���.
	 * 
	 * �����߳�����Ƿ��ж�.
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
	 * ����ThreadFactory��ʹ�ô������߳����Լ������ֶ�����Ĭ�ϵ�"pool-x-thread-y"
	 * 
	 * ��ʽ��"mythread-%d"��ʹ����Guava�Ĺ�����
	 */
	public static ThreadFactory buildThreadFactory(String nameFormat) {
		return new ThreadFactoryBuilder().setNameFormat(nameFormat).build();
	}

	/**
	 * ���趨�Ƿ�daemon, daemon�߳������߳���ִ�����ʱ, ��������Ӧ�ò��˳�, ����daemon�߳��������.
	 * 
	 * @see #buildThreadFactory(String)
	 */
	public static ThreadFactory buildThreadFactory(String nameFormat, boolean daemon) {
		return new ThreadFactoryBuilder().setNameFormat(nameFormat).setDaemon(daemon).build();
	}

	/**
	 * ��֤������Exception�׳����̳߳ص�Runnable�����࣬��ֹ�û�û�в�׽�쳣�����ж����̳߳��е��߳�, ʹ��SchedulerService�޷�ִ��.
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
	 * ����FixedThreadPool.
	 * 
	 * 1. �����ύʱ, ����߳�����û�ﵽpoolSize���������̲߳�������(��poolSize���ύ���߳������شﵽpoolSize����������֮ǰ���߳�)
	 * 
	 * 1.a poolSize�Ǳ����Ĭ��Ϊ1�����ܺ���.
	 * 
	 * 2. ��poolSize�������ύ��, �����������Queue��, Pool�е������̴߳�Queue��take����ִ��.
	 * 
	 * 2.a QueueĬ��Ϊ���޳���LinkedBlockingQueue, Ҳ��������queueSize�����н�Ķ���.
	 * 
	 * 2.b ���ʹ���н����, ����������֮��,�����RejectHandler���д���, Ĭ��ΪAbortPolicy���׳�RejectedExecutionException�쳣.
	 * ������ѡ��Policy������Ĭ������ǰ����(Discard)������Queue�����ϵ�����(DisacardOldest)���������߳���ֱ��ִ��(CallerRuns).
	 * 
	 * 3. ��Ϊ�߳�ȫ��Ϊcore�̣߳����Բ����ڿ��л���.
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
	 * ����CachedThreadPool.
	 * 
	 * 1. �����ύʱ, ����߳�����û�ﵽcoreSize���������̲߳�������(��coreSize���ύ���߳������شﵽcoreSize, ��������֮ǰ���߳�)
	 * 
	 * 1.a coreSizeĬ��Ϊ0, �����ñ�֤�л������̴߳������󲻱�����.
	 * 
	 * 2. ��coreSize�������ύ��, ���������ύ��SynchronousQueue�����û�п����߳����̴�����ᴴ���µ��߳�, ֱ�����߳����ﵽ����.
	 * 
	 * 2.a maxSizeĬ��ΪInteger.Max, �ɽ�������.
	 * 
	 * 2.b ���������maxSize, �����߳����ﵽ����, �����RejectHandler���д���, Ĭ��ΪAbortPolicy, �׳�RejectedExecutionException�쳣.
	 * ������ѡ��Policy������Ĭ������ǰ����(Discard)���������߳���ֱ��ִ��(CallerRuns).
	 * 
	 * 3. coreSize����, maxSize���µ��߳�, �����keepAliveTime�ж�poll��������ִ�н��ᱻ������, keeAliveTimeĬ��Ϊ60��, ������.
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
	 * ��ͬʱ����min/max/queue Size���̳߳�, ���������ⳡ��.
	 * 
	 * ���粢��Ҫ��ǳ��ߣ�����SynchronousQueue������̫��.
	 * 
	 * ����ƽ��ʹ��Core�̹߳�������������ȷ�queue��queue���ٿ���ʱ�̣߳���ʱqueue�ĳ���һ��Ҫ����Ŀ�������.
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