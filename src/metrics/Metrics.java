package metrics;

public class Metrics {
    private int operationCount;
    private long startTime;

    public Metrics() {
        this.operationCount = 0;
    }

    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    public long stopTimer() {
        return System.nanoTime() - startTime;
    }

    public void incrementOperation() {
        operationCount++;
    }

    public void incrementOperation(int count) {
        operationCount += count;
    }

    public int getOperationCount() {
        return operationCount;
    }

    public void reset() {
        operationCount = 0;
    }
}