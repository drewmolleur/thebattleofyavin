package View;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Performance readout, printed every five seconds (every two with
 * -Dyavin.stats=true):
 *
 *   stats: game loop 60.0 fps (render 3.2 ms) | screen paints 59.5/s (1.1 ms) | background frames 24.5/s
 *
 * "game loop" is how often the game thread finishes a frame, "screen paints"
 * how often Swing actually puts one on screen, and "background frames" how
 * fast the animated GIF decoder delivers complete frames.
 */
public final class FrameStats {

    public static final boolean ENABLED = true;
    private static final double INTERVAL_SECONDS = Boolean.getBoolean("yavin.stats") ? 2.0 : 5.0;

    private static final AtomicLong renders = new AtomicLong();
    private static final AtomicLong renderNanos = new AtomicLong();
    private static final AtomicLong paints = new AtomicLong();
    private static final AtomicLong paintNanos = new AtomicLong();
    private static final AtomicLong backgroundFrames = new AtomicLong();

    private FrameStats() {
    }

    private static long lastStallReport;

    /**
     * Reports something that took long enough to be felt as a hitch. Always
     * printed (at most a few times a second) so a stall can be traced without
     * enabling the full readout.
     */
    public static synchronized void stall(String what) {
        long now = System.currentTimeMillis();
        if (now - lastStallReport < 250) {
            return;
        }
        lastStallReport = now;
        System.out.println("stall: " + what);
    }

    static void renderDone(long nanos) {
        if (ENABLED) {
            renders.incrementAndGet();
            renderNanos.addAndGet(nanos);
        }
    }

    static void paintDone(long nanos) {
        if (ENABLED) {
            paints.incrementAndGet();
            paintNanos.addAndGet(nanos);
        }
    }

    static void backgroundFrameDone() {
        if (ENABLED) {
            backgroundFrames.incrementAndGet();
        }
    }

    static {
        if (ENABLED) {
            Thread reporter = new Thread(FrameStats::reportLoop, "frame-stats");
            reporter.setDaemon(true);
            reporter.start();
        }
    }

    private static void reportLoop() {
        final double intervalSeconds = INTERVAL_SECONDS;
        while (true) {
            try {
                Thread.sleep((long) (intervalSeconds * 1000));
            } catch (InterruptedException e) {
                return;
            }
            long r = renders.getAndSet(0), rn = renderNanos.getAndSet(0);
            long p = paints.getAndSet(0), pn = paintNanos.getAndSet(0);
            long b = backgroundFrames.getAndSet(0);
            System.out.printf("stats: game loop %.1f fps (render %.1f ms) | screen paints %.1f/s (%.1f ms) | background frames %.1f/s%n",
                    r / intervalSeconds, r == 0 ? 0 : rn / 1e6 / r,
                    p / intervalSeconds, p == 0 ? 0 : pn / 1e6 / p,
                    b / intervalSeconds);
        }
    }
}
