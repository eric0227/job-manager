package concurrent

import org.scalatest.funsuite.AnyFunSuite

class Ch01 extends AnyFunSuite {
  test("compose") {
    def compose[A, B, C](g: B => C, f: A => B): A => C = x => g(f(x))
    val h = compose((b: Int) => b + 1, (a: Int) => a * 10)
    println(h(10))
  }

  test("fuse") {
    def fuse[A, B](a: Option[A], b:Option[B]): Option[(A, B)] = a.flatMap( _a => b.map((_a, _)))
    println(fuse(Some(10), None))
    println(fuse(Some(10), Some(20)))
  }

  test("check") {
    def check[T](xs: Seq[T])(pred: T => Boolean): Boolean = xs.exists(x => pred(x))
    println(check(1 until 10) { n =>
      40 / n > 0
    })
  }

 test("list") {
   val list = List(1,2)
   val iter = list.iterator
   iter
 }
}
