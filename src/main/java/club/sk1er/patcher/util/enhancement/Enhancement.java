package club.sk1er.patcher.util.enhancement;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public interface Enhancement {

    AtomicInteger counter = new AtomicInteger(0);
    ThreadPoolExecutor POOL = new ThreadPoolExecutor(50, 50,
        0L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(),
        r -> new Thread(r, String.format("Patcher Concurrency Thread %s", counter.incrementAndGet())));

    String getName();

    default void tick() {
    }
}
