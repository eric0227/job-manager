import java.io.Closeable
import java.util.{Timer, TimerTask}
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

object Job {
  val PRIORITY_LOW = "00"
  val PRIORITY_HIGH = "01"

  val STATUS_SUBMITTED = "01"
  val STATUS_TAKE = "02"
  val STATUS_START = "03"
  val STATUS_CANCELED = "04"
  val STATUS_END = "05"
  val STATUS_FAILED = "06"
  val STATUS_TIMEOUT = "07"
}

abstract class Job(val jobId: String, val priority: String, val submitTime: Long, val timeoutDuration: Long) extends Runnable with Closeable {
  private var _status: String = Job.STATUS_SUBMITTED
  private val timer: Timer = new Timer(true)
  private var f: Future[Unit] = _
  private val lock = new Object

  timer.schedule(new TimerTask() {
    def run(): Unit = {
      if(List(Job.STATUS_SUBMITTED, Job.STATUS_TAKE, Job.STATUS_START).contains(status))
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
      else if(after == Job.STATUS_START && before == Job.STATUS_TAKE) true
      else if(after == Job.STATUS_CANCELED && List(Job.STATUS_SUBMITTED, Job.STATUS_TAKE, Job.STATUS_START).contains(before)) true
      else if(after == Job.STATUS_END && before == Job.STATUS_START) true
      else if(after == Job.STATUS_FAILED && List(Job.STATUS_SUBMITTED, Job.STATUS_TAKE, Job.STATUS_START).contains(before)) true
      else if(after == Job.STATUS_TIMEOUT && List(Job.STATUS_SUBMITTED, Job.STATUS_TAKE, Job.STATUS_START).contains(before)) true
      else false

    if(chk) {
      _status = after
      publish(JobStatusUpdate(this, before, after))
      true
    } else false
  }

  override def run(): Unit = {
    if (status == Job.STATUS_TAKE && !Thread.currentThread().isInterrupted) {
      status(Job.STATUS_START)
      try {
        publish(JobStartEvent(this))

        process()

        status(Job.STATUS_END)
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

  override def toString = s"Job($jobId, $priority, $submitTime, $timeoutDuration, $status)"
}
