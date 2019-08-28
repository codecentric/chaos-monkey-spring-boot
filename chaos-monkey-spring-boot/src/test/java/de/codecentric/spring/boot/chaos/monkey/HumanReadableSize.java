package de.codecentric.spring.boot.chaos.monkey;

public class HumanReadableSize {

    public static long inMegabytes(long bytes) {
        return bytes / 1024 / 1024;
    }

    public static long inMegabytes(double bytes){
        return inMegabytes((long)bytes);
    }
}
