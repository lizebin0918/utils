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
    private ConcurrentHashMap<String, LongAdder> counter = new ConcurrentHashMap<>();

    private FrequencyHelper() {}

    /**
     * 构造函数
     * timeUnit=TimeUnit.MINUTE,interval=1,limit=1000:表示一分钟最多只能执行1000次
     * @param timeUnit 时间间隔单位
     * @param interval 时间间隔数值
     * @param max 最大次数
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
        LocalDateTime now = LocalDateTime.now();
        LongAdder count = counter.computeIfAbsent(key, (k) -> {
            return new LongAdder();
        });
        //上次调用时间 - 当前时间 < 时间间隔
        if(interval > TimeUtils.getOffset(timeUnit, lastCallDatetime, now)) {
            count.increment();
        } else {
            lastCallDatetime = LocalDateTime.now();
            count.reset();
        }
        if(count.longValue() > limit) {
            return false;
        }
        return true;
    }
}
