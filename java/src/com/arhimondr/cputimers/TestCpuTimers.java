package com.arhimondr.cputimers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class TestCpuTimers
{
    private static final ThreadMXBean THREAD_MX_BEAN = ManagementFactory.getThreadMXBean();

    /**
     * Sample output:
     *
     * wallStart: 17360264571786, cpuStart: 55112771, userProcStart: 50000000, userTimerStart: 52000000
     * wallEnd: 17362777623865, cpuEnd: 2546575937, userProcEnd: 1160000000, userTimerEnd: 1160000000
     * wall: 2513052079, cpu: 2491463166, userProc: 1110000000, userTimer: 1108000000
     */
    public static void main(String[] args)
            throws Exception
    {
        Thread t = new Thread(() -> {
            long wallStart = System.nanoTime();
            long cpuStart = THREAD_MX_BEAN.getCurrentThreadCpuTime();
            long userProcStart = THREAD_MX_BEAN.getCurrentThreadUserTime();
            long userTimerStart = CpuTimers.getUserCpuTime();
            System.out.printf(
                    "wallStart: %s, cpuStart: %s, userProcStart: %s, userTimerStart: %s\n",
                    wallStart, cpuStart, userProcStart, userTimerStart);
            doStuff();
            long wallEnd = System.nanoTime();
            long cpuEnd = THREAD_MX_BEAN.getCurrentThreadCpuTime();
            long userProcEnd = THREAD_MX_BEAN.getCurrentThreadUserTime();
            long userTimerEnd = CpuTimers.getUserCpuTime();
            System.out.printf(
                    "wallEnd: %s, cpuEnd: %s, userProcEnd: %s, userTimerEnd: %s\n",
                    wallEnd, cpuEnd, userProcEnd, userTimerEnd);
            long userTimer = userTimerEnd - userTimerStart;
            long userProc = userProcEnd - userProcStart;
            long cpu = cpuEnd - cpuStart;
            long wall = wallEnd - wallStart;

            System.out.printf(
                    "wall: %s, cpu: %s, userProc: %s, userTimer: %s\n",
                    wall, cpu, userProc, userTimer);
        });
        t.start();
        t.join();
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
