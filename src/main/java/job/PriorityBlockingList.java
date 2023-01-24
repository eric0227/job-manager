package job;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class PriorityBlockingList {
    private List<Job> list = new ArrayList();

    private final ReentrantLock lock;
    private final Condition notEmpty;

    public PriorityBlockingList() {
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
    }

    public List<Job> all() {
        return list;
    }

    public boolean add(Job job) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            list.add(job);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
        return true;
    }

    public List<Job> sortedJobList() {
        return list
                .stream()
                //.filter(job -> Job.JOB_STATUS_WAITING.equals(job.getStatus()))
                .sorted(Comparator.comparing(Job::getJobPriority).reversed())
                .collect(Collectors.toList());
    }

    public Optional<Job> getWaitingJob() {
        return list
                .stream()
                .filter(job -> Job.JOB_STATUS_WAITING.equals(job.getStatus()))
                .sorted(Comparator.comparing(Job::getJobPriority).reversed())
                .findFirst();
    }

    public Job take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        Optional<Job> jobOpt;
        try {
            while (!(jobOpt = getWaitingJob()).isPresent()) notEmpty.await();
        } finally {
            lock.unlock();
        }
        Job job = jobOpt.get();
        job.setStatus(Job.JOB_STATUS_TAKE);
        return job;
    }
}
