package com.geishatokyo.typesafeconfig.lax

import org.scalatest.{Matchers, FlatSpec}
import com.geishatokyo.typesafeconfig.TSConfigFactory

/**
 * Lax=緩い値解決をするlax系クラスのテスト
 * Created by takezoux2 on 2014/06/13.
 */
class LaxPatternTest extends FlatSpec with Matchers {


  "Not exist path" should "return exists == false" in {
    val conf = TSConfigFactory.lax.parseString("""{hoge:fuga}""")

    assert((conf / "not" / "exists" exists) == false)

  }
  "Not exist path" should "cast as default values" in {
    val conf = TSConfigFactory.lax.parseString("""{hoge:fuga}""")
    val v = conf / "not" / "exists"
    assert(v.asInt == LaxDefaults.int)
    assert(v.asLong == LaxDefaults.long)
    assert(v.asString == LaxDefaults.string)
    assert(v.asBoolean == LaxDefaults.boolean)
    assert(v.asDouble == LaxDefaults.double)
  }

  "Not exist " should "be empty list" in {
    val conf = TSConfigFactory.lax.parseString("""{hoge:fuga}""")
    val v = conf / "not" / "exists"

    assert(v.asList == Nil)
    assert(v.as[List[String]] == Nil)
    assert(v.as[Seq[Boolean]] == Seq())
    assert(v.as[Set[Long]] == Set())
    assert(v.as[Map[String,Int]] == Map())

  }

  "Wrong type path" should "return any value.(not exception)" in {
    val conf = TSConfigFactory.lax.parseString("""{notList:fuga}""")

    assert((conf / "notList").asList[String] == Nil)
  }

  "Case class" should "set LaxDefaults values to not exist fields" in {
    LaxDefaults.string = "Default string"
    val conf = TSConfigFactory.lax.parseString("""{a:2121}""")

    val a = conf.as[A]
    assert(a.a == 2121)
    assert(a.b == LaxDefaults.string)
    assert(a.c == 10.0)

  }

}

case class A(a : Int , b : String , c : Double = 10.0)
