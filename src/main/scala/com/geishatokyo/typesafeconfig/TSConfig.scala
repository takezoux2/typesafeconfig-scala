package com.geishatokyo.typesafeconfig

import scala.reflect._
import scala.reflect.runtime._
import scala.reflect.runtime.universe._

/**
 * Created by takezoux2 on 2014/06/13.
 */
trait TSConfig {

  def /(key : String) : TSConfig

  def ? = exists
  def exists : Boolean
  def keys : List[String]

  def as[T : TypeTag] : T
  def as(t : Type)(implicit mirror : Mirror) : Any
  def get[T : TypeTag] : Option[T]

  def asList[T : TypeTag] : List[T]
  def asList : List[TSConfig]

  def asInt = as[Int]
  def asLong = as[Long]
  def asString = as[String]
  def asDouble = as[Double]
  def asBoolean = as[Boolean]


}

object TSNone extends TSConfig{
  override def /(key: String): TSConfig = TSNone

  override def get[T: TypeTag]: Option[T] = None

  override def exists: Boolean = false
  override def keys = Nil

  override def asList[T: TypeTag]: List[T] = Nil
  override def as[T: TypeTag]: T = null.asInstanceOf[T]
  override def as(t : Type)(implicit mirror : Mirror) : Any = null.asInstanceOf[Any]

  override def asList: List[TSConfig] = Nil
}



