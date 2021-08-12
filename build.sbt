lazy val localConfigPath = "./local.conf"

lazy val root = Project(id = "root", base = file("."))
  .settings(
    name := "busting-grain",
    skip in publish := true
  )
  .withId("root")
  .settings(commonSettings)
  .aggregate()

lazy val pipeline = appModule("pipeline")
  .enablePlugins(CloudflowApplicationPlugin)
  .settings(commonSettings, runLocalConfigFile := Some(localConfigPath))

lazy val datamodel = appModule("datamodel")
  .enablePlugins(CloudflowLibraryPlugin)

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
  scalacOptions in(Compile, console) --= Seq("-Ywarn-unused", "-Ywarn-unused-import"),
  scalacOptions in(Test, console) := (scalacOptions in(Compile, console)).value
)
