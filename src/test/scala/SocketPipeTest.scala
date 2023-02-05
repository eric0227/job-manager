import org.scalatest.funsuite.AnyFunSuite

class SocketPipeTest extends AnyFunSuite {

  test("iter") {
    val writer = new Writer[String]()
    val pipe = new SocketPipe()
    pipe
      .stream
      .zipWithIndex
      .foreach(println)
  }
}
