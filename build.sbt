

organization := "com.geishatokyo"

name := "typesafeconfig-scala"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.1"

crossScalaVersions := List("2.10.3","2.11.1")

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.1",
  "org.scalatest" %% "scalatest" % "2.2.0" % "test" 
)


libraryDependencies <++= (scalaVersion)(projectScalaVersion => {
  Seq("org.scala-lang" % "scala-reflect" % projectScalaVersion)
})


unmanagedSourceDirectories in Compile <++= (baseDirectory,scalaVersion)((dir,sVersion) => {
  sVersion match{
    case v if v.startsWith("2.10") => {
      Seq(dir / "src" / "main" / "scala2.10")
    }
    case _ => Seq()
  }
})