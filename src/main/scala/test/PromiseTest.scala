package test

import java.io.{BufferedInputStream, BufferedReader}
import java.net.Socket
import java.util.{Timer, TimerTask}
import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue, TimeoutException}
import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object PromiseTest extends App {

  def cancellable[T](f: Future[T], timeout: Duration)(fn: String => Unit): (() => Unit, Future[T]) = {
    var canceled = false
    val p = Promise[T]
    val first = Future.firstCompletedOf(Seq(p.future, f))

    val timerTask = new TimerTask() {
      def run(): Unit = {
        fn("timeout")
        p.failure(new TimeoutException())
      }
    }

    val cancellation: () => Unit = { () =>
        canceled = true
        timerTask.cancel()

        first.failed.foreach { case e => e.printStackTrace();  fn("cancel") }
        p.failure(new Exception)
    }
    val timer: Timer = new Timer(true)
    timer.schedule(timerTask, timeout.toMillis)
    f
      .map { a => if(canceled || p.trySuccess(a)) timerTask.cancel() else true }
      .recover { case e: Exception => if (canceled || p.tryFailure(e)) timerTask.cancel() else true }
    (cancellation, first)
  }

  var socket: Socket = _
  val ff = Future {
    //Thread.sleep(5000)
    socket = new Socket("localhost", 8088)
    val in = new BufferedInputStream(socket.getInputStream)
    var i: Int = -1
    while((i = in.read()) != -1) println(i)

    "TTT"
  }
  val (cancel, f1) = cancellable(ff, Duration("4 seconds")) { status =>
    println(status)
    socket.close()
  }

  val cancelF = Future {
    Thread.sleep(6000)
    cancel()
  }

  f1.foreach(println)

  Thread.sleep(10000)
}
