import java.io.Closeable
import java.util.concurrent.TimeoutException
import java.util.{Timer, TimerTask}
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Future, Promise}

object JobBack {
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
/*
abstract class JobBack(val jobId: String, val priority: String, val submitTime: Long, val timeoutDuration: Long) extends Runnable with Closeable {
  private var _status: String = Job.STATUS_SUBMITTED
  private val timer: Timer = new Timer(true)
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
  def status(s: String) = {
    val before = _status
    _status = s
    publish(JobStatusUpdate(before, s))
  }
  def cancel() = {
    close()
    publish(JobCancelEvent())
    Thread.currentThread().interrupt()
  }
  def timeout() = {
    close()
    publish(JobTimeoutEvent())
    Thread.currentThread().interrupt()
  }

  def cancellable[T](f: Future[T], timeout: Duration): (() => Unit, Future[T]) = {
    var canceled = false
    val p = Promise[T]
    val first = Future.firstCompletedOf(Seq(p.future, f))

    val timerTask = new TimerTask() {
      def run(): Unit = {
        publish(JobTimeoutEvent())
        p.failure(new TimeoutException())
      }
    }

    val cancellation: () => Unit = { () =>
      canceled = true
      timerTask.cancel()

      first.failed.foreach { case e =>
        e.printStackTrace()
        publish(JobCancelEvent())
      }
      p.failure(new Exception)
    }
    val timer: Timer = new Timer(true)
    timer.schedule(timerTask, timeout.toMillis)
    f
      .map { a => if (canceled || p.trySuccess(a)) timerTask.cancel() else true }
      .recover { case e: Exception => if (canceled || p.tryFailure(e)) timerTask.cancel() else true }
    (cancellation, first)
  }

  override def toString = s"Job($jobId, $priority, $submitTime, $timeoutDuration, $status)"
}
*/