import java.io.{BufferedReader, InputStreamReader}
import java.net.Socket

class OwmsOutputCdrJob(jobId: String, priority: String, submitTime: Long, timeout: Long) extends Job(jobId,priority,submitTime,timeout) {

  private var conn: Socket = _
  private var in: BufferedReader = _

  @Override
  def jobPriority = priority+":"+(Long.MaxValue - submitTime)

  def process() = {
    conn = new Socket("localhost", 8088)
    in = new BufferedReader(new InputStreamReader(conn.getInputStream))
    var line: String = null
    while (status == Job.STATUS_START && (line = in.readLine()) != null && !Thread.currentThread().isInterrupted())
      println(line)
  }

  override def close(): Unit = {
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
