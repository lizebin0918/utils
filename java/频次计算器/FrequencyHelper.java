import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 频次帮助类(用于限制调用频次)
 * timeUnit=TimeUnit.MINUTE,interval=1,limit=1000:表示一分钟最多只能执行1000次；如果超过上限，会返回false，只有等下一个周期，才会重设
 * <br/>
 * Created on : 2017-03-15 23:21
 * @author lizebin
 */
public class FrequencyHelper {

    /**
     * 最大调用次数
     */
    private long limit;

    /**
     *  时间单位
     */
    private ChronoUnit timeUnit;

    /**
     * 时间间隔数值
     */
    private int interval;

    /**
     * 上次调用时间戳
     */
    private LocalDateTime lastCallDatetime = LocalDateTime.now();

    /**
     * 计数器
     */
    private ConcurrentHashMap<String, AtomicLong> counter = new ConcurrentHashMap<>();

    private FrequencyHelper() {}

    /**
     * 构造函数
     * @param timeUnit 时间间隔单位
     * @param interval 时间间隔数值
     * @param limit 最大次数
     * @return
     */
    public static FrequencyHelper newInstance(ChronoUnit unit, int interval, long limit) {
        FrequencyHelper instance = new FrequencyHelper();
        instance.timeUnit = unit;
        instance.limit = limit;
        instance.interval = interval;
        return instance;
    }

    /**
     * 是否能调用
     * @return
     */
    public boolean canCall(String key) {
        AtomicLong count = counter.computeIfAbsent(key, (k) -> {
            return new AtomicLong();
        });
        //上次调用时间 - 当前时间 >= 时间间隔:需要重设计数器
        if(interval <= timeUnit.between(lastCallDatetime, LocalDateTime.now())) {
            synchronized (this) {
                if(interval <= timeUnit.between(lastCallDatetime, LocalDateTime.now())) {
                    lastCallDatetime = LocalDateTime.now();
                    count.set(0L);
                }
            }
        }
        if(count.incrementAndGet() <= limit) {
            return true;
        }
        return false;
    }

    /*public static void main(String[] args) throws InterruptedException {
        FrequencyHelper instance = FrequencyHelper.newInstance(ChronoUnit.SECONDS, 1, 1);
        if(instance.canCall("a")) {//可执行

        } else {//不可执行

        }
    }*/
}