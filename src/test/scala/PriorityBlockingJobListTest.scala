import job.PriorityBlockingList
import org.scalatest.funsuite.AnyFunSuite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

class PriorityBlockingJobListTest extends AnyFunSuite {

  test("jobTake") {

    val list = new PriorityBlockingJobList()
    list.add(OwmsOutputCdrJob("job01", Job.PRIORITY_LOW, 0L, 100000L))
    list.add(OwmsOutputCdrJob("job02", Job.PRIORITY_HIGH, 3L, 100000L))
    list.add(OwmsOutputCdrJob("job03", Job.PRIORITY_HIGH, 4L, 100000L))

    Future {
      Thread.sleep(1200)
      list.add(OwmsOutputCdrJob("job04", Job.PRIORITY_HIGH, 5L, 100L))
    }

    while(list.size(_.status == Job.STATUS_SUBMITTED) >= 0) {
      list
        .takeJob()
        .foreach { job =>
          try{Thread.sleep(200)} catch {case e =>}
          //job.status(Job.STATUS_CANCELED)
          job.cancel()
          println(job)
        }
    }
  }
}
