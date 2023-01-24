import java.util.concurrent.{ExecutorService, Executors, Future, ThreadPoolExecutor}
import scala.collection.mutable

object JobManager {

}
class JobManager(poolSize: Int) {
  private var isStopped = true
  private val managerScheduler: ExecutorService = Executors.newSingleThreadExecutor
  private val jobPoolExecutor: ThreadPoolExecutor = Executors.newFixedThreadPool(poolSize).asInstanceOf[ThreadPoolExecutor]
  private val jobList = new PriorityBlockingJobList
  private final val fm = mutable.HashMap[String, Future[_]]()

  def startManager(): Unit = {
    isStopped = false
    managerScheduler.execute{() =>
      while(!isStopped || !Thread.currentThread().isInterrupted) {
        if (jobPoolExecutor.getActiveCount < poolSize) {
          val job = jobList.take
          fm.put(job.jobId, jobPoolExecutor.submit(job))
        }
        else Thread.sleep(100)
      }
    }
  }

  def stopManager() {
    isStopped = true
    managerScheduler.shutdown()
    jobPoolExecutor.shutdown()
    jobList
      .all
      .filter(job => List(Job.STATUS_SUBMITTED, Job.STATUS_TAKE, Job.STATUS_RUNNING).contains(job.status))
      .foreach(_.close())
  }

  def jobStatusList: List[JobStatus] = jobList.all.map(_.jobStatus)
  def getJob(jobId: String): Option[Job] = jobList.all.find(_.jobId == jobId)
  def addJob(job: Job) = jobList.add(job)
  def cancelJob(jobId: String) = {
    getJob(jobId).map(_.cancel())
    fm.get(jobId).foreach(_.cancel(true))
  }
}
