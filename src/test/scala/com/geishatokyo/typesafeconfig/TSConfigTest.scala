package com.geishatokyo.typesafeconfig

import org.scalatest.{Matchers, FlatSpec}
import scala.reflect.runtime._
import scala.reflect.runtime.universe._
import scala.concurrent.duration.Duration
import scala.concurrent.duration._

/**
 * Created by takezoux2 on 2014/06/13.
 */
class TSConfigTest extends FlatSpec with Matchers{


  "Normal usage" should "be" in {

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
        |  },
        |  loginSpan : "10 days"
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
      List(Item("stone",10),Item("potion",100)),
      Map(
        "fireBall" -> Skill(Some(30),None),
        "shield" -> Skill(None,Some(40))
      ),
      10 days
    ) )
  }
}

case class User(id : Long,name : String,age : Int,roles : List[String], avatar : Avatar,items : List[Item],skills : Map[String,Skill],loginSpan : Duration)
case class Avatar(head : String,body : String)
case class Item(name : String, price : Int = 10)
case class Skill(attack : Option[Int],defence : Option[Int])
