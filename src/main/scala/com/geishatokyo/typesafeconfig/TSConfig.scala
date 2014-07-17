package com.geishatokyo.typesafeconfig

import scala.reflect._
import scala.reflect.runtime._
import scala.reflect.runtime.universe._
import java.util.Date
import scala.concurrent.duration.Duration

/**
 * Created by takezoux2 on 2014/06/13.
 */
trait TSConfig {


  def /(key : String) : TSConfig

  def ? = exists
  def exists : Boolean
  def keys : List[String]

  def as(t : Type)(implicit mirror : Mirror) : Any

  def as[T : TypeTag] : T = {
    val typeTag = implicitly[TypeTag[T]]
    implicit val mirror = typeTag.mirror
    as(typeOf[T]).asInstanceOf[T]
  }
  def get[T : TypeTag] : Option[T] = {
    if(exists) {
      as[Option[T]]
    }else None
  }

  def asList[T : TypeTag] : List[T] = as[List[T]]
  def asList : List[TSConfig]

  def asInt = as[Int]
  def asLong = as[Long]
  def asString = as[String]
  def asDouble = as[Double]
  def asBoolean = as[Boolean]

  def asDate = as[Date]
  def duration = as[Duration]


}




