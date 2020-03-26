package part2dataframes

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, column, expr}

object ColumnsAndExpressions extends App {

  val spark = SparkSession.builder()
    .appName("DF Columns and Expressions")
    .config("spark.master", "local")
    .getOrCreate()

  val carsDF = spark.read
    .option("inferSchema", "true")
    .json("src/main/resources/data/cars.json")

  // Columns
  val firstColumn = carsDF.col("Name")

  // selecting (projecting) - this is called a "PROJECTION"
  //DRS - This creates a new DataFrame.    Remember, DataFrames are immutable.
  val carNamesDF = carsDF.select(firstColumn)

  // various select methods
  import spark.implicits._
  carsDF.select(
    carsDF.col("Name"),
    col("Acceleration"),
    column("Weight_in_lbs"),
    'Year, // Scala Symbol, auto-converted to column
    $"Horsepower", // fancier interpolated string, returns a Column object
    expr("Origin") // EXPRESSION
  )

  // select with plain column names
  carsDF.select("Name", "Year")

  // EXPRESSIONS
  val simplestExpression = carsDF.col("Weight_in_lbs")
  val weightInKgExpression = carsDF.col("Weight_in_lbs") / 2.2

  val carsWithWeightsDF = carsDF.select(
    col("Name"),
    col("Weight_in_lbs"),
    weightInKgExpression.as("Weight_in_kg"),
    expr("Weight_in_lbs / 2.2").as("Weight_in_kg_2")
  )

  // selectExpr
  val carsWithSelectExprWeightsDF = carsDF.selectExpr(
    "Name",
    "Weight_in_lbs",
    "Weight_in_lbs / 2.2"
  )

  // DF processing

  // adding a column
  val carsWithKg3DF = carsDF.withColumn("Weight_in_kg_3", col("Weight_in_lbs") / 2.2)
  // renaming a column
  val carsWithColumnRenamed = carsDF.withColumnRenamed("Weight_in_lbs", "Weight in pounds")
  // careful with column names
  carsWithColumnRenamed.selectExpr("`Weight in pounds`")
  // remove a column
  carsWithColumnRenamed.drop("Cylinders", "Displacement")

  // filtering - note that equals is === and not equals is =!= so that it doesn't conflict with standard scala operators
  val europeanCarsDF = carsDF.filter(col("Origin") =!= "USA")
  val europeanCarsDF2 = carsDF.where(col("Origin") =!= "USA")
  // filtering with expression strings
  val americanCarsDF = carsDF.filter("Origin = 'USA'")
  // chain filters
  val americanPowerfulCarsDF = carsDF.filter(col("Origin") === "USA").filter(col("Horsepower") > 150)
  val americanPowerfulCarsDF2 = carsDF.filter(col("Origin") === "USA" and col("Horsepower") > 150)
  val americanPowerfulCarsDF3 = carsDF.filter("Origin = 'USA' and Horsepower > 150")

  // unioning = adding more rows
  val moreCarsDF = spark.read.option("inferSchema", "true").json("src/main/resources/data/more_cars.json")
  val allCarsDF = carsDF.union(moreCarsDF) // works if the DFs have the same schema

  // distinct values
  val allCountriesDF = carsDF.select("Origin").distinct()

  /**
    * Exercises
    *
    * 1. Read the movies DF and select 2 columns of your choice
    * 2. Create another column summing up the total profit of the movies = US_Gross + Worldwide_Gross + DVD sales
    * 3. Select all COMEDY movies with IMDB rating above 6
    *
    * Use as many versions as possible
    */

  val drsMoviesDF = spark.read
    .option("inferSchema", "true")
    .json("src/main/resources/data/movies.json")


  val drsMoviesSubsetDF = drsMoviesDF.select(
    col("Title"),
    col("US_Gross"),
    expr("Worldwide_Gross"),
    expr("US_DVD_Sales")
  ).withColumn(
    "Total_Profit", col("US_Gross") + col("Worldwide_Gross")
  )
      //  val carsWithKg3DF = carsDF.withColumn("Weight_in_kg_3", col("Weight_in_lbs") / 2.2)
  println("Dave's New Movies DF")
  drsMoviesSubsetDF.show()

  println("Dave's comedies with IMDB rating > 6")
  val drsMoviesBestDF = drsMoviesDF.filter(col("Major_Genre") === "Comedy").filter(col("IMDB_Rating") > 6)
  drsMoviesBestDF.show()


  val moviesDF = spark.read.option("inferSchema", "true").json("src/main/resources/data/movies.json")
  moviesDF.show()

  // 1
  val moviesReleaseDF = moviesDF.select("Title", "Release_Date")
  val moviesReleaseDF2 = moviesDF.select(
    moviesDF.col("Title"),
    col("Release_Date"),
    $"Major_Genre",
    expr("IMDB_Rating")
  )
  val moviesReleaseDF3 = moviesDF.selectExpr(
    "Title", "Release_Date"
  )

  // 2
  val moviesProfitDF = moviesDF.select(
    col("Title"),
    col("US_Gross"),
    col("Worldwide_Gross"),
    col("US_DVD_Sales"),
    (col("US_Gross") + col("Worldwide_Gross")).as("Total_Gross")
  )

  val moviesProfitDF2 = moviesDF.selectExpr(
    "Title",
    "US_Gross",
    "Worldwide_Gross",
    "US_Gross + Worldwide_Gross as Total_Gross"
  )

  val moviesProfitDF3 = moviesDF.select("Title", "US_Gross", "Worldwide_Gross")
    .withColumn("Total_Gross", col("US_Gross") + col("Worldwide_Gross"))

  // 3
  val atLeastMediocreComediesDF = moviesDF.select("Title", "IMDB_Rating")
    .where(col("Major_Genre") === "Comedy" and col("IMDB_Rating") > 6)

  val comediesDF2 = moviesDF.select("Title", "IMDB_Rating")
    .where(col("Major_Genre") === "Comedy")
    .where(col("IMDB_Rating") > 6)

  val comediesDF3 = moviesDF.select("Title", "IMDB_Rating")
    .where("Major_Genre = 'Comedy' and IMDB_Rating > 6")

  comediesDF3.show
}
