# jheappo
A Heap Dump Parser


## Neo4j Export

run Heappo and add the "graph" parameter

`java -jar path/to/jheappo.jar "/path/to/file.hprof" graph`

It will create a `graph.db` directory.

The graph model is:

```
(:Class {id, name, _fields})-[:SUPERCLASS]->(:Class)
(:Instance {id, _fields})-[:IS_CLASS]->(:Class)
(:Instance)-[:CONTAINS]->(:Instance)
```
<!-- TODO visual graph model -->

### Query Graph Console

You can open it directly with neo4j-shell:

`$NEO4J_HOME/bin/neo4j-shell -path graph.db`

and then run queries like:

```
~/v/neo4j-enterprise-3.3.3/bin/neo4j-shell -path graph.db
NOTE: Local Neo4j graph database service at 'graph.db'

match (c:Class) return c limit 5;
+---------------------------------------------------------------------------------------------------------------------------------------------------+
| c                                                                                                                                                 |
+---------------------------------------------------------------------------------------------------------------------------------------------------+
| Node[0]{id:28991131920,name:"sun/lwawt/macosx/CPlatformWindow$$Lambda$33",size:8,_arg$1:2}                                                        |
| Node[1]{size:0,id:28991103008,name:"java/lang/Object"}                                                                                            |
| Node[2]{id:28991132048,name:"sun/java2d/opengl/CGLLayer$$Lambda$32",size:0}                                                                       |
| Node[3]{id:28991132200,name:"java/lang/invoke/LambdaForm$MH",size:0}                                                                              |
| Node[4]{id:28991132328,name:"java/lang/invoke/LambdaForm$DMH",size:0}                                                                             |
| Node[5]{id:28991132456,name:"java/lang/invoke/LambdaForm$DMH",size:0}                                                                             |
| Node[6]{_arg$5:2,_arg$4:2,_arg$3:2,_arg$2:10,id:28991132584,name:"com/apple/laf/AquaPainter$AquaSingleImagePainter$$Lambda$31",size:32,_arg$1:10} |
+---------------------------------------------------------------------------------------------------------------------------------------------------+

match (c:Class) where not c.name starts with "java/lang/invoke/LambdaForm" return c limit 100;

match (c:Class) where c.name = "java/lang/Object" return c;

match (c:Class) where c.name = "java/lang/Object" return c, size( (c)<-[:IS_CLASS]-() );

match (c:Instance) return count(*);
+----------+
| count(*) |
+----------+
| 38359    |
+----------+
1 row
70 ms
match (c:Instance) where not (c)<-[:CONTAINS]-() return count(*);
+----------+
| count(*) |
+----------+
| 14611    |
+----------+
1 row
213 ms

match (i:Instance)-[:IS_CLASS]->(c) where not (i)<-[:CONTAINS]-() return c.name, i limit 10;;
+------------------------------------------------------------------------------------------------+
| c.name                                     | i                                                 |
+------------------------------------------------------------------------------------------------+
| "java/util/concurrent/locks/ReentrantLock" | Node[2188]{stackSerial:1,id:28991029248}          |
| "java/lang/String"                         | Node[2190]{stackSerial:1,id:28991029264,_hash:0}  |
| "java/lang/String"                         | Node[2195]{stackSerial:1,id:28991030104,_hash:0}  |
| "java/lang/String"                         | Node[2197]{stackSerial:1,id:28991030672,_hash:0}  |
| "java/lang/String"                         | Node[2199]{stackSerial:1,id:28991030696,_hash:0}  |
| "java/lang/String"                         | Node[2201]{stackSerial:1,id:28991030720,_hash:0}  |
| "java/lang/StringBuilder"                  | Node[2203]{stackSerial:1,id:28991031944}          |
| "java/lang/String"                         | Node[2204]{stackSerial:1,id:28991031968,_hash:0}  |
| "java/lang/String"                         | Node[2206]{stackSerial:1,id:28991031992,_hash:0}  |
| "sun/awt/SunHints$Value"                   | Node[2213]{stackSerial:1,id:28991032064,_index:2} |
+------------------------------------------------------------------------------------------------+
10 rows
52 ms


match (c:Class) return c.name, size( (c)<-[:IS_CLASS]-() ) as instances order by instances desc limit 10;
+-----------------------------------------------------------+
| c.name                                        | instances |
+-----------------------------------------------------------+
| "java/lang/String"                            | 7968      |
| "java/util/Hashtable$Entry"                   | 1805      |
| "java/util/concurrent/ConcurrentHashMap$Node" | 1348      |
| "java/util/HashMap$Node"                      | 1323      |
| "java/lang/ref/SoftReference"                 | 1123      |
| "java/util/concurrent/ConcurrentHashMap"      | 838       |
| "java/lang/ref/Finalizer"                     | 815       |
| "sun/font/Font2DHandle"                       | 793       |
| "sun/font/CFont"                              | 785       |
| "java/lang/reflect/Field"                     | 561       |
+-----------------------------------------------------------+
10 rows
12 ms
```

### Graph Visualization

Ìf you want to see the graph visually, download and install Neo4j(Desktop) and create an new "Graph" in your "Project"

Then go to "Manager" -> "Open Folder" and copy the `graph.db` directory into `data/database/graph.db`

Then you can start the server and "Open Neo4j Browser" to execute queries and interactively visualize the data.

Note: As the Heap graph is very large and dense, Neo4j browser's default visualization will come to its limits quickly.

Then you need to add LIMIT's or filter / group data.

