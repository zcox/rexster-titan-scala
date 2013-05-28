organization := "com.pongr"

name := "rexster-titan-scala"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.1"

libraryDependencies ++= {
  val rexster = "2.3.0"
  val titan = "0.3.1"
  val slf4j = "1.7.5"
  Seq(
    "com.tinkerpop.rexster" % "rexster-protocol" % rexster,
    //"com.tinkerpop.rexster" % "rexster-server" % rexster,
    //"com.thinkaurelius.titan" % "titan-core" % titan,
    "com.tinkerpop.blueprints" % "blueprints-rexster-graph" % rexster,
    "org.slf4j" % "slf4j-api" % slf4j,
    "org.slf4j" % "jcl-over-slf4j" % slf4j,
    "org.slf4j" % "log4j-over-slf4j" % slf4j,
    "org.clapper" %% "grizzled-slf4j" % "0.6.10",
    "ch.qos.logback" % "logback-classic" % "1.0.13"
  )
}

ivyXML := 
  <dependencies>
    <exclude org="log4j" name="log4j" />
    <exclude org="commons-logging" name="commons-logging" />
    <exclude org="org.slf4j" name="slf4j-log4j12" />
  </dependencies>
