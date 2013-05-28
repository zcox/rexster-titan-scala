Shows some examples of using [Titan](http://thinkaurelius.github.io/titan/) over [Rexster](http://rexster.tinkerpop.com) in [Scala](http://scala-lang.org).

# Overview

 - Run a [Titan Server](https://github.com/thinkaurelius/titan/wiki/Titan-Server)
 - Use [Rexster Console](https://github.com/tinkerpop/rexster/wiki/Rexster-Console) to set up a simple graph
 - Run [Gremlin](http://gremlin.tinkerpop.com) queries on Titan remotely using the [RexPro Java client](https://github.com/tinkerpop/rexster/wiki/RexPro-Java)
 - Tweak things slightly for Scala

# Titan Server

We'll run a Titan+Cassandra+RexPro server. Version 0.3.1 is the latest as of this writing.

```
wget http://s3.thinkaurelius.com/downloads/titan/titan-cassandra-0.3.1.zip
unzip titan-cassandra-0.3.1.zip
cd titan-cassandra-0.3.1
bin/titan.sh config/titan-server-rexster.xml config/titan-server-cassandra.properties
```

# Rexster Console

We'll use [Rexster Console](https://github.com/tinkerpop/rexster/wiki/Rexster-Console) to set up some nodes and edges in Titan.

```
wget http://tinkerpop.com/downloads/rexster/rexster-console-2.3.0.zip
unzip rexster-console-2.3.0.zip
cd rexster-console-2.3.0
bin/rexster-console.sh

        (l_(l
(_______( 0 0
(        (-Y-) <woof>
l l-----l l
l l,,   l l,,
opening session [127.0.0.1:8184]
?h for help

rexster[groovy]> g = rexster.getGraph("graph")
==>titangraph[embeddedcassandra:null]
rexster[groovy]> v1 = g.addVertex([name:"Zach"])
==>v[4]
rexster[groovy]> v2 = g.addVertex([name:"Scala"])
==>v[8]
rexster[groovy]> e1 = g.addEdge(v1, v2, "likes", [since: 2009])
==>e[n-4-2F0LaTPQAS][4-likes->8]
rexster[groovy]> v3 = g.addVertex([name:"NOS"])
==>v[12]
rexster[groovy]> e2 = g.addEdge(v1,v3,"likes",[since:2012])
==>e[z-4-2F0LaTPQAS][4-likes->12]
rexster[groovy]> g.stopTransaction(SUCCESS)
==>null
rexster[groovy]> g.V.name
==>Zach
==>Scala
==>NOS
rexster[groovy]> g.V('name','Zach').out('likes').name
==>Scala
==>NOS
rexster[groovy]> ?q
closing session with Rexster [ip-10-152-185-66.ec2.internal:8184]--> done
```

# RexPro

You can use the [RexPro Java API](https://github.com/tinkerpop/rexster/wiki/RexPro-Java) to send Gremlin queries to a remote Rexster server and process the results.

All you really need is the rexster-protocol dependency in build.sbt:

```scala
"com.tinkerpop.rexster" % "rexster-protocol" % "2.3.0"
```

Next just use RexsterClientFactory to get a RexsterClient:

```scala
import com.tinkerpop.rexster.client.RexsterClientFactory
val client = RexsterClientFactory.open("localhost", "graph")
```

Then send Gremlin queries over the RexsterClient:

```scala
val results = client.execute("g.V.name")
```

# RexsterGraph

You can also use [RexsterGraph](https://github.com/tinkerpop/blueprints/wiki/Rexster-Implementation) to communicate with any remote Rexster server. This is an implementation of the [Blueprints](http://blueprints.tinkerpop.com) Graph interface.

Just include the blueprints-rexster-graph dependency in build.sbt:

```scala
"com.tinkerpop.blueprints" % "blueprints-rexster-graph" % "2.3.0"
```

Next create a RexsterGraph and call methods on it, just like you would any Graph:

```scala
import com.tinkerpop.blueprints.impls.rexster.RexsterGraph
val graph = new RexsterGraph("http://localhost:8182/graphs/graph")
val names = graph.getVertices.map(v => Option(v.getProperty("name"))).flatten
```

Note that RexsterGraph also has an execute method, similar to RexsterClient, that takes a Gremlin query String and returns a JSONArray of results:

```scala
graph.execute("g.V.name")
```

# Running the App

With the Titan Server running locally, just clone this repo and do `sbt run` and you should see output like this:

```
$ sbt run
[info] Set current project to rexster-titan-scala (in build file:/home/zcox/dev/rexster-titan-scala/)
[info] Running com.pongr.Main 
2013-05-14 16:54:53,293 INFO  c.t.r.client.RexsterClientFactory - Create RexsterClient instance: [hostname=localhost
graph-name=graph
port=8184
timeout-connection-ms=8000
timeout-write-ms=4000
timeout-read-ms=16000
max-async-write-queue-size=512000
message-retry-count=16
message-retry-wait-ms=50
language=groovy
graph-obj-name=g
transaction=true
channel=2]
2013-05-14 16:54:53,925 DEBUG com.pongr.Main$ - 3 names: [Zach,Scala,NOS]
2013-05-14 16:54:54,004 DEBUG com.pongr.Main$ - Zach likes 2 things: [Scala,NOS]
[success] Total time: 4 s, completed May 14, 2013 4:54:54 PM
```

# EC2

This demonstrates running the Titan Server and the Rexster app on two different servers, communicating over the network.

Initial setup:

```
ec2run ami-856f02ec -n 2 -t m1.medium -k yourkey -g your-ssh-group -g your-titan-group
open port 8184 for your-titan-group
```

On Titan Server instance:

```
sudo apt-get update
sudo apt-get install -y unzip htop ntp default-jre

#Download and run Titan Server as above
#Use private IP address in <server-host> in titan-server-rexster.xml
```

On Rexster app instance:

```
sudo apt-get update
sudo apt-get install -y unzip htop ntp default-jdk git

#Download and unzip Rexter Console as above
bin/rexster-console.sh -rh [internal-hostname of titan-server]

cd ~
wget http://scalasbt.artifactoryonline.com/scalasbt/sbt-native-packages/org/scala-sbt/sbt//0.12.3/sbt.tgz
tar xvzf sbt.tgz

cd ~
git clone git://github.com/zcox/rexster-titan-scala.git
cd rexster-titan-scala
#Replace localhost in src/main/scala/main.scala with internal-hostname of titan-server
~/sbt/bin/sbt run
```

It will take a long time for sbt to set itself up and download all dependencies, but you should then see the same output as running the app locally.

# TODO

  - What about local development? Local file or in-memory Titan?
    - TitanFactory.open() with storage.backend=inmemory should return an in-memory TitanGraph, which is a Blueprints Graph
    - DefaultRexsterApplication takes a Blueprints Graph
    - RexProRexsterServer.start() takes a RexsterApplication
    - Then RexsterClient should be able to connect to that RexPro server...
    - For now, just assume you have to run Titan Server locally during development
  - Can you set up all of the indexes, keys etc via Rexster?
