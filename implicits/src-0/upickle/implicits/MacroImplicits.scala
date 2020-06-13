package upickle.implicits

trait MacroImplicits{ this: upickle.core.Types =>
  implicit def macroSingletonR[T <: Singleton]: Reader[T] = ???
  implicit def macroSingletonW[T <: Singleton]: Writer[T] = ???
  implicit def macroSingletonRW[T <: Singleton]: ReadWriter[T] = ???
  def macroR[T]: Reader[T] = ???
  def macroW[T]: Writer[T] = ???
  def macroRW[T]: ReadWriter[T] = ???
  def macroR0[T, M[_]]: Reader[T] = ???
  def macroW0[T, M[_]]: Writer[T] = ???
}

