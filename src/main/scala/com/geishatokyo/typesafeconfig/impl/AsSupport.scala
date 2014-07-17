package com.geishatokyo.typesafeconfig.impl

import com.geishatokyo.typesafeconfig.{Env, TSConfig}
import scala.reflect.runtime.universe._
import com.typesafe.config.Config
import scala.collection.JavaConverters._
import scala.concurrent.duration.Duration
import java.util.Date

/**
 * Created by takezoux2 on 2014/06/13.
 */
trait AsSupport { self : TSConfig =>

  def config : Config
  def key : String

  protected def env : Env


  def as(tpe: Type)(implicit mirror: Mirror): Any = {

    if(tpe =:= typeOf[TSConfig]) return this

    def defaultValue() = {
      env.defaults.applyOrElse(tpe, (t : Type) => null)
    }

    if(!exists) defaultValue
    else{
      try{
        env.as(config,key).applyOrElse(tpe, (a : Type) => {
          defaultValue
        })
      }catch{
        case e : Throwable => {
          //e.printStackTrace()
          defaultValue
        }
      }
    }
  }
}
