package com.arhimondr.cputimers;

public class CpuTimers
{
  static {
    System.loadLibrary("cputimers");
  }

  public static native long getUserCpuTime();
}
