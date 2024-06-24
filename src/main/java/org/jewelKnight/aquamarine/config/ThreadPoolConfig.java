package com.impactcn.aquamarine.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author impactCn
 * @date 2024/2/8 0:15
 */
public class ThreadPoolConfig {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));

    private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;

    private static final BlockingQueue<Runnable> POOL_WORK_QUEUE = new LinkedBlockingQueue<>(128);

    private static final int KEEP_ALIVE_SECONDS = 60;

    private static final String THREAD_NAME_PREFIX = "aquamarine-thread-pool";

    private static volatile ThreadPoolConfig threadPoolConfig = null;

    private final ThreadPoolExecutor threadPoolExecutor;


    public ThreadPoolConfig() {
        threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                POOL_WORK_QUEUE,
                new ThreadFactoryBuilder().setNameFormat(THREAD_NAME_PREFIX + "-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    public static ThreadPoolConfig getNewInstance(){
        if (threadPoolConfig == null) {
            synchronized (ThreadPoolConfig.class) {
                if (threadPoolConfig == null) {
                    threadPoolConfig = new ThreadPoolConfig();
                }
            }
        }
        return threadPoolConfig;
    }

    public Future<?> submit(Runnable task) {
        return threadPoolExecutor.submit(task);
    }


    public void execute(Runnable task) {
        threadPoolExecutor.execute(task);
    }



}
