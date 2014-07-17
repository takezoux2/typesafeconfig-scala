package com.geishatokyo.typesafeconfig

import org.scalatest.{Matchers, FlatSpec}
import com.geishatokyo.typesafeconfig.impl.DefaultEnv

/**
 * Lax=緩い値解決をするlax系クラスのテスト
 * Created by takezoux2 on 2014/06/13.
 */
class SuccessPatternTest extends FlatSpec with Matchers {


  "Not exist path" should "return exists == false" in {
    val conf = TSConfigFactory.parseString("""{hoge:fuga}""")

    assert((conf / "not" / "exists" exists) == false)

  }
  "Not exist path" should "cast as default values" in {
    val conf = TSConfigFactory.parseString("""{hoge:fuga}""")
    val v = conf / "not" / "exists"
    assert(v.asInt == DefaultEnv.int)
    assert(v.asLong == DefaultEnv.long)
    assert(v.asString == DefaultEnv.string)
    assert(v.asBoolean == DefaultEnv.boolean)
    assert(v.asDouble == DefaultEnv.double)
  }

  "Not exist " should "be empty list" in {
    val conf = TSConfigFactory.parseString("""{hoge:fuga}""")
    val v = conf / "not" / "exists"

    assert(v.asList == Nil)
    assert(v.as[List[String]] == Nil)
    assert(v.as[Seq[Boolean]] == Seq())
    assert(v.as[Set[Long]] == Set())
    assert(v.as[Map[String,Int]] == Map())

  }

  "Wrong type path" should "return any value.(not exception)" in {
    val conf = TSConfigFactory.parseString("""{notList:fuga}""")

    assert((conf / "notList").asList[String] == Nil)
  }

  "Case class" should "set LaxDefaults values to not exist fields" in {
    DefaultEnv.string = "Default string"
    val conf = TSConfigFactory.parseString("""{a:2121}""")

    val a = conf.as[ABC]
    assert(a == ABC(2121,DefaultEnv.string,10.0))

  }

}

case class ABC(a : Int , b : String , c : Double = 10.0)
