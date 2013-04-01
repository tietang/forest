# forest #

a library for creating distributed service and sharding framework.

**forest** was designed to be **EASY** to use, also is a lightweight framework and easily is extended.

## forest-core ##

forest-core is a core API. 

To use it, we can create distributed service, like Redis/Memecached cluster.

To use it, we can create data sharding service, like database vertical and horizontal scaling.

forest-core support exact match and range match.

Also see [Examples](https://github.com/wtt2012/forest/tree/master/forest-core/src/main/java/fengfei/forest/slice/example "Examples"):

 - [exact match example](https://github.com/wtt2012/forest/tree/master/forest-core/src/main/java/fengfei/forest/slice/example/AccuracyRouterExample.java "exact match example")
- [range match example](https://github.com/wtt2012/forest/tree/master/forest-core/src/main/java/fengfei/forest/slice/example/NavigableRouterExample.java "range match example")
- [Basic Server Example](https://github.com/wtt2012/forest/tree/master/forest-core/src/main/java/fengfei/forest/slice/example/ServerRouterExample.java "Basic Server Example")
- [Basic Server Example](https://github.com/wtt2012/forest/tree/master/forest-core/src/main/java/fengfei/forest/slice/example "Basic Server Example")
- [Pooled Server Example](https://github.com/wtt2012/forest/tree/master/forest-core/src/main/java/fengfei/forest/slice/example/PooledServerRouterExample.java "Pooled Server Example")
- [Another Pooled Server Example](https://github.com/wtt2012/forest/tree/master/forest-core/src/test/java/fengfei/forest/slice/server/example/PoolableServerExample.java "Another Pooled Server Example") 
- [test example](https://github.com/wtt2012/forest/tree/master/forest-core/src/test/java/fengfei/forest/slice "test example")



## forest-database ##

 a implement of sharding framework based forest-core for database.
 You can also see [sharding example](https://github.com/wtt2012/forest/tree/master/forest-database "sharding example]").

## maven dependency##
**add repository**

		<repository>
			<id>fengfei-repo</id>
			<name>fengfei Repository </name>
			<url>http://fengfei.googlecode.com/svn/maven-repo/releases</url>
		</repository>
		<repository>
			<id>fengfei-snapshot</id>
			<name>fengfei Repository </name>
			<url>http://fengfei.googlecode.com/svn/maven-repo/snapshots</url>
		</repository>

**use it as a maven dependency:**

    <dependency>
    	<groupId>fengfei.forest</groupId>
    	<artifactId>forest-core</artifactId>
    	<version>1.0-SNAPSHOT</version>
    </dependency>
or

    <dependency>
    	<groupId>fengfei.forest</groupId>
    	<artifactId>forest-database</artifactId>
    	<version>1.0-SNAPSHOT</version>
    </dependency>