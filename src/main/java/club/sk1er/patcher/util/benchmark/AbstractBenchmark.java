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

import club.sk1er.patcher.util.chat.ChatUtilities;

public abstract class AbstractBenchmark {

    public void setup() {

    }

    public void warmUp() {

    }

    public void tearDown() {

    }

    protected void sendMessage(String message) {
        ChatUtilities.sendMessage(message, false);
    }

    public abstract BenchmarkResult[] benchmark(String[] args);
}
