package com.geishatokyo.typesafeconfig.impl

import scala.reflect.runtime.universe._
import com.geishatokyo.typesafeconfig.{Env, TSConfig}
import scala.concurrent.duration.Duration
import java.util.Date

/**
 * Created by takezoux2 on 2014/06/13.
 */
class TSNone(env : Env) extends TSConfig {


  def /(key : String) : TSConfig = this

  def exists : Boolean = false
  def keys : List[String] = Nil


  def as(tpe : Type)(implicit mirror : Mirror) : Any = {
    env.defaults.applyOrElse(tpe,(t : Type) => null)
  }
  def asList : List[TSConfig] = Nil
}
