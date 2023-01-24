package test

import java.io.PrintStream
import java.net.ServerSocket
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object SocketServer extends App {

  val server = new ServerSocket(8088)
  while (true) {
    try {
      val socket = server.accept()
      Future {
        val out = socket.getOutputStream()
        val pout = new PrintStream(out)
        (1 to 100).foreach { n =>
          println(n)
          pout.println(n.toString)
          Thread.sleep(100)
        }
        pout.close()
        socket.close()
      }
    } catch {
      case e => e.printStackTrace()
    } finally {
    }
  }
}
