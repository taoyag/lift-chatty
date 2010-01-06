import sbt._

class LiftChattyProject(info: ProjectInfo) extends DefaultWebProject(info) {

  val liftWebkit = "net.liftweb" % "lift-webkit" % "1.1-M6" % "compile"
  val liftMapper = "net.liftweb" % "lift-mapper" % "1.1-M6" % "compile"
  val derby = "org.apache.derby" % "derby" % "10.4.2.0" % "compile"
  val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
  val junit = "junit" % "junit" % "4.5" % "test"
  val scalatest = "org.scalatest" % "scalatest" % "1.0" % "test"
  val jetty6 = "org.mortbay.jetty" % "jetty" % "[6.1.6, 6.1.19)" % "test"
  val scala = "org.scala-lang" % "scala-compiler" % "2.7.7" % "test"

  val smackRepo = "m2-repository-smack" at "http://maven.reucon.com/public"
}
