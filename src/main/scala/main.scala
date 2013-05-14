package com.pongr

import com.tinkerpop.rexster.client.RexsterClientFactory
import grizzled.slf4j.Logging
import scala.collection.JavaConversions._

object Main extends App with Logging {
  val client = RexsterClientFactory.open("localhost", "graph")

  val names: Seq[String] = client.execute("g.V.name").toSeq
  debug("%d names: %s" format (names.size, names.mkString("[", ",", "]")))

  val zachLikes: Seq[String] = client.execute("g.V('name',name).out('likes').name", Map("name" -> "Zach")).toSeq
  debug("Zach likes %d things: %s" format (zachLikes.size, zachLikes.mkString("[", ",", "]")))

  client.close()
}
