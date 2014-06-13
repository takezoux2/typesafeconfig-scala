package com.geishatokyo.typesafeconfig.lax

import com.geishatokyo.typesafeconfig.{TSNone, TSConfig}
import com.typesafe.config.Config
import scala.reflect._
import scala.reflect.runtime._
import scala.reflect.runtime.universe._
import scala.collection.JavaConverters._

/**
 * 極力例外を発生させない実装
 * Keyが無い場合は、LaxDefaultsの値が使用される
 * Created by takezoux2 on 2014/06/13.
 */
trait LaxTSConfig extends TSConfig {

}

case class LaxTSConfigWithKey(config : Config,key : String) extends LaxTSConfig with AsSupport{

  def keys = {
    if(exists) {
      config.getConfig(key).entrySet().asScala.map(es => {
        val k = es.getKey
        val i = k.indexOf(".")
        if(i > 0){
          k.substring(0,i)
        }else{
          k
        }
      }).toList.distinct
    } else Nil
  }

  override def /(key: String): TSConfig = {
    if(exists){
      LaxTSConfigWithKey(config.getConfig(this.key),key)
    }else{
      TSNone
    }
  }

  override def exists: Boolean = {
    config.hasPath(key)
  }

  override def as[T: universe.TypeTag]: T = {
    get[T].getOrElse(null.asInstanceOf[T])
  }
  override def asList[T: TypeTag]: List[T] = {
    asList.map(c => {
      c.as[T]
    })
  }

  override def asList: List[TSConfig] = {
    if(exists){
      config.getConfigList(key).asScala.toList.map(c => LaxTSConfigRoot(c))
    }else{
      Nil
    }
  }

}

case class LaxTSConfigRoot(config : Config) extends LaxTSConfig with AsSupport{



  override def key: String = ""

  def keys = {
    if(exists) {
      config.entrySet().asScala.map(es => {
        val k = es.getKey
        val i = k.indexOf(".")
        if(i > 0){
          k.substring(0,i)
        }else{
          k
        }
      }).toList.distinct
    } else Nil
  }

  override def /(key: String): TSConfig = {
    if(exists){
      LaxTSConfigWithKey(config,key)
    }else{
      TSNone
    }
  }

  override def exists: Boolean = {
    true
  }

  override def as[T: universe.TypeTag]: T = {
    get[T].getOrElse(null.asInstanceOf[T])
  }
  override def asList[T: TypeTag]: List[T] = {
    asList.map(c => {
      c.as[T]
    })
  }

  override def asList: List[TSConfig] = {
    Nil
  }
}