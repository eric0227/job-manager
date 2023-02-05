import java.io.{BufferedReader, InputStreamReader}
import java.net.Socket
import scala.collection.{AbstractIterable, AbstractIterator}
import java.lang.String

object Pipe {

  class Writer[T] {
    def open() = {
      println("writer open")
    }

    def process(d: T) = {
      println(d)
    }

    def close(): Unit = {
      println("writer close")
    }
  }

  implicit class PipeIterator[T](iter: Iterator[T]) {
    def foreachWriter(writer: Writer[T]) = {
      writer.open()
      iter.foreach(d => writer.process(d))
      writer.close()
    }
  }
}

abstract class Reader {
  def open()
  def stream: Iterator[String]
  def close()
}

class SocketReader extends Reader {
  import Pipe._
  private var conn: Socket = _
  private var in: BufferedReader = _

  override def open() = {
    println("reader open")
    conn = new Socket("localhost", 8088)
    in = new BufferedReader(new InputStreamReader(conn.getInputStream))
  }

  val stream: Iterator[String] = new Iterator[String] {
    var index: Int = 0
    var line: java.lang.String = _
    override def hasNext: Boolean = {
      if(index == 0) open()
      line = in.readLine()
      if(line != null) true
      else {
        close()
        false
      }
    }
    override def next(): String = {
      index = index + 1
      line
    }
  }

  override def close() = {
    println("reader close")
    in.close()
    conn.close()
  }
}
