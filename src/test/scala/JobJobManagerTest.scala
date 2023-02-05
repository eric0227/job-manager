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
    m.addJob(OwmsOutputCdrJob("job01", Job.PRIORITY_LOW, 0L, 100000L))
    m.addJob(OwmsOutputCdrJob("job02", Job.PRIORITY_HIGH, 3L, 100000L))
    m.addJob(OwmsOutputCdrJob("job03", Job.PRIORITY_HIGH, 4L, 100000L))
    Thread.sleep(5000)
    m.addJob(OwmsOutputCdrJob("job04", Job.PRIORITY_HIGH, 4L, 100000L))
    Thread.sleep(10000)

    m.jobStatusList.foreach(println)
    m.stopManager()
  }

  test("cancel") {
    val m = new JobManager(2)
    m.startManager()
    //Thread.sleep(1000)
    m.addJob(OwmsOutputCdrJob("job01", Job.PRIORITY_LOW, 0L, 100000L))
    m.addJob(OwmsOutputCdrJob("job02", Job.PRIORITY_HIGH, 3L, 100000L))
    m.addJob(OwmsOutputCdrJob("job03", Job.PRIORITY_HIGH, 4L, 100000L))

    Thread.sleep(100)
    m.cancelJob("job01")
    m.cancelJob("job02")
    Thread.sleep(3000)

    m.stopManager()
    m.jobStatusList.foreach(println)
  }

  test("timeout") {
    val m = new JobManager(2)
    m.startManager()
    //Thread.sleep(1000)
    m.addJob(OwmsOutputCdrJob("job01", Job.PRIORITY_LOW, 0L, 1000L))
    m.addJob(OwmsOutputCdrJob("job02", Job.PRIORITY_HIGH, 3L, 2000L))
    m.addJob(OwmsOutputCdrJob("job03", Job.PRIORITY_HIGH, 4L, 100000L))
    Thread.sleep(10000)

    m.jobStatusList.foreach(println)
    m.stopManager()
  }
}