package com.geishatokyo.typesafeconfig

import scala.reflect.runtime.universe._
import com.geishatokyo.typesafeconfig.impl.TSNone
import com.typesafe.config.Config

/**
 * Created by takezoux2 on 2014/07/17.
 */
trait Env {

  def defaults : PartialFunction[Type,Any]
  def as(config : Config,key : String)(implicit mirror: Mirror) : PartialFunction[Type,Any]

  lazy val none : TSConfig = new TSNone(this)

  def +(env : Env) : Env = {
    //後から追加したほうが優先されるベキなので順序を入れ替え
    new AggEnv(env,this)
  }

}

class AggEnv(e1 : Env,e2 : Env) extends Env{
  def defaults : PartialFunction[Type,Any] = e1.defaults orElse e2.defaults
  def as(config : Config,key : String)(implicit mirror: Mirror) : PartialFunction[Type,Any] = e1.as(config,key) orElse e2.as(config,key)
}

class NoneEnv extends Env{
  def defaults : PartialFunction[Type,Any] = PartialFunction.empty
  def as(config : Config,key : String)(implicit mirror: Mirror) : PartialFunction[Type,Any] = PartialFunction.empty

}

object NoneEnv extends NoneEnv