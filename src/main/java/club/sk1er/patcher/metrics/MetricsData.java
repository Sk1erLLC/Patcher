package club.sk1er.patcher.metrics;

@SuppressWarnings("unused")
public class MetricsData {
    private final long[] samples = new long[240];
    private int startIndex;
    private int sampleCount;
    private int writeIndex;

    public void pushSample(long time) {
        this.samples[this.writeIndex] = time;
        ++this.writeIndex;
        if (this.writeIndex == 240) {
            this.writeIndex = 0;
        }

        if (this.sampleCount < 240) {
            this.startIndex = 0;
            ++this.sampleCount;
        } else {
            this.startIndex = this.wrapIndex(this.writeIndex + 1);
        }
    }

    public int scaleSampleTo(long time, int multiplier, int counter) {
        final double sample = (double) time / (double) (1000000000L / counter);
        return (int) (sample * (double) multiplier);
    }

    public int wrapIndex(int index) {
        return index % 240;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getWriteIndex() {
        return writeIndex;
    }

    public long[] getSamples() {
        return samples;
    }
}
