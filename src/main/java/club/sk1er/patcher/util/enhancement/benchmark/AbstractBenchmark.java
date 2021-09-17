package club.sk1er.patcher.util.enhancement.benchmark;

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
