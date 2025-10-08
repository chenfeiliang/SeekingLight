package seekLight.utils;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 雪花算法（Snowflake）工具类，用于生成全局唯一的ID。
 * <p>
 * 结构：1位符号位 + 41位时间戳 + 10位机器ID + 12位序列号
 * </p>
 */
public class SnowflakeUtils {

    // ============================== 常量 ==============================
    /** 开始时间戳 (2020-01-01 00:00:00) */
    private static final long twepoch = 1577836800000L;

    /** 机器ID所占的位数 */
    private static final long workerIdBits = 10L;

    /** 序列号所占的位数 */
    private static final long sequenceBits = 12L;

    /** 支持的最大机器ID，结果是 1023 (这个移位算法可以很快地计算出几位二进制数所能表示的最大十进制数) */
    private static final   long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /** 支持的最大序列号，结果是 4095 */
    private static final  long maxSequence = -1L ^ (-1L << sequenceBits);

    /** 机器ID向左移 12 位 */
    private static final long workerIdShift = sequenceBits;

    /** 时间戳向左移 22 位 (10 + 12) */
    private static final long timestampLeftShift = sequenceBits + workerIdBits;

    // ============================== 全局变量 ==============================
    /** 工作机器ID */
    private static long workerId;

    /** 序列号 */
    private static long sequence = 0L;

    /** 上一次生成ID的时间戳 */
    private static long lastTimestamp = -1L;

    // ============================== 单例 ==============================
    private static volatile SnowflakeUtils instance = new SnowflakeUtils(1);

    private SnowflakeUtils(long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker ID can't be greater than %d or less than 0", maxWorkerId));
        }
        this.workerId = workerId;
    }

    /**
     * 获取雪花算法工具类的实例。
     * <p>
     * 懒汉式单例，双重检查锁定（DCL）保证线程安全和高性能。
     * </p>
     * @param workerId 工作机器ID (0-1023)
     * @return 雪花算法工具类的唯一实例
     */
    public static SnowflakeUtils getInstance(long workerId) {
        if (instance == null) {
            synchronized (SnowflakeUtils.class) {
                if (instance == null) {
                    instance = new SnowflakeUtils(workerId);
                }
            }
        }
        return instance;
    }

    // ============================== 核心方法 ==============================
    /**
     * 生成下一个唯一ID。
     * @return 一个64位的唯一ID (long)
     */
    public static synchronized String nextId() {
        long timestamp = System.currentTimeMillis();

        // 1. 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过，这是一个严重的问题，抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 2. 如果是同一时间生成的，则进行序列号自增
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & maxSequence;
            // 序列号溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒，获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        // 3. 如果是不同时间生成的，序列号重置为0
        else {
            sequence = 0L;
        }

        // 4. 更新上一次生成ID的时间戳
        lastTimestamp = timestamp;

        // 5. 拼接并返回ID
        // (timestamp - twepoch) << timestampLeftShift  // 时间戳部分
        // | (workerId << workerIdShift)                // 机器ID部分
        // | sequence                                   // 序列号部分
        long id = ((timestamp - twepoch) << timestampLeftShift)
                | (workerId << workerIdShift)
                | sequence;
        return DateUtils.dateToString(new Date())+id;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳。
     * @param lastTimestamp 上一次生成ID的时间戳
     * @return 当前新的时间戳
     */
    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    // ============================== 测试方法 ==============================
    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < 1000000; i++) {
            new Thread(()->{
                String id = SnowflakeUtils.nextId();
                if(set.contains(id)){
                    System.out.printf("1111");
                    return;
                }
                System.out.println(id + "  -->  长度: " + String.valueOf(id).length());
                set.add(id);
            }).start();
        }
    }
}