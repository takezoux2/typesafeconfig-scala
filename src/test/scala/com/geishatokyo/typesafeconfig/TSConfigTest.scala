package com.geishatokyo.typesafeconfig

import org.scalatest.{Matchers, FlatSpec}
import scala.reflect.runtime._
import scala.reflect.runtime.universe._

/**
 * Created by takezoux2 on 2014/06/13.
 */
class TSConfigTest extends FlatSpec with Matchers{


  "" should "" in {

    val conf = TSConfigFactory.lax.parseString(
      """
        |{
        |  id : 222,
        |  name : "Tom",
        |  age : 23,
        |  roles : ["admin","user"],
        |  avatar : {
        |    head : "afro",
        |    body : "normal"
        |  },
        |  items : [
        |    { name : stone},
        |    { name : potion, price : 100}
        |  ],
        |  skills : {
        |    fireBall : { attack : 30},
        |    shield : {defence : 40}
        |  }
        |}
        |
      """.stripMargin)

    assert( (conf / "id" asInt) == 222)
    assert( (conf / "name" asString) == "Tom")
    assert( (conf / "avatar" / "head" asString) == "afro")

    assert((conf / "avatar").as[Avatar] == Avatar("afro","normal"))
    assert( conf.as[User] == User(222,"Tom",23,
      List("admin","user"),
      Avatar("afro","normal"),
      List(Item("stone",0),Item("potion",100)),
      Map(
        "fireBall" -> Skill(Some(30),None),
        "shield" -> Skill(None,Some(40))
      )
    ) )
  }
}

case class User(id : Long,name : String,age : Int,roles : List[String], avatar : Avatar,items : List[Item],skills : Map[String,Skill])
case class Avatar(head : String,body : String)
case class Item(name : String, price : Int)
case class Skill(attack : Option[Int],defence : Option[Int])