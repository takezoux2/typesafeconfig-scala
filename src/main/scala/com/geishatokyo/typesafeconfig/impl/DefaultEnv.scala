package com.geishatokyo.typesafeconfig.impl

import com.geishatokyo.typesafeconfig.{TSConfig, Env}
import scala.reflect.runtime.universe._
import java.util.Date
import scala.concurrent.duration.Duration
import com.typesafe.config.Config
import scala.collection.JavaConverters._
import java.text.SimpleDateFormat

/**
 * Created by takezoux2 on 2014/07/17.
 */
class DefaultEnv extends Env{

  var int = 0
  var long = 0L
  var string = ""
  var double = 0.0
  var boolean = false
  var date : Date = new Date(0)

  def defaults : PartialFunction[Type,Any] = {
    case t if t =:= typeOf[Int] => int
    case t if t =:= typeOf[Long] => long
    case t if t =:= typeOf[String] => string
    case t if t =:= typeOf[Double] => double
    case t if t =:= typeOf[Boolean] => boolean
    case t if t =:= typeOf[Date] => date
    case t if t =:= typeOf[Duration] => Duration.fromNanos(0)
    case t => genericDefault(t)
  }

  private def genericDefault(t : Type) = t match{
    case t if t <:< typeOf[List[_]] => Nil
    case t if t <:< typeOf[Set[_]] => Set()
    case t if t <:< typeOf[Seq[_]] => Seq()
    case t if t <:< typeOf[Map[_,_]] => Map.empty
    case t if t <:< typeOf[Option[_]] => None
    case t => {
      //println("Default of " + t)
      null
    }
  }



  def as(config : Config,key : String)(implicit mirror: Mirror) : PartialFunction[Type,Any] = {
    case t if t =:= typeOf[Int] => config.getInt(key)
    case t if t =:= typeOf[Long] => config.getLong(key)
    case t if t =:= typeOf[Double] => config.getDouble(key)
    case t if t =:= typeOf[Boolean] => config.getBoolean(key)
    case t if t =:= typeOf[String] => config.getString(key)
    case t if t =:= typeOf[Option[Int]] => Some(config.getInt(key))
    case t if t =:= typeOf[Option[Long]] => Some(config.getLong(key))
    case t if t =:= typeOf[Option[Double]] => Some(config.getDouble(key))
    case t if t =:= typeOf[Option[Boolean]] => Some(config.getBoolean(key))
    case t if t =:= typeOf[Option[String]] => Some(config.getString(key))
    case t if t =:= typeOf[Date] => new SimpleDateFormat("yyyy/MM/dd HH:mm")
    case t if t <:< typeOf[List[String]] => config.getStringList(key).asScala.toList
    case t if t <:< typeOf[List[Int]] => config.getIntList(key).asScala.toList
    case t if t <:< typeOf[List[Long]] => config.getLongList(key).asScala.toList
    case t if t <:< typeOf[List[Double]] => config.getDoubleList(key).asScala.toList
    case t if t <:< typeOf[List[Boolean]] => config.getBooleanList(key).asScala.toList
    case t if t <:< typeOf[Seq[Int]] => config.getIntList(key).asScala.toSeq
    case t if t <:< typeOf[Seq[Long]] => config.getLongList(key).asScala.toSeq
    case t if t <:< typeOf[Seq[Double]] => config.getDoubleList(key).asScala.toSeq
    case t if t <:< typeOf[Seq[Boolean]] => config.getBooleanList(key).asScala.toSeq
    case t if t <:< typeOf[Seq[String]] => config.getStringList(key).asScala.toSeq
    case t if t <:< typeOf[Set[Int]] => config.getIntList(key).asScala.toSet
    case t if t <:< typeOf[Set[Long]] => config.getLongList(key).asScala.toSet
    case t if t <:< typeOf[Set[Double]] => config.getDoubleList(key).asScala.toSet
    case t if t <:< typeOf[Set[Boolean]] => config.getBooleanList(key).asScala.toSet
    case t if t <:< typeOf[Set[String]] => config.getStringList(key).asScala.toSet
    case t => {
      genericMatch(config,key,t)
    }
  }

  private def keys(conf : Config) = {
    conf.entrySet().asScala.map(e => {
      val k = e.getKey
      if(k.indexOf('.') >= 0){
        k.substring(0,k.indexOf('.'))
      }else{
        k
      }
    }).toSet
  }

  private def genericMatch(config : Config,key : String,t : Type)(implicit mirror: Mirror ) = t match {
    case t if t <:< typeOf[Map[String,_]] => {
      val c = if(key.length == 0) {
        config
      }else{
        config.getConfig(key)
      }
      val v = keys(c).map(k => {
        k -> mapObject(c.getConfig(k),t.typeArgs(1))
      }).toMap
      v
    }
    case t if t <:< typeOf[List[_]] => config.getConfigList(key).asScala.map(c => mapObject(c,t.typeArgs(0))).toList
    case t if t <:< typeOf[Set[_]] => config.getConfigList(key).asScala.map(mapObject(_,t.typeArgs(0))).toSet
    case t if t <:< typeOf[Option[_]] => Some(mapObject(config.getConfig(key),t.typeArgs(0)))
    case t if t =:= typeOf[Duration] => {
      Duration(config.getString(key))
    }
    case t if t <:< typeOf[Seq[_]] => config.getConfigList(key).asScala.map(mapObject(_,t.typeArgs(0))).toSeq
    case t => {
      if(key.length == 0){
        mapObject(config, t)
      }else {
        mapObject(config.getConfig(key), t)
      }
    }
  }

  private def mapObject(config : Config,tpe : Type)(implicit mirror: Mirror) : Any = {

    // map classes
    val constructor = tpe.members.collectFirst({
      case m : MethodSymbol if m.isPrimaryConstructor => m
    }).getOrElse({
      throw new Exception("Can't find primary constructor in " + tpe)
    })

    lazy val companionInstanceMirror = {
      val module = mirror.reflectModule(tpe.typeSymbol.companion.asModule).instance
      mirror.reflect(module)
    }

    def getDefaultValue(index : Int) = {

      val dfltMName = {
        import scala.reflect.runtime.universe
        import scala.reflect.internal._
        val ds = universe.asInstanceOf[Definitions with SymbolTable with StdNames]
        ds.nme.defaultGetterName(ds.newTermName("apply"),index + 1)
      }
      tpe.companion.decl(TermName(dfltMName.encoded)) match{
        case m : MethodSymbol => {
          Some(companionInstanceMirror.reflectMethod(m).apply())
        }
        case _ => None
      }
    }

    val constructorParams = constructor.paramss.flatten.zipWithIndex.map({
      case (p,index) => {
        val name = p.name.encodedName.toString
        val pf = as(config,name)

        def defaultValue = {
          getDefaultValue(index) match{
            case Some(v) => v
            case None => defaults.apply(p.typeSignature)
          }
        }

        if(config.hasPath(name) && pf.isDefinedAt(p.typeSignature)) {
          try{
            as(config,name).apply(p.typeSignature)
          }catch{
            case e : Throwable => defaultValue
          }
        }
        else {
          defaultValue
        }
      }
    })
    val classMirror = mirror.reflectClass(tpe.typeSymbol.asClass)
    val instance = classMirror.reflectConstructor(constructor)(constructorParams :_*)


    instance
  }
}

object DefaultEnv extends DefaultEnv