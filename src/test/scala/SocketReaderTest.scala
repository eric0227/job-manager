import org.scalatest.funsuite.AnyFunSuite

class SocketReaderTest extends AnyFunSuite {

  test("iter") {
    import Pipe._
    val pipe = new SocketReader()
    pipe
      .stream
      .zipWithIndex
      .foreachWriter(new Writer[(String, Int)]())
  }
}
