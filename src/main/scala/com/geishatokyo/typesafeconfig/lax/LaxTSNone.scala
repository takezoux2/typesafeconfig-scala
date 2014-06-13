package com.geishatokyo.typesafeconfig.lax

import scala.reflect.runtime.universe._
import com.geishatokyo.typesafeconfig.TSConfig

/**
 * Created by takezoux2 on 2014/06/13.
 */
object LaxTSNone extends LaxTSConfig {


  def /(key : String) : TSConfig = LaxTSNone

  def exists : Boolean = false
  def keys : List[String] = Nil

  override def as[T : TypeTag] = {
    val tt = implicitly[TypeTag[T]]
    as(tt.tpe)(tt.mirror).asInstanceOf[T]
  }

  def as(tpe : Type)(implicit mirror : Mirror) : Any = {
    tpe match {
      case t if t =:= typeOf[Int] => LaxDefaults.int
      case t if t =:= typeOf[Long] => LaxDefaults.long
      case t if t =:= typeOf[Double] => LaxDefaults.double
      case t if t =:= typeOf[Boolean] => LaxDefaults.boolean
      case t if t =:= typeOf[String] => LaxDefaults.string
      case t if t <:< typeOf[Option[_]] => None
      case t if t <:< typeOf[List[_]] => Nil
      case t if t <:< typeOf[Seq[_]] => Seq()
      case t if t <:< typeOf[Set[_]] => Set()
      case t if t <:< typeOf[Map[_,_]] => Map.empty
      case _ => {
        null
      }
    }
  }
  def get[T : TypeTag] : Option[T] = None
  def asList[T : TypeTag] : List[T] = Nil
  def asList : List[TSConfig] = Nil
}
