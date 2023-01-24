package job;

import java.io.Closeable;
import java.io.IOException;

public abstract class Job implements Runnable, Closeable {

    public static final String PRIORITY_LOW = "00";
    public static final String PRIORITY_HIGH = "01";

    public static final String JOB_STATUS_WAITING = "WAITING";
    public static final String JOB_STATUS_TAKE = "TAKE";
    public static final String JOB_STATUS_RUNNING = "RUNNING";
    public static final String JOB_STATUS_CANCELED = "CANCELED";
    public static final String JOB_STATUS_DONE = "DONE";
    public static final String JOB_STATUS_FAILED = "FAILED";
    public static final String JOB_STATUS_TIMEOUT = "TIMEOUT";

    private String jobId;
    private String priority;
    private long timestamp;
    private int timeout = 0; // seconds
    private String status;

    public Job(String jobId, String priority, long timestamp) {
        this.jobId = jobId;
        this.priority = priority;
        this.timestamp = timestamp;
        this.status = JOB_STATUS_WAITING;
    }

    public Job(String jobId, String priority, long timestamp, int timeout) {
        this.jobId = jobId;
        this.priority = priority;
        this.timestamp = timestamp;
        this.status = JOB_STATUS_WAITING;
        this.timeout = timeout;
    }

    public String getJobId() {
        return jobId;
    }

    public String getPriority() {
        return priority;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getJobPriority() {
        return priority + "_" + timestamp;
    }

    public String getStatus() {
        return status;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean cancel() {
        return stop();
    }

    public boolean stop() {
        try {
            close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String toString() {
        return "Job{" +
                "jobId='" + jobId + '\'' +
                ", priority='" + priority + '\'' +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                '}';
    }
}
