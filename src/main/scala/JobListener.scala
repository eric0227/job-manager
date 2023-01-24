
trait JobEvent

case class JobSubmitEvent(job: Job) extends JobEvent
case class JobTakeEvent(job: Job) extends JobEvent
case class JobStartEvent(job: Job) extends JobEvent
case class JobEndEvent(job: Job) extends JobEvent
case class JobCancelEvent(job: Job) extends JobEvent
case class JobFailEvent(job: Job, e: Throwable) extends JobEvent
case class JobTimeoutEvent(job: Job) extends JobEvent
case class JobStatusUpdate(job: Job,before: String, after: String)

trait JobListenerInterface {
  def onJobSubmit(event: JobSubmitEvent)
  def onJobTake(event: JobTakeEvent)
  def onJobStart(event: JobStartEvent)
  def onJobEnd(event: JobEndEvent)
  def onJobCancel(event: JobCancelEvent)
  def onJobFail(event: JobFailEvent)
  def onJobTimeout(event: JobTimeoutEvent)
  def onJobStatusUpdate(event: JobStatusUpdate)

}

abstract class AbstractJobListener extends JobListenerInterface {
  override def onJobSubmit(event: JobSubmitEvent): Unit = {}
  override def onJobTake(event: JobTakeEvent): Unit = {}
  override def onJobStart(event: JobStartEvent): Unit = {}
  override def onJobEnd(event: JobEndEvent): Unit = {}
  override def onJobCancel(event: JobCancelEvent): Unit = {}
  override def onJobFail(event: JobFailEvent): Unit = {}
  override def onJobTimeout(event: JobTimeoutEvent): Unit = {}
  override def onJobStatusUpdate(event: JobStatusUpdate): Unit = {}
}

class DisplayEventJobListener extends AbstractJobListener {
  override def onJobSubmit(event: JobSubmitEvent): Unit = println(event)
  override def onJobTake(event: JobTakeEvent): Unit = println(event)
  override def onJobStart(event: JobStartEvent): Unit = println(event)
  override def onJobEnd(event: JobEndEvent): Unit = println(event)
  override def onJobCancel(event: JobCancelEvent): Unit = println(event)
  override def onJobFail(event: JobFailEvent): Unit = println(event)
  override def onJobTimeout(event: JobTimeoutEvent): Unit = println(event)
  override def onJobStatusUpdate(event: JobStatusUpdate): Unit = println(event)
}
