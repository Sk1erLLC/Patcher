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

package club.sk1er.patcher.util.benchmark;

public class BenchmarkResult {

    private final long deltaTime;
    private final long iterations;
    private final String name;

    public BenchmarkResult(long deltaTime, long iterations, String name) {
        this.deltaTime = deltaTime;
        this.iterations = iterations;
        this.name = name;
    }

    public long getDeltaTime() {
        return deltaTime;
    }

    public long getIterations() {
        return iterations;
    }

    public String getName() {
        return name;
    }
}
