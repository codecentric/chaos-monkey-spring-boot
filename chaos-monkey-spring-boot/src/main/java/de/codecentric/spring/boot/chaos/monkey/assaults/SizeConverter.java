package de.codecentric.spring.boot.chaos.monkey.assaults;

class SizeConverter {

  private static final int FACTOR = 1024;

  static long toMegabytes(long bytes) {
    return bytes / FACTOR / FACTOR;
  }

  static long toMegabytes(double bytes) {
    return toMegabytes((long) bytes);
  }

  static int toBytes(int megabytes) {
    return megabytes * FACTOR * FACTOR;
  }
}
