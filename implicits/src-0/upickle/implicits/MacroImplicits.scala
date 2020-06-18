package upickle.implicits

import scala.quoted._
import scala.deriving._
import scala.compiletime.{erasedValue, summonInline}

import upickle.core.{ObjVisitor, Visitor}
import scala.annotation.implicitNotFound

trait MacroImplicits{ self: upickle.core.Types =>
  implicit def macroSingletonR[T <: Singleton]: Reader[T] = ???
  implicit def macroSingletonW[T <: Singleton]: Writer[T] = ???
  implicit def macroSingletonRW[T <: Singleton]: ReadWriter[T] = ???
  def macroR[T]: Reader[T] = ???
  def macroRW[T]: ReadWriter[T] = ???
  def macroR0[T, M[_]]: Reader[T] = ???

  inline def macroW0[T](using m: Mirror.Of[T]): Writer[T] = inline m match {
    case s: Mirror.SumOf[T] => ??? // TODO: implement sum types
    case p: Mirror.ProductOf[T] =>
      new Writer[T]{
        def write0[V](out: Visitor[_, V], v: T): V = {
          val size: Int = valueOf[Tuple.Size[m.MirroredElemTypes]]
          val objVisitor: ObjVisitor[Any, V] = out.visitObject(size, -1).narrow

          Macros.tupleWrites[self.type, m.MirroredElemLabels, m.MirroredElemTypes](
            self,
            objVisitor,
            v.asInstanceOf[Product].productIterator
          )
          objVisitor.visitEnd(-1)
        }
      }
  }

}

object Macros {

  inline def tupleWrites[Api <: upickle.core.Types, Names <: Tuple, Types <: Tuple]
    (api: Api, out: ObjVisitor[Any, _], valueIterator: Iterator[Any]): Unit =
      inline (erasedValue[Names], erasedValue[Types]) match {
        case _: (EmptyTuple, EmptyTuple) => ()
        case _: (*:[n,ns], *:[t, ts]) =>
          val name: String = valueOf[n].toString
          val writer: api.Writer[t] = summonInline[api.Writer[t]]
          val value: t = valueIterator.next().asInstanceOf[t]

          val keyVisitor = out.visitKey(-1)
          out.visitKeyValue(keyVisitor.visitString(name, -1))
          out.visitValue(writer.write(out.subVisitor, value), -1)
          tupleWrites[Api, ns, ts](api, out, valueIterator)
      }

}
