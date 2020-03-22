package playground

import com.github.nscala_time.time.Imports._

object DavesPlayground extends App {

  //DateTime libraries with nScala wrapper for Joda Time
  //https://stackoverflow.com/questions/3614380/whats-the-standard-way-to-work-with-dates-and-times-in-scala-should-i-use-java
  //https://github.com/nscala-time/nscala-time
  val currentTime = DateTime.now()

  println("Current Day of month " +currentTime.dayOfMonth())
  println("Current Day of week " + currentTime.dayOfWeek())
  println("Current Day of year " + currentTime.dayOfYear())
  println("Current Day millis " + currentTime.millis)
}
