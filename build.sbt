lazy val localConfigPath = "./local.conf"

lazy val root = Project(id = "root", base = file("."))
  .settings(
    name := "busting-grain",
    skip in publish := true
  )
  .withId("root")
  .settings(commonSettings)

lazy val pipeline = appModule("pipeline")
  .enablePlugins(CloudflowApplicationPlugin)
  .settings(commonSettings, runLocalConfigFile := Some(localConfigPath))
  .aggregate(
    datamodel,
    grainGenerator,
    grainEgress,
    grainBuster,
    badGrain,
    goodGrain
  )

lazy val datamodel = appModule("datamodel")
  .enablePlugins(CloudflowLibraryPlugin)

lazy val grainGenerator = appModule("grain-generator")
  .enablePlugins(CloudflowFlinkPlugin)
  .settings(
    libraryDependencies += "org.apache.flink" % "flink-metrics-dropwizard" % "1.10.2"
  )
  .dependsOn(datamodel)

lazy val grainEgress = appModule("grain-egress")
  .enablePlugins(CloudflowFlinkPlugin)
  .dependsOn(datamodel)

lazy val grainBuster = appModule("grain-buster")
  .enablePlugins(CloudflowFlinkPlugin)
  .dependsOn(datamodel)

lazy val goodGrain = appModule("good-grain")
  .enablePlugins(CloudflowFlinkPlugin)
  .dependsOn(datamodel)

lazy val badGrain = appModule("bad-grain")
  .enablePlugins(CloudflowFlinkPlugin)
  .dependsOn(datamodel)

def appModule(moduleID: String): Project = {
  Project(id = moduleID, base = file(moduleID))
    .settings(
      name := moduleID
    )
    .withId(moduleID)
    .settings(commonSettings)
}

lazy val commonSettings = Seq(
  organization := "org.dedkot",
  scalaVersion := "2.12.10",
  javacOptions += "-Xlint:deprecation",
  scalacOptions ++= Seq(
    "-encoding",
    "UTF-8",
    "-target:jvm-1.8",
    "-Xlog-reflective-calls",
    "-Xlint",
    "-Ywarn-unused",
    "-Ywarn-unused-import",
    "-deprecation",
    "-feature",
    "-language:_",
    "-unchecked"
  ),
  scalacOptions in (Compile, console) --= Seq("-Ywarn-unused", "-Ywarn-unused-import"),
  scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value
)
