package seekLight.config;

import java.util.concurrent.*;

public class ThreadPools {
    public static final ExecutorService executor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2,  // 核心线程数
            Runtime.getRuntime().availableProcessors() * 4,  // 最大线程数
            60,  // 空闲线程存活时间
            TimeUnit.SECONDS,  // 时间单位
            new LinkedBlockingQueue<>(100),  // 任务队列（避免队列无界导致内存溢出）
            Executors.defaultThreadFactory(),  // 线程工厂
            new ThreadPoolExecutor.AbortPolicy()  // 拒绝策略（任务满时抛异常，便于监控）
    );
}
