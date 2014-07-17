# What is this?

Scala wrapper for Typesafe config.

# Usage

scala

    import com.geishatokyo.typesafeconfig._
    
    val v = TSConfigFactory.parseString("""
      {
        name : "Tom",
        age : 20,
        job : { name : "Programmer"}
      }
    """)

    assert((v / "name" asString) == "Tom")
    assert((v / "age" asInt) == 20)
    assert((v / "job" / "name" asString) == "Programmer")
    assert((v / "fullname" exists) == false)
    
    case class User(name : String,age : Int,job : Job)
    case class Job(name : String)
    
    assert(v.as[User] == User("Tom",20,Job("Programmer"))

# Support types

* Int
* Long
* Double
* Boolean
* String
* Duration
* java.util.Date
* Case class
* TSConf(wrapper class for TypesafeConfig)

and 

* Option[T] of above
* List[T] of above
* Set[T] of above
* Seq[T] of above
* Map[String,T] of case class

# Mapping Map[String,T]

Map as such

    import com.geishatokyo.typesafeconfig._
    
    case class Item(attack : Int,defence : Int)
    
    val v = TSConfigFactory.parseString("""
      {
        weapon : {attack : 20,defence : 10},
        armor : {attack : 0,defence : 40}
      }
    """)
    
    assert(v.as[Map[String,Item]] == Map("weapon" -> Item(20,10),"armor" -> Item(0,40))
    


# More codes?

See test codes.


# License

MIT License
