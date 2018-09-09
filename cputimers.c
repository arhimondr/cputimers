#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include <time.h>
#include <pthread.h>
#include <sys/types.h>
#include <unistd.h>
#include <sys/syscall.h>
#include "java/target/headers/com_arhimondr_cputimers_CpuTimers.h"

#define NANOSECS_PER_SEC 1000000000

// COPIED FROM glibc/sysdeps/unix/sysv/linux/kernel-posix-cpu-timers.h
#define CPUCLOCK_PID(clock)                ((pid_t) ~((clock) >> 3))
#define CPUCLOCK_PERTHREAD(clock) \
        (((clock) & (clockid_t) CPUCLOCK_PERTHREAD_MASK) != 0)
#define CPUCLOCK_PID_MASK        7
#define CPUCLOCK_PERTHREAD_MASK        4
#define CPUCLOCK_WHICH(clock)        ((clock) & (clockid_t) CPUCLOCK_CLOCK_MASK)
#define CPUCLOCK_CLOCK_MASK        3
#define CPUCLOCK_PROF                0
#define CPUCLOCK_VIRT                1
#define CPUCLOCK_SCHED                2
#define CPUCLOCK_MAX                3
#define MAKE_PROCESS_CPUCLOCK(pid, clock) \
        ((~(clockid_t) (pid) << 3) | (clockid_t) (clock))
#define MAKE_THREAD_CPUCLOCK(tid, clock) \
        MAKE_PROCESS_CPUCLOCK((tid), (clock) | CPUCLOCK_PERTHREAD_MASK)
// END COPIED FROM glibc/sysdeps/unix/sysv/linux/kernel-posix-cpu-timers.h

// COPIED FROM hotspot/src/os/linux/vm/os_linux.cpp
static jlong fast_thread_cpu_time(clockid_t clockid) {
  struct timespec tp;
  int rc = clock_gettime(clockid, &tp);
  if(rc!=0){
    fprintf(stderr, "fast_thread_cpu_time rc=%d\n", rc);
    exit(1);
  }
  return (tp.tv_sec * NANOSECS_PER_SEC) + tp.tv_nsec;
}
// END COPIED FROM hotspot/src/os/linux/vm/os_linux.cpp

static clockid_t thread_user_cpu_clockid() {
  // syscall is not required. the native tid can be decoded from the struct that pthread_t points to.
  // pthread_t can be obtained from the native data structure inside the JVM.
  // The JNI api doesn't have access to the native data structs, that's why the syscall is being made here.
  pid_t tid = syscall(__NR_gettid);
  clockid_t clockid = MAKE_THREAD_CPUCLOCK (tid, CPUCLOCK_VIRT);
  return clockid;
}

JNIEXPORT jlong JNICALL Java_com_arhimondr_cputimers_CpuTimers_getUserCpuTime
  (JNIEnv * env, jclass class) 
{
  return fast_thread_cpu_time(thread_user_cpu_clockid());
}
