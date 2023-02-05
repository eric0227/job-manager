import java.io.{BufferedReader, InputStreamReader}
import java.net.Socket
import scala.collection.{AbstractIterable, AbstractIterator}
import java.lang.String


class Writer[T] {
  def open() = {
    println("open")
  }

  def process(d: T) = {
    println(d)
  }

  def close(): Unit = {
    println("close")
  }
}

class SocketPipe {
  private var conn: Socket = _
  private var in: BufferedReader = _

  conn = new Socket("localhost", 8088)
  in = new BufferedReader(new InputStreamReader(conn.getInputStream))

  val stream: Iterator[String] = new Iterator[String] {
    var line: java.lang.String = _
    println("start.")
    override def hasNext: Boolean = {
      line = in.readLine()
      if(line != null) true
      else {
        println("end.")
        false
      }
    }

    override def next(): String = {
      println("process.")
      line
    }
  }

  def close() = {
    in.close()
    conn.close()
  }
}
