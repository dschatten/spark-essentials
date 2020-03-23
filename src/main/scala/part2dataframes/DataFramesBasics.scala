package part2dataframes

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types._

object DataFramesBasics extends App {

  // creating a SparkSession
  //DRS - you need a SparkSession for most things

  val spark = SparkSession.builder()
    .appName("DataFrames Basics")
    .config("spark.master", "local")      //You can add multiple config calls if you like
    .getOrCreate()

  // reading a DF
  //DRS - See comment on automatically inferring DataFrame schema
  val firstDF = spark.read
    .format("json")
    .option("inferSchema", "true")    //DRS - AUTOMATICALLY INFERRING THE SCHEMA CAN SCREW THINGS UP
    .load("src/main/resources/data/cars.json")

  // showing a DF
  firstDF.show()
  firstDF.printSchema()

  // get rows
  firstDF.take(10).foreach(println)

  // spark types
  val longType = LongType

  // schema
  //DRS - this is if you want to manually define the schema.
  //DRS - in practice, Spark can infer the schema as it has above, but it might screw it up
  val carsSchema = StructType(Array(
    StructField("Name", StringType),
    StructField("Miles_per_Gallon", DoubleType),
    StructField("Cylinders", LongType),
    StructField("Displacement", DoubleType),
    StructField("Horsepower", LongType),
    StructField("Weight_in_lbs", LongType),
    StructField("Acceleration", DoubleType),
    StructField("Year", StringType),
    StructField("Origin", StringType)
  ))

  // obtain a schema
  val carsDFSchema = firstDF.schema

  // read a DF with your schema
  //DRS - Here we pass in our own schema so it is not inferred
  val carsDFWithSchema = spark.read
    .format("json")
    .schema(carsDFSchema)
    .load("src/main/resources/data/cars.json")

  // create rows by hand
  val myRow = Row("chevrolet chevelle malibu",18,8,307,130,3504,12.0,"1970-01-01","USA")   //Can pass in whatever data you want, rows can contain anything.

  // create DF from tuples
  val cars = Seq(
    ("chevrolet chevelle malibu",18,8,307,130,3504,12.0,"1970-01-01","USA"),
    ("buick skylark 320",15,8,350,165,3693,11.5,"1970-01-01","USA"),
    ("plymouth satellite",18,8,318,150,3436,11.0,"1970-01-01","USA"),
    ("amc rebel sst",16,8,304,150,3433,12.0,"1970-01-01","USA"),
    ("ford torino",17,8,302,140,3449,10.5,"1970-01-01","USA"),
    ("ford galaxie 500",15,8,429,198,4341,10.0,"1970-01-01","USA"),
    ("chevrolet impala",14,8,454,220,4354,9.0,"1970-01-01","USA"),
    ("plymouth fury iii",14,8,440,215,4312,8.5,"1970-01-01","USA"),
    ("pontiac catalina",14,8,455,225,4425,10.0,"1970-01-01","USA"),
    ("amc ambassador dpl",15,8,390,190,3850,8.5,"1970-01-01","USA")
  )
  val manualCarsDF = spark.createDataFrame(cars) // schema auto-inferred

  // note: DFs have schemas, rows do not

  // create DFs with implicits
  import spark.implicits._
  val manualCarsDFWithImplicits = cars.toDF("Name", "MPG", "Cylinders", "Displacement", "HP", "Weight", "Acceleration", "Year", "CountryOrigin")


  /**
    * Exercise:
    * 1) Create a manual DF describing smartphones
    *   - make
    *   - model
    *   - screen dimension
    *   - camera megapixels
    *
    * 2) Read another file from the data/ folder, e.g. movies.json
    *   - print its schema
    *   - count the number of rows, call count()
    */

  // 1
  val smartphones = Seq(
    ("Samsung", "Galaxy S10", "Android", 12),
    ("Apple", "iPhone X", "iOS", 13),
    ("Nokia", "3310", "THE BEST", 0)
  )

  val smartphonesDF = smartphones.toDF("Make", "Model", "Platform", "CameraMegapixels")
  smartphonesDF.show()

  // 2
  val moviesDF = spark.read
    .format("json")
    .option("inferSchema", "true")
    .load("src/main/resources/data/movies.json")
  moviesDF.printSchema()
  println(s"The Movies DF has ${moviesDF.count()} rows")


  //DRS - Manually create some clinical data
  val clinicalData = Seq(
    (1234, "MI", "good", "1/1/2020"),
    (12345, "MI", "bad", "1/1/2020"),
    (123456, "RHEUM", "good", "1/2/2020")
  )

  var clinicalDataDF = clinicalData.toDF(colNames = "patient_id", "disease", "outcome", "date_assessed")
  println(clinicalDataDF)

  //DRS - Create some clinical data from a JSON file with schema
  val clinicalSchema = StructType(Array(
    StructField("patient_id", LongType),
    StructField("disease", StringType),
    StructField("outcome", StringType),
    StructField("date_assessed", DateType),
  ))

  val clinicalDataDFWithSchema = spark.read
    .format("json")
    .schema(clinicalSchema)
    .load("src/main/resources/data/clinicalData.json")

  println("Clinical Data With Schema: ")
  clinicalDataDFWithSchema.show()
  clinicalDataDFWithSchema.printSchema()
  clinicalDataDFWithSchema.take(10).foreach(println)
}
