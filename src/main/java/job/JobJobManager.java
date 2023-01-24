package job;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class JobJobManager {
    private boolean isStarted = false;
    private ExecutorService priorityJobScheduler = Executors.newSingleThreadExecutor();
    private ExecutorService priorityJobPoolExecutor;
    private ExecutorService taskPoolExecutor;

    private PriorityBlockingList jobList;
    private int poolSize;

    private final ReentrantLock lock;
    private final Condition notEmpty;

    public JobJobManager(int poolSize) {
        this.poolSize = poolSize;
        priorityJobPoolExecutor = Executors.newFixedThreadPool(poolSize);
                //new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(poolSize));
        //taskPoolExecutor = Executors.newFixedThreadPool(poolSize);
        jobList = new PriorityBlockingList();
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
    }

    public void startManager() {
        isStarted = true;
        priorityJobScheduler.execute(() -> {
            ThreadPoolExecutor pool = (ThreadPoolExecutor)priorityJobPoolExecutor;
            while (isStarted) {
                try {
                    //priorityJobPoolExecutor.submit(takeJob());
                    if(pool.getActiveCount() < poolSize) priorityJobPoolExecutor.submit(takeJob());
                    else Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stopManager() {
        isStarted = false;
        priorityJobPoolExecutor.shutdown();
        priorityJobScheduler.shutdown();
        //Thread.currentThread().interrupt();
        // ..
    }

    public PriorityBlockingList getJobList() {
        return jobList;
    }

    public Job takeJob() throws InterruptedException {
        return jobList.take();
    }

    public Optional<Job> findJob(String jobId) {
        return jobList
                .all()
                .stream()
                .filter(job -> job.getJobId().equals(jobId))
                .findFirst();
    }

    public boolean cancelJob(String jobId) {
        return findJob(jobId)
                .map( job -> {
                    if(Arrays.asList(Job.JOB_STATUS_WAITING,Job.JOB_STATUS_TAKE, Job.JOB_STATUS_RUNNING).contains(job.getStatus())) job.cancel();
                    return true; })
                .orElse(false);
    }

    public void scheduleJob(Job job) {
        final ReentrantLock lock = this.lock;
        lock.lock();

        try {
            jobList.add(job);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }
}
