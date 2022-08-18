import Dependencies.Version._

name := "pureconfig-magnolia"

crossScalaVersions := Seq(scala31)

libraryDependencies ++= Seq(
  "com.softwaremill.magnolia1_3" %% "magnolia" % "1.1.5"
)

developers := List(
  Developer("ruippeixotog", "Rui Gonçalves", "ruippeixotog@gmail.com", url("https://github.com/ruippeixotog"))
)
