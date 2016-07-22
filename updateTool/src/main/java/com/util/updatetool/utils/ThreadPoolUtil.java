package com.util.updatetool.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @{#} ThreadPoolUtil.java Create on 2013-6-24 上午9:59:29
 * <p>
 * class desc:
 * <p>
 * <p>Copyright: Copyright(c) 2013 </p>
 * @Version 1.0
 * @Author <a href="mailto:kris@krislq.com">Kris.lee</a>
 */
public class ThreadPoolUtil{
    public static final int SINGLETHREAD = 0;
    public static final int FIXEDTHREAD = 1;
    public static final int CACHEDTHREAD = 2;
    public static final int SCHEDULETHREAD = 3;

    private static ExecutorService[] pools = new ExecutorService[]{null, null, null, null};
    private static ExecutorService pool = null;

    public static ExecutorService getThreadPool(int kind, int... nThreads) {
        return getThreadPool(kind, false, nThreads);
    }

    public static ExecutorService getThreadPool(int kind, boolean needNewThreadPool, int... nThreads) {
        if (pools[kind] == null || needNewThreadPool) {
            switch (kind) {
                case SINGLETHREAD:
                    pools[SINGLETHREAD] = Executors.newSingleThreadExecutor();
                    break;
                case FIXEDTHREAD:
                    pools[FIXEDTHREAD] = Executors.newFixedThreadPool(nThreads[0]);
                    break;
                case CACHEDTHREAD:
                    pools[CACHEDTHREAD] = Executors.newCachedThreadPool();
                    break;
                case SCHEDULETHREAD:
                    pools[SCHEDULETHREAD] = Executors.newScheduledThreadPool(nThreads[0]);
                    break;
                default:
                    return null;
            }
        }

        return pool = pools[kind];
    }

    public static void execute(Runnable command) {
        pool.execute(command);
    }

    public static <T> Future<T> submit(Callable<T> task) {
        return pool.submit(task);
    }

    public static Future<?> submit(Runnable task) {
        return pool.submit(task);
    }

    public static <T> Future<T> submit(Runnable task, T result) {
        return pool.submit(task, result);
    }
}
