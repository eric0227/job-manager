package ml

object M {

  trait Functor[M[_]] {
    def map[U, V](m: M[U])(f: U => V): M[V]
  }

  type Hom[T] = {
    type Right[X] = Function1[X, T]
    type Left[X] = Function1[T, X]
  }

  trait CoFunctor[M[_]] {
    def map[U, V](m: M[U])(f: V => U): M[V]
  }

  trait ObsFunctor[T] extends Functor[(Hom[T])#Right] {
    self =>
      def map[U, V](vu: Function1[U, T])(f: V => U): Function1[V, T] = f.andThen(vu)
  }
}
