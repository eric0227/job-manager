import java.util.{Comparator, Optional}
import java.util.concurrent.locks.{Condition, ReentrantLock}

class PriorityBlockingJobList {
  private val list = scala.collection.mutable.ArrayBuffer[Job]()
  private val lock = new ReentrantLock
  private val notEmpty = lock.newCondition

  def size(f: Job => Boolean = _ => true): Int = list.count(f)
  def all: List[Job] = list.toList
  def add(job: Job): Boolean = {
    val lock = this.lock
    lock.lock()
    try {
      list.append(job)
      notEmpty.signal()
    } finally { lock.unlock() }
    true
  }

  def takeJob(): Option[Job] =
    list
      .filter(job => job.status == Job.STATUS_SUBMITTED)
      .sortWith((j1, j2) => j1.jobPriority > j2.jobPriority)
      .headOption

  @throws[InterruptedException]
  def take: Job = {
    //val lock: ReentrantLock = this.lock
    //lock.lockInterruptibly()
    var jobOpt: Option[Job] = None
    try {
      while (jobOpt.isEmpty) {
        //notEmpty.await()
        jobOpt = takeJob()
        try{jobOpt.foreach(_.status(Job.STATUS_TAKE))}catch{ case e: Throwable => e.printStackTrace()}
        Thread.sleep(100)
      }
    } finally { //lock.unlock()
    }
    jobOpt.get
  }

}
