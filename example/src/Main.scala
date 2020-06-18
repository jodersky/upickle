
import scala.deriving._

case class Bar(w: Double = 0.0, x: String = "", y: Int = -1, z: Boolean = false)
object Bar {

  given upickle.default.Writer[Bar] = upickle.default.macroW0
 
}

case class Foo(b: Bar = Bar())// derives upickle.default.Writer
object Foo {
  given upickle.default.Writer[Foo] = upickle.default.macroW0
}

@main def mail() = {
  val str = upickle.default.write(Foo(), 2)
  //upickle.implicits.ExtraMirror.gimme[Bar]
  println(str)
}
