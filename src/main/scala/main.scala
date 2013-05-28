package com.pongr

import com.tinkerpop.rexster.client.RexsterClientFactory
import scala.collection.JavaConversions._
import grizzled.slf4j.Logging

//https://github.com/tinkerpop/rexster/wiki/RexPro-Java
object RexsterClientMain extends App with Logging {
  val client = RexsterClientFactory.open("localhost", "graph")

  val names: Seq[String] = client.execute("g.V.name").toSeq
  debug("%d names: %s" format (names.size, names.mkString("[", ",", "]")))

  val zachLikes: Seq[String] = client.execute("g.V('name',name).out('likes').name", Map("name" -> "Zach")).toSeq
  debug("Zach likes %d things: %s" format (zachLikes.size, zachLikes.mkString("[", ",", "]")))

  client.close()
}

//https://github.com/tinkerpop/blueprints/wiki/Rexster-Implementation
import com.tinkerpop.blueprints.impls.rexster.RexsterGraph
import com.tinkerpop.blueprints.Direction._
object RexterGraphMain extends App with Logging {
  val graph = new RexsterGraph("http://localhost:8182/graphs/graph")

  val names = graph.getVertices.map(v => Option(v.getProperty("name"))).flatten
  debug("%d names: %s" format (names.size, names.mkString("[", ",", "]")))

  val zachLikes = graph.getVertices("name", "Zach").flatMap(_.getVertices(OUT, "likes")).map(v => Option(v.getProperty("name"))).flatten
  debug("Zach likes %d things: %s" format (zachLikes.size, zachLikes.mkString("[", ",", "]")))
}

//An attempt to expose in-memory Titan via RexPro...
/*import com.thinkaurelius.titan.core.TitanFactory
import com.tinkerpop.rexster.server.{DefaultRexsterApplication, RexProRexsterServer}
import org.apache.commons.configuration.{BaseConfiguration, XMLConfiguration}

object RexProServerWithInMemoryTitanGraph extends App {
  val conf = new BaseConfiguration()
  conf.setProperty("storage.backend","inmemory")
  val graph = TitanFactory.open(conf) //this throws an NPE...

  val app = new DefaultRexsterApplication("graph", graph)
  val props = new XMLConfiguration()
  val server = new RexProRexsterServer(props)
  server.start(app)
}*/
