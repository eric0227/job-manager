import java.io.{BufferedReader, InputStreamReader}
import java.net.Socket

object OwmsOutputCdrJob {
  def apply(jobId: String, priority: String, submitTime: Long, timeoutDuration: Long) = {
    new OwmsOutputCdrJob(JobInfo(jobId, priority, submitTime, timeoutDuration))
  }
}

class OwmsOutputCdrJob(jobInfo: JobInfo) extends Job(jobInfo) {

  private var conn: Socket = _
  private var in: BufferedReader = _

  @Override
  def jobPriority = priority+":"+(Long.MaxValue - submitTime)

  def process() = {
    conn = new Socket("localhost", 8088)
    in = new BufferedReader(new InputStreamReader(conn.getInputStream))
    var stop: Boolean = false
    while (status == Job.STATUS_RUNNING && !stop && !Thread.currentThread().isInterrupted()) {
      val line = in.readLine()
      println(line)
      if(line == null) stop = true
    }
    close()
  }

  override def close(): Unit = synchronized {
    if(in != null) in.close()
    if(conn != null) conn.close()
    in = null
    conn = null
  }

  addListener(new DisplayEventJobListener())
  addListener(new AbstractJobListener {
    override def onJobSubmit(event: JobSubmitEvent): Unit = {
    }

    override def onJobTake(event: JobTakeEvent): Unit = {
    }

    override def onJobStart(event: JobStartEvent): Unit = {
    }

    override def onJobEnd(event: JobEndEvent): Unit = {
    }

    override def onJobCancel(event: JobCancelEvent): Unit = {
    }

    override def onJobFail(event: JobFailEvent): Unit = {
    }

    override def onJobTimeout(event: JobTimeoutEvent): Unit = {
    }
  })

  override def toString: String = super.toString
}
