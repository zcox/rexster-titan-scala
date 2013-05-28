package com.pongr

import com.tinkerpop.blueprints.impls.rexster.RexsterGraph
import com.tinkerpop.blueprints.Direction._
import scala.collection.JavaConversions._
import grizzled.slf4j.Logging

//https://github.com/tinkerpop/blueprints/wiki/Rexster-Implementation
object RexsterGraphMain extends App with Logging {
  val graph = new RexsterGraph("http://localhost:8182/graphs/graph")

  val names = graph.getVertices.map(v => Option(v.getProperty("name"))).flatten
  debug("%d names: %s" format (names.size, names.mkString("[", ",", "]")))

  val zachLikes = graph.getVertices("name", "Zach").flatMap(_.getVertices(OUT, "likes")).map(v => Option(v.getProperty("name"))).flatten
  debug("Zach likes %d things: %s" format (zachLikes.size, zachLikes.mkString("[", ",", "]")))
}
