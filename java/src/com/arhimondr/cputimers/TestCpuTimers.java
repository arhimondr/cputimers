package com.arhimondr.cputimers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class TestCpuTimers
{
    private static final ThreadMXBean THREAD_MX_BEAN = ManagementFactory.getThreadMXBean();

    private static int THREADS_COUNT = 10;

    /**
     * > make test
     *
     * Name: Main Thread, wallStart: 2214158412487, cpuStart: 93767075, userProcStart: 80000000, userTimerStart: 88000000
     * Name: Thread #1, wallStart: 2214163223703, cpuStart: 559652, userProcStart: 0, userTimerStart: 0
     * Name: Thread #0, wallStart: 2214163520718, cpuStart: 958472, userProcStart: 0, userTimerStart: 0
     * Name: Thread #2, wallStart: 2214163520712, cpuStart: 828451, userProcStart: 0, userTimerStart: 0
     * Name: Thread #6, wallStart: 2214163396599, cpuStart: 194548, userProcStart: 0, userTimerStart: 0
     * Name: Thread #5, wallStart: 2214163385549, cpuStart: 257684, userProcStart: 0, userTimerStart: 0
     * Name: Thread #3, wallStart: 2214163394875, cpuStart: 586885, userProcStart: 0, userTimerStart: 0
     * Name: Thread #4, wallStart: 2214163345709, cpuStart: 402337, userProcStart: 0, userTimerStart: 0
     * Name: Thread #7, wallStart: 2214163388125, cpuStart: 89415, userProcStart: 0, userTimerStart: 0
     * Name: Thread #9, wallStart: 2214165320418, cpuStart: 198167, userProcStart: 0, userTimerStart: 0
     * Name: Thread #8, wallStart: 2214163790122, cpuStart: 155966, userProcStart: 0, userTimerStart: 0
     * Name: Thread #8, wallEnd: 2216931111774, cpuEnd: 2749846512, userProcEnd: 30000000, userTimerEnd: 32000000
     * Name: Thread #8, wall: 2767321652, cpu: 2749690546, userProc: 30000000, userTimer: 32000000
     * Name: Thread #2, wallEnd: 2216992207690, cpuEnd: 2819409751, userProcEnd: 20000000, userTimerEnd: 28000000
     * Name: Thread #2, wall: 2828686978, cpu: 2818581300, userProc: 20000000, userTimer: 28000000
     * Name: Thread #5, wallEnd: 2217070703069, cpuEnd: 2878198886, userProcEnd: 60000000, userTimerEnd: 64000000
     * Name: Thread #5, wall: 2907317520, cpu: 2877941202, userProc: 60000000, userTimer: 64000000
     * Name: Thread #4, wallEnd: 2217126140825, cpuEnd: 2941355894, userProcEnd: 30000000, userTimerEnd: 36000000
     * Name: Thread #4, wall: 2962795116, cpu: 2940953557, userProc: 30000000, userTimer: 36000000
     * Name: Thread #1, wallEnd: 2217150320019, cpuEnd: 2945948530, userProcEnd: 40000000, userTimerEnd: 48000000
     * Name: Thread #1, wall: 2987096316, cpu: 2945388878, userProc: 40000000, userTimer: 48000000
     * Name: Thread #9, wallEnd: 2217155399313, cpuEnd: 2980927002, userProcEnd: 50000000, userTimerEnd: 56000000
     * Name: Thread #9, wall: 2990078895, cpu: 2980728835, userProc: 50000000, userTimer: 56000000
     * Name: Thread #6, wallEnd: 2217176997309, cpuEnd: 2997264811, userProcEnd: 70000000, userTimerEnd: 72000000
     * Name: Thread #6, wall: 3013600710, cpu: 2997070263, userProc: 70000000, userTimer: 72000000
     * Name: Thread #3, wallEnd: 2217184053858, cpuEnd: 3011410828, userProcEnd: 50000000, userTimerEnd: 52000000
     * Name: Thread #3, wall: 3020658983, cpu: 3010823943, userProc: 50000000, userTimer: 52000000
     * Name: Thread #0, wallEnd: 2217188673305, cpuEnd: 3017790274, userProcEnd: 50000000, userTimerEnd: 52000000
     * Name: Thread #0, wall: 3025152587, cpu: 3016831802, userProc: 50000000, userTimer: 52000000
     * Name: Thread #7, wallEnd: 2217197942535, cpuEnd: 3022329819, userProcEnd: 30000000, userTimerEnd: 36000000
     * Name: Thread #7, wall: 3034554410, cpu: 3022240404, userProc: 30000000, userTimer: 36000000
     * Name: Main Thread, wallEnd: 2217198208573, cpuEnd: 98342537, userProcEnd: 90000000, userTimerEnd: 92000000
     * Name: Main Thread, wall: 3039796086, cpu: 4575462, userProc: 10000000, userTimer: 4000000
     */
    public static void main(String[] args)
    {
        measure("Main Thread", () -> {

            Thread[] threads = new Thread[THREADS_COUNT];
            for (int i = 0; i < THREADS_COUNT; i++) {
                final int threadNumber = i;
                threads[i] = new Thread(() -> measure("Thread #" + threadNumber, TestCpuTimers::doStuff));
                threads[i].start();
            }

            for (int i = 0; i < THREADS_COUNT; i++) {
                try {
                    threads[i].join();
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static void measure(String name, Runnable runnable)
    {
        long wallStart = System.nanoTime();
        long cpuStart = THREAD_MX_BEAN.getCurrentThreadCpuTime();
        long userProcStart = THREAD_MX_BEAN.getCurrentThreadUserTime();
        long userTimerStart = CpuTimers.getUserCpuTime();
        System.out.printf(
                "Name: %s, wallStart: %s, cpuStart: %s, userProcStart: %s, userTimerStart: %s\n",
                name, wallStart, cpuStart, userProcStart, userTimerStart);

        runnable.run();

        long wallEnd = System.nanoTime();
        long cpuEnd = THREAD_MX_BEAN.getCurrentThreadCpuTime();
        long userProcEnd = THREAD_MX_BEAN.getCurrentThreadUserTime();
        long userTimerEnd = CpuTimers.getUserCpuTime();
        System.out.printf(
                "Name: %s, wallEnd: %s, cpuEnd: %s, userProcEnd: %s, userTimerEnd: %s\n",
                name, wallEnd, cpuEnd, userProcEnd, userTimerEnd);
        long userTimer = userTimerEnd - userTimerStart;
        long userProc = userProcEnd - userProcStart;
        long cpu = cpuEnd - cpuStart;
        long wall = wallEnd - wallStart;

        System.out.printf(
                "Name: %s, wall: %s, cpu: %s, userProc: %s, userTimer: %s\n",
                name, wall, cpu, userProc, userTimer);
    }

    private static void doStuff()
    {
        try {
            byte[] buffer = new byte[1024 * 1024];
            for (int i = 0; i < 10_000; i++) {
                try (InputStream output = new FileInputStream(new File("/proc/cpuinfo"))) {
                    while (true) {
                        if (output.read(buffer) == -1) {
                            break;
                        }
                    }
                }
            }
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }
}
