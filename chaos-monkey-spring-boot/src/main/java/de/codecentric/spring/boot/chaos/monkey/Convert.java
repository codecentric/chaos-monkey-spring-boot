package de.codecentric.spring.boot.chaos.monkey;

public class Convert {

    private static final int FACTOR = 1024;

    public static long toMegabytes(long bytes) {
        return bytes / FACTOR / FACTOR;
    }

    public static long toMegabytes(double bytes) {
        return toMegabytes((long) bytes);
    }

    public static int toBytes(int megabytes) {
        return megabytes * FACTOR * FACTOR;
    }
}
