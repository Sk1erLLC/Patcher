/*
 * Copyright © 2020 by Sk1er LLC
 *
 * All rights reserved.
 *
 * Sk1er LLC
 * 444 S Fulton Ave
 * Mount Vernon, NY
 * sk1er.club
 */

package club.sk1er.patcher.util.enhancement;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public interface Enhancement {

    // todo: use when false fixes koin being stupid
    AtomicInteger counter = new AtomicInteger(0);
    ThreadPoolExecutor POOL = new ThreadPoolExecutor(50, 50,
        0L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(),
        r -> new Thread(r, String.format("Patcher Concurrency Thread %s", counter.incrementAndGet())));

    String getName();

    default void tick() {
    }
}
