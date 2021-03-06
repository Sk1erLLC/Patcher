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

    // todo: figure out what this is
    public int method(long l, int fps, int tps) {
        final double d = (double) l / (double) (1000000000L / tps);
        return (int) (d * (double) fps);
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
