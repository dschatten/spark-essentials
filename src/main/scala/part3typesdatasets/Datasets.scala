package part3typesdatasets

import java.sql.Date

import org.apache.spark.sql.{DataFrame, Dataset, Encoders, SparkSession}
import org.apache.spark.sql.functions._


object Datasets extends App {

  val spark = SparkSession.builder()
    .appName("Datasets")
    .config("spark.master", "local")
    .getOrCreate()

  val numbersDF: DataFrame = spark.read
    .format("csv")
    .option("header", "true")
    .option("inferSchema", "true")
    .load("src/main/resources/data/numbers.csv")

  numbersDF.printSchema()

  // convert a DF to a Dataset
  implicit val intEncoder = Encoders.scalaInt
  val numbersDS: Dataset[Int] = numbersDF.as[Int]

  //now, you can apply operations
  //note this DS has only one column, so this is a simple operation
  numbersDS.filter(_ < 100)

  // dataset of a complex type
  // 1 - define your case class
  case class Car(
                Name: String,
                Miles_per_Gallon: Option[Double],  //Option means it could be nullable
                Cylinders: Long,
                Displacement: Double,
                Horsepower: Option[Long],   //Option means it could be nullable
                Weight_in_lbs: Long,
                Acceleration: Double,
//DRS - Temporary hack.     Date parse is failing with an error, just use string temporarily
//                Year: Date,
                Year: String,
                Origin: String
                )

//  {"Name":"chevrolet chevelle malibu", "Miles_per_Gallon":18, "Cylinders":8, "Displacement":307, "Horsepower":130, "Weight_in_lbs":3504, "Acceleration":12, "Year":"1970-01-01", "Origin":"USA"}

  // 2 - read the DF from the file
  def readDF(filename: String) = spark.read
    .option("inferSchema", "true")
    .json(s"src/main/resources/data/$filename")

  val carsDF = readDF("cars.json")

  // 3 - define an encoder (importing the implicits)
  import spark.implicits._
  // 4 - convert the DF to DS
  val carsDS = carsDF.as[Car]

  // Now you can use standard DS collection functions
  numbersDS.filter(_ < 100)

  // map, flatMap, fold, reduce, for comprehensions ...
  val carNamesDS = carsDS.map(car => car.Name.toUpperCase())

  /**
    * Exercises
    *
    * 1. Count how many cars we have
    * 2. Count how many POWERFUL cars we have (HP > 140)
    * 3. Average HP for the entire dataset
    */
  println(s"Count of cars: $carsDS.count()")

  val powerfulCars = carsDS.filter(_.Horsepower.getOrElse(0L) > 140).count
  println(s"Count of powerful cars: $powerfulCars")

//  val horsepower = carsDS.map(_.Horsepower)

  // 1
  val carsCount = carsDS.count
  println(carsCount)

  // 2
  println(carsDS.filter(_.Horsepower.getOrElse(0L) > 140).count)

  // 3
  println(carsDS.map(_.Horsepower.getOrElse(0L)).reduce(_ + _) / carsCount)

  // also can use the DataFrame functions!
  carsDS.select(avg(col("Horsepower")))


  // Joins
  case class Guitar(id: Long, make: String, model: String, guitarType: String)
  case class GuitarPlayer(id: Long, name: String, guitars: Seq[Long], band: Long)
  case class Band(id: Long, name: String, hometown: String, year: Long)

  val guitarsDS = readDF("guitars.json").as[Guitar]
  val guitarPlayersDS = readDF("guitarPlayers.json").as[GuitarPlayer]
  val bandsDS = readDF("bands.json").as[Band]

  val guitarPlayerBandsDS: Dataset[(GuitarPlayer, Band)] = guitarPlayersDS.joinWith(bandsDS, guitarPlayersDS.col("band") === bandsDS.col("id"), "inner")

  /**
    * Exercise: join the guitarsDS and guitarPlayersDS, in an outer join
    * (hint: use array_contains)
    */

    println("Guitars and Guitar Players joined: ")
//    val joinedDS: Dataset[(GuitarPlayer, Guitar)] =
      guitarPlayersDS.joinWith(guitarsDS, array_contains(guitarPlayersDS.col("guitars"),
        guitarsDS.col("id")), joinType = "outer").show()



  /*
  guitarPlayersDS
    .joinWith(guitarsDS, array_contains(guitarPlayersDS.col("guitars"), guitarsDS.col("id")), "outer")
    .show()

  // Grouping DS

  val carsGroupedByOrigin = carsDS
    .groupByKey(_.Origin)
    .count()
    .show()*/

  // joins and groups are WIDE transformations, will involve SHUFFLE operations
}
