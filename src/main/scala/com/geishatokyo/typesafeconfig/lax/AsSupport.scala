package com.geishatokyo.typesafeconfig.lax

import com.geishatokyo.typesafeconfig.TSConfig
import scala.reflect.runtime.universe._
import com.typesafe.config.Config
import scala.collection.JavaConverters._

/**
 * Created by takezoux2 on 2014/06/13.
 */
trait AsSupport { self : TSConfig =>

  def config : Config
  def key : String

  def get[T : TypeTag]: Option[T] = {
    if(this ?){
      val typeTag = implicitly[TypeTag[T]]
      val mirror = typeTag.mirror
      Some(as(typeOf[T])(mirror).asInstanceOf[T])
    }else{
      None
    }
  }

  def as(tpe: Type)(implicit mirror: Mirror): Any = {
    // map special types
    tpe match{
      case t if t =:= typeOf[Int] => return if(!exists) LaxDefaults.int else config.getInt(key)
      case t if t =:= typeOf[Long] => return if(!exists) LaxDefaults.long else config.getLong(key)
      case t if t =:= typeOf[Double] => return if(!exists) LaxDefaults.double else config.getDouble(key)
      case t if t =:= typeOf[Boolean] => return if(!exists) LaxDefaults.boolean else config.getBoolean(key)
      case t if t =:= typeOf[String] => return if(!exists) LaxDefaults.string else config.getString(key)
      case t if t =:= typeOf[Option[Int]] => return if(exists) Some(config.getInt(key)) else None
      case t if t =:= typeOf[Option[Long]] => return if(exists) Some(config.getLong(key)) else None
      case t if t =:= typeOf[Option[Boolean]] => return if(exists) Some(config.getBoolean(key)) else None
      case t if t =:= typeOf[Option[Double]] => return if(exists) Some(config.getDouble(key)) else None
      case t if t =:= typeOf[Option[String]] => return if(exists) Some(config.getString(key)) else None
      case t if t <:< typeOf[List[String]] => return config.getStringList(key).asScala.toList
      case t if t <:< typeOf[List[Int]] => return config.getIntList(key).asScala.toList
      case t if t <:< typeOf[List[Long]] => return config.getLongList(key).asScala.toList
      case t if t <:< typeOf[List[Double]] => return config.getDoubleList(key).asScala.toList
      case t if t <:< typeOf[List[Boolean]] => return config.getBooleanList(key).asScala.toList
      case t if t <:< typeOf[Seq[Int]] => return config.getIntList(key).asScala.toSeq
      case t if t <:< typeOf[Seq[Long]] => return config.getLongList(key).asScala.toSeq
      case t if t <:< typeOf[Seq[Double]] => return config.getDoubleList(key).asScala.toSeq
      case t if t <:< typeOf[Seq[Boolean]] => return config.getBooleanList(key).asScala.toSeq
      case t if t <:< typeOf[Seq[String]] => return config.getStringList(key).asScala.toSeq
      case t if t <:< typeOf[Set[Int]] => return config.getIntList(key).asScala.toSet
      case t if t <:< typeOf[Set[Long]] => return config.getLongList(key).asScala.toSet
      case t if t <:< typeOf[Set[Double]] => return config.getDoubleList(key).asScala.toSet
      case t if t <:< typeOf[Set[Boolean]] => return config.getBooleanList(key).asScala.toSet
      case t if t <:< typeOf[Set[String]] => return config.getStringList(key).asScala.toSet
      case t if t <:< typeOf[List[_]] => return this.asList.map(_.as(t.typeArgs(0)))
      case t if t <:< typeOf[Seq[_]] => return this.asList.map(_.as(t.typeArgs(0))).toSeq
      case t if t <:< typeOf[Set[_]] => return this.asList.map(_.as(t.typeArgs(0))).toSet
      case t if t <:< typeOf[Map[String,_]] => {
        val valueType = t.typeArgs(1)
        return keys.map(key => {
          key -> (this / key).as(valueType)
        }).toMap
      }
      case _ =>
    }

    // map classes
    val constructor = tpe.members.collectFirst({
      case m : MethodSymbol if m.isPrimaryConstructor => m
    }).getOrElse({
      throw new Exception("Can't find primary constructor in " + tpe)
    })
    var index = 0
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
        val v = (this / p.name.encodedName.toString)
        if(v.exists) v.as(p.typeSignature)
        else {
          getDefaultValue(index) match{
            case Some(v) => v
            case None => v.as(p.typeSignature)
          }
        }
      }
    })
    val classMirror = mirror.reflectClass(tpe.typeSymbol.asClass)
    val instance = classMirror.reflectConstructor(constructor)(constructorParams :_*)


    instance
  }
}
