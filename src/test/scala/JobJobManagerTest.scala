import org.scalatest.funsuite.AnyFunSuite

class JobJobManagerTest extends AnyFunSuite {

  test("startManager -> stopManager") {
    val m = new JobManager(2)
    m.startManager()
    Thread.sleep(10000)
    m.stopManager()
  }

  test("addJob") {
    val m = new JobManager(2)
    m.startManager()
    //Thread.sleep(1000)
    m.addJob(new OwmsOutputCdrJob("job01", Job.PRIORITY_LOW, 0L, 100000L))
    m.addJob(new OwmsOutputCdrJob("job02", Job.PRIORITY_HIGH, 3L, 100000L))
    m.addJob(new OwmsOutputCdrJob("job03", Job.PRIORITY_HIGH, 4L, 100000L))
    Thread.sleep(10000)
    m.addJob(new OwmsOutputCdrJob("job04", Job.PRIORITY_HIGH, 4L, 100000L))
    Thread.sleep(50000)
    m.stopManager()
  }

  test("cancel") {
    val m = new JobManager(2)
    m.startManager()
    //Thread.sleep(1000)
    m.addJob(new OwmsOutputCdrJob("job01", Job.PRIORITY_LOW, 0L, 100000L))
    //m.addJob(new OwmsOutputCdrJob("job02", Job.PRIORITY_HIGH, 3L, 100000L))
    //m.addJob(new OwmsOutputCdrJob("job03", Job.PRIORITY_HIGH, 4L, 100000L))

    Thread.sleep(100)
    m.cancelJob("job01")
    //m.cancelJob("job02")
    //m.cancelJob("job03")
    Thread.sleep(1000)
    m.cancelJob("job01")

    Thread.sleep(50000)

    m.stopManager()
  }

}


