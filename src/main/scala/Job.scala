import java.io.Closeable
import java.util.{Timer, TimerTask}
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

object Job {
  val PRIORITY_LOW = "00"
  val PRIORITY_HIGH = "01"

  val STATUS_SUBMITTED = "SUBMITTED"
  val STATUS_TAKE = "TAKE"
  val STATUS_RUNNING = "RUNNING"
  val STATUS_CANCELED = "CANCELED"
  val STATUS_SUCCESS = "SUCCESS"
  val STATUS_FAILED = "FAILED"
  val STATUS_TIMEOUT = "TIMEOUT"
}

case class JobStatus(jobId: String, priority: String, status: String)
case class JobInfo(jobId: String, priority: String, submitTime: Long, timeoutDuration: Long)
abstract class Job(val jobInfo: JobInfo) extends Runnable with Closeable {
  private var _status: String = Job.STATUS_SUBMITTED
  private val timer: Timer = new Timer(true)
  private val lock = new Object

  val jobId = jobInfo.jobId
  val priority= jobInfo.priority
  val submitTime = jobInfo.submitTime
  val timeoutDuration = jobInfo.timeoutDuration

  timer.schedule(new TimerTask() {
    def run(): Unit = {
      if(List(Job.STATUS_SUBMITTED, Job.STATUS_TAKE, Job.STATUS_RUNNING).contains(status))
        timeout()
    }
  }, timeoutDuration)

  def jobPriority: String

  val listeners = ArrayBuffer[JobListenerInterface]()
  def addListener(listener: JobListenerInterface) = listeners.append(listener)
  def publish[T >: JobEvent](event: T) = event match {
      case e: JobStartEvent => listeners.foreach(_.onJobStart(e))
      case e: JobSubmitEvent => listeners.foreach(_.onJobSubmit(e))
      case e: JobTakeEvent => listeners.foreach(_.onJobTake(e))
      case e: JobCancelEvent => listeners.foreach(_.onJobCancel(e))
      case e: JobTimeoutEvent => listeners.foreach(_.onJobTimeout(e))
      case e: JobEndEvent => listeners.foreach(_.onJobEnd(e))
      case e: JobStatusUpdate => listeners.foreach(_.onJobStatusUpdate(e))
  }

  def status: String = _status
  def status(after: String): Boolean = lock.synchronized {
    val before = _status
    val chk =
      if(after == Job.STATUS_TAKE && before == Job.STATUS_SUBMITTED) true
      else if(after == Job.STATUS_RUNNING && before == Job.STATUS_TAKE) true
      else if(after == Job.STATUS_CANCELED && List(Job.STATUS_SUBMITTED, Job.STATUS_TAKE, Job.STATUS_RUNNING).contains(before)) true
      else if(after == Job.STATUS_SUCCESS && before == Job.STATUS_RUNNING) true
      else if(after == Job.STATUS_FAILED && List(Job.STATUS_SUBMITTED, Job.STATUS_TAKE, Job.STATUS_RUNNING).contains(before)) true
      else if(after == Job.STATUS_TIMEOUT && List(Job.STATUS_SUBMITTED, Job.STATUS_TAKE, Job.STATUS_RUNNING).contains(before)) true
      else false

    if(chk) {
      _status = after
      publish(JobStatusUpdate(this, before, after))
      true
    } else false
  }

  override def run(): Unit = {
    if (status == Job.STATUS_TAKE && !Thread.currentThread().isInterrupted) {
      status(Job.STATUS_RUNNING)
      try {
        publish(JobStartEvent(this))

        process()

        status(Job.STATUS_SUCCESS)
        publish(JobEndEvent(this))
      } catch {
        case e: Throwable =>
          e.printStackTrace()
          status(Job.STATUS_FAILED)
          publish(JobFailEvent(this, e))
      } finally {
        close()
      }
    }
  }

  def process(): Unit

  def cancel() = lock.synchronized {
    close()
    status(Job.STATUS_CANCELED)
    publish(JobCancelEvent(this))
    //Thread.currentThread().interrupt()
  }
  def timeout() = lock.synchronized {
    close()
    status(Job.STATUS_TIMEOUT)
    publish(JobTimeoutEvent(this))
    Thread.currentThread().interrupt()
  }

  def jobStatus: JobStatus = JobStatus(jobId, priority, status)

  override def toString = s"Job($jobId, $priority, $submitTime, $timeoutDuration, $status)"
}
