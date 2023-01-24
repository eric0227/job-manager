package job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

public class OwmsOutputCdrJob extends Job {

    private Connection conn;  // set Connection
    private PreparedStatement pstmt; // set PreparedStatement

    public OwmsOutputCdrJob(String jobName, String priority) {
        super(jobName, priority, System.currentTimeMillis());
    }

    public OwmsOutputCdrJob(String jobName, String priority, long timestamp) {
        super(jobName, priority, timestamp);
    }

    public OwmsOutputCdrJob(String jobName, String priority, long timestamp, int timeout) {
        super(jobName, priority, timestamp, timeout);
    }

    @Override
    public void run() {
        System.out.println(new Date() + " " + super.toString() + " START..");
        try {
            setStatus(Job.JOB_STATUS_RUNNING);
            //pstmt = conn.prepareStatement("");
            //pstmt.executeQuery();
            Thread.sleep(5000); // to simulate actual execution time
        } catch (Exception e) {
            setStatus(Job.JOB_STATUS_FAILED);
            throw new RuntimeException(e);
        } finally {
            if(pstmt != null) { try { pstmt.close();} catch (SQLException e) { throw new RuntimeException(e); } }
            if(conn != null) { try { conn.close();} catch (SQLException e) { throw new RuntimeException(e); } }
        }
        setStatus(Job.JOB_STATUS_DONE);
        System.out.println(new Date() + " " + super.toString() + " END..");
    }

    @Override
    public void close() {
        Thread.currentThread().interrupt();
        if(pstmt != null) { try { pstmt.close();} catch (SQLException e) { throw new RuntimeException(e); } }
        if(conn != null) { try { conn.close();} catch (SQLException e) { throw new RuntimeException(e); } }
        setStatus(Job.JOB_STATUS_CANCELED);
        // ..
    }
}
