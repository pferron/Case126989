lazy val scala212 = "2.12.15"

name := "x-pipeline"
organization := "com.capitalone.cstrat"
scalaVersion := scala212

val sparkVersion = "3.3.0"

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

lazy val sparkDependencies = Seq(
  ("org.apache.spark" %% "spark-core" % sparkVersion % "provided").excludeAll(
    ExclusionRule("com.fasterxml.jackson.core", "jackson-databind"),
    ExclusionRule("com.fasterxml.jackson.module", "jackson-databind")
  ),
  ("org.apache.spark" %% "spark-sql" % sparkVersion % "provided").excludeAll(
    ExclusionRule("com.fasterxml.jackson.core", "jackson-databind"),
    ExclusionRule("com.fasterxml.jackson.module", "jackson-databind")
  )
)

libraryDependencies ++= sparkDependencies

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.14" % "test",
  "org.scalacheck" %% "scalacheck" % "1.15.4" % "test",
  "com.typesafe" % "config" % "1.4.2",
  "com.capitalone.cstrat" %% "graphite" % "1.1.9"
)

excludeDependencies ++= Seq(
  ExclusionRule("log4j", "log4j"),
  ExclusionRule("org.slf4j", "slf4j-log4j12"),
  ExclusionRule("org.codehaus.jackson", "jackson-mapper-asl"),
  ExclusionRule("commons-beanutils", "commons-beanutils-core"),
  ExclusionRule("com.fasterxml.jackson.module",  "jackson-databind")
)


externalResolvers := Resolver.combineDefaultResolvers(resolvers.value.toVector, mavenCentral = false)
resolvers ++= Seq(
  "artifactory-maven-internalfacing" at "https://artifactory.cloud.capitalone.com/artifactory/maven-internalfacing",
  "artifactory-maven-publicfacing" at "https://artifactory.cloud.capitalone.com/artifactory/maven-publicfacing",
  "artifactory-sbt-internalfacing-mavenstyle" at "https://artifactory.cloud.capitalone.com/artifactory/sbt-internalfacing",
  Resolver.url("artifactory-sbt-internalfacing-ivystyle", url("https://artifactory.cloud.capitalone.com/artifactory/sbt-internalfacing"))(Resolver.ivyStylePatterns),
  "artifactory-sbt-publicfacing-mavenstyle" at "https://artifactory.cloud.capitalone.com/artifactory/sbt-publicfacing",
  Resolver.url("artifactory-sbt-publicfacing-ivystyle", url("https://artifactory.cloud.capitalone.com/artifactory/sbt-publicfacing"))(Resolver.ivyStylePatterns),
  Resolver.url("artifactory-sbt-publicfacing-releases", url("https://artifactory.cloud.capitalone.com/artifactory/sbt-publicfacing/"))(Resolver.ivyStylePatterns),
  Resolver.url("artifactory-sbt-internalfacing-releases", url("https://artifactory.cloud.capitalone.com/artifactory/sbt-internalfacing/"))(Resolver.ivyStylePatterns),
  Resolver.url("artifactory-sbt-plugins-releases", url("https://artifactory.cloud.capitalone.com/artifactory/sbt-plugins/"))(Resolver.ivyStylePatterns),
  "artifactory-sbt-publicfacing-releases-anyformat" at "https://artifactory.cloud.capitalone.com/artifactory/sbt-publicfacing/",
  "artifactory-sbt-plugins-releases-anyformat" at "https://artifactory.cloud.capitalone.com/artifactory/sbt-plugins/",
  Resolver.url("artifactory-maven-plugins-releases", url("https://artifactory.cloud.capitalone.com/artifactory/x-maven-int-east-local/"))(Resolver.ivyStylePatterns),
  "artifactory-maven-releases-anyformat" at "https://artifactory.cloud.capitalone.com/artifactory/x-maven-int-east-local/",
  "artifactory-sbt-internalfacing-releases-anyformat" at "https://artifactory.cloud.capitalone.com/artifactory/sbt-internalfacing/"
)


assembly / assemblyMergeStrategy := {
  case x if Assembly.isConfigFile(x) =>
    MergeStrategy.concat
  case PathList(ps @ _*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) =>
    MergeStrategy.rename
  case PathList("META-INF", xs @ _*) =>
    (xs map {_.toLowerCase}) match {
      case "native" :: xs =>
        MergeStrategy.deduplicate
      case "services" :: xs =>
        MergeStrategy.filterDistinctLines
      case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.discard
    }
  case _ => MergeStrategy.first
}

// Adds fat jar to set of artifacts to publish
Compile / assembly / artifact := {
  val art = (Compile / assembly / artifact).value
  art.withClassifier(Some("assembly"))
}

addArtifact((Compile / assembly / artifact), assembly)

//Skips tests in sbt assembly
assembly / test := {}