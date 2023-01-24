package job;

import org.junit.Test;

public class JobJobManagerTest {

    private static int POOL_SIZE = 2;

    @Test
    public void test() throws Exception {
        JobJobManager pjs = new JobJobManager(POOL_SIZE);//, QUEUE_SIZE);
        pjs.startManager();

        pjs.scheduleJob(new OwmsOutputCdrJob("Job1", Job.PRIORITY_LOW));
        Thread.sleep(100);
        pjs.scheduleJob(new OwmsOutputCdrJob("Job2", Job.PRIORITY_LOW));
        Thread.sleep(100);
        pjs.scheduleJob(new OwmsOutputCdrJob("Job3", Job.PRIORITY_LOW));
        Thread.sleep(100);
        pjs.scheduleJob(new OwmsOutputCdrJob("Job4", Job.PRIORITY_LOW));
        Thread.sleep(100);
        pjs.scheduleJob(new OwmsOutputCdrJob("Job5", Job.PRIORITY_LOW));
        Thread.sleep(100);
        pjs.scheduleJob(new OwmsOutputCdrJob("Job6", Job.PRIORITY_HIGH));

        pjs.getJobList().all().forEach(job -> System.out.println(job));


        pjs.cancelJob("Job5");

        //Thread.sleep(1000);
        pjs.scheduleJob(new OwmsOutputCdrJob("Job7", Job.PRIORITY_LOW));
        pjs.scheduleJob(new OwmsOutputCdrJob("Job8", Job.PRIORITY_HIGH));
        pjs.scheduleJob(new OwmsOutputCdrJob("Job9", Job.PRIORITY_LOW));
        pjs.scheduleJob(new OwmsOutputCdrJob("Job10", Job.PRIORITY_HIGH));

        while(true) {
            try { Thread.sleep(5000); }catch (Exception e) {e.printStackTrace();}
            System.out.println();
            pjs.getJobList().all().forEach(job -> System.out.println(job));
        }
    }
}
