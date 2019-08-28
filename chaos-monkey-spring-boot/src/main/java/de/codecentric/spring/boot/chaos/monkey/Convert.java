package de.codecentric.spring.boot.chaos.monkey;

public class Convert {

    public static long toMegabytes(long bytes) {
        return bytes / 1024 / 1024;
    }

    public static long toMegabytes(double bytes){
        return toMegabytes((long)bytes);
    }

    public static int toBytes(int megabytes) {
        return megabytes * 1024 * 1024;
    }
}
