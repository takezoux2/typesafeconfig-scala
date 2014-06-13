package com.geishatokyo.typesafeconfig

import com.typesafe.config.{Config, ConfigFactory}
import com.geishatokyo.typesafeconfig.lax.LaxTSConfigRoot
import java.io.File

/**
 * Created by takezoux2 on 2014/06/13.
 */
trait TSConfigFactory {
  def parseString(str : String) : TSConfig
  def parseFile(path : String) : TSConfig
  def parseFile(file : File) : TSConfig
  def fromConfig(config : Config) : TSConfig
}

object TSConfigFactory extends TSConfigFactory{

  var defaultFactory = lax

  override def parseString(str: String): TSConfig = defaultFactory.parseString(str)
  override def fromConfig(config: Config): TSConfig = defaultFactory.fromConfig(config)
  override def parseFile(path: String): TSConfig = defaultFactory.parseFile(path)
  override def parseFile(file: File): TSConfig = defaultFactory.parseFile(file)

  object lax extends TSConfigFactory{
    override def parseString(str: String): TSConfig = {
      val conf = ConfigFactory.parseString(str)
      LaxTSConfigRoot(conf)
    }

    override def parseFile(path: String): TSConfig = {
      val conf = ConfigFactory.parseFile(new File(path))
      LaxTSConfigRoot(conf)
    }
    override def parseFile(file: File): TSConfig = {
      val conf = ConfigFactory.parseFile(file)
      LaxTSConfigRoot(conf)
    }

    override def fromConfig(config: Config): TSConfig = {
      LaxTSConfigRoot(config)
    }
  }

}
