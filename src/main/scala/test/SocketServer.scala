package test

import java.io.PrintStream
import java.net.ServerSocket

object SocketServer extends App {

  val server = new ServerSocket(8088)
  while (true) {
    try {
      val socket = server.accept()
      val out = socket.getOutputStream()
      val pout = new PrintStream(out)
      (1 to 100).foreach { n =>
        println(n)
        pout.println(n.toString)
        Thread.sleep(100)
      }
    } catch {
      case e => e.printStackTrace()
    }
  }
}
