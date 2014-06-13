package com.geishatokyo.typesafeconfig

import com.typesafe.config.ConfigFactory
import com.geishatokyo.typesafeconfig.lax.LaxTSConfigRoot

/**
 * Created by takezoux2 on 2014/06/13.
 */
trait TSConfigFactory {
  def parseString(str : String) : TSConfig
}

object TSConfigFactory extends Proxy {

  var defaultFactory = lax

  def self = defaultFactory



  object lax extends TSConfigFactory{
    override def parseString(str: String): TSConfig = {
      val conf = ConfigFactory.parseString(str)
      LaxTSConfigRoot(conf)
    }
  }

}
