package nl.hva.ict.ads;

import java.util.concurrent.TimeUnit;

public class Stopwatch {
    private final long startTime;

    public Stopwatch() {
        startTime = System.nanoTime();
    }

    /**
     * Calculates the time passed since object creation
     *
     * @return elapsed time
     */
    long elapsedTime() {
        long elapsedTime = System.nanoTime() - startTime;
        return TimeUnit.NANOSECONDS.toMillis(elapsedTime);
    }
}
