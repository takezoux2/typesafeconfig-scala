# What is this?

Scala wrapper for Typesafe config.

# Usage

scala

    import com.geishatokyo.typesafeconfig._
    
    val v = TSConfigFactory.lax.parseString("""
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
* Case class

and 

* Option[T] of above
* List[T] of above
* Set[T] of above
* Seq[T] of above
* Map[String,T] of case class

# More codes?

See test codes.


# License

MIT License
