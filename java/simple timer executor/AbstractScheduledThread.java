import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public abstract class AbstractScheduledThread extends Thread {

	private final int delaySeconds;
	
	private final int periodSeconds;
	
	private final String executeCounts;
	
	private volatile boolean isCancel = false;
	
	/**
	 * @param delaySeconds 延迟秒数
	 * @param periodSeconds 执行周期（秒）
	 * @param executeCounts 执行次数(-1表示一直执行，知道取消)
	 */
	public AbstractScheduledThread(int delaySeconds, int periodSeconds, String executeCounts) {
		this.delaySeconds = delaySeconds;
		this.periodSeconds = periodSeconds;
		this.executeCounts = executeCounts;
	}
	
	@Override
	public void run() {
		ScheduledExecutorService execute = Executors.newSingleThreadScheduledExecutor();
		final AtomicInteger latch = new AtomicInteger(Integer.parseInt(executeCounts));
		final Thread currentThread = Thread.currentThread();
		Runnable task = new Runnable() {
			@Override
			public void run() {
				if((latch.intValue() == -1 || latch.getAndDecrement() > 0) && !isCancel) {
					execute();
				} else {
					LockSupport.unpark(currentThread);
					return;
				}
			}
		};
		execute.scheduleAtFixedRate(task, delaySeconds, periodSeconds, TimeUnit.SECONDS);
		LockSupport.park(currentThread);
		if(!execute.isShutdown()) {
			execute.shutdown();
			execute = null;
		}
	}
	
	public abstract void execute();
	
	public void cancel() {
		isCancel = true;
	};
}