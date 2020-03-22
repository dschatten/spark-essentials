package davepart1recap

object ScalaRecap extends App {
  //declare a value
  val aBoolean: Boolean = false

  //expressions
  val anIfExpression = if(2 > 3) "bigger" else "smaller"

  //instructions vs expressions
  //instructions - imperative language building blocks
  //expressions - functional program building blocks

  //an expression; returns a unit
  //Unit basically means 'no meaningful value'; like void in other languages
  val theUnit = println("Dave Print")

  //define a function
  def myFunction(x: Int) = 42

  //OOP - Single inheritence
  class Animal
  class Dog extends Animal
  class Golden extends Dog


  //Traits are like interfaces
  trait Carnivore {
    def eat(animal: Animal)
    def stringAnimal(animal: Animal): String ={
      animal.toString
    }
  }

  trait AnotherCarnivoreTrait{

  }

  //must implement the 'eat' method since it's not defined in the trait
  //does not need to implement stringAnimal, since it is defined in the trait
  //here we also see how to implement multiple traits
  class Crocodile extends Animal with Carnivore with AnotherCarnivoreTrait {
    override def eat(animal: Animal): Unit = {}
  }

    //'object' keyword defines the singleton pattern very easily
    object MySingleton

    //you can also make a singleton from the Carnivore trait
    //two objects in the same package are called 'companions' and have some special features in Scala
    object Carnivore

    //generics
    trait MyList[A]

    //method notation
    //'+' is actually a method in scala
    val x = 1 + 2
    val y = 1.+(2)

    //functional programming
    //standard lambda notation
    //these are syntactic sugar for another way of doing it.
    //In IntelliJ - select following expression and 'Desugar' if you want to see it
    val incrementerDesugared: Function1[Int, Int] = (x: Int) => x.+(1)

    //this is lambda notation, also called an 'anonymous function'
    //but this actually refers to the FUnction1 trait
    val incrementer: Int => Int = x => x + 1
    val incremented = incrementer(1)

    //map, flatMap, and filter are all higher order functions
    val processedList = List(1,2,3).map(incrementer)

    //Pattern Matching is a rich feature
    //'Any' class is at the root of scala class hierarchy
    //All scala classes inherit from 'Any' directly or indirecly
    val unknown: Any = 45
    val matchit = unknown match {
      case 1 => "bear"
      case 2 => "lion"
      case _ => "other animal"
    }

     //try-catch
  try {
    throw new NullPointerException
  }catch {
      case _: NullPointerException => "some returned value"
      case _=> "something else"

  }

  //Futures - abstract away the operations on separate threads
  //These are important features
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.Future

  val aFuture = Future {
    //run some expensive computation on another value
    //and return a value
    42
  }

  //import scala Failure and Success classes
  import scala.util.{Failure, Success}

  //Do something once the future is complete
  aFuture.onComplete {
    case Success(meaningOfLife) => println(s"I've found the $meaningOfLife")    //string interpolation with variable
    case Failure(anException) => println(s"I've failed $anException")    //string interpolation with variable
  }

  //Partial functions
  //Doesn't provide an answer for every input value
  //You can query the function to see which values it can handle
  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 43
    case 8 => 88
    case _ => 999
  }

  //String interpolation
  //Implicits - Advanced Scala
  //These are auto-injected by the compiler
  def methodWithImplicitArgument(implicit x: Int) = x + 43
  implicit val implicitInt = 67

  //compiler basically figures out which argument to inject into the method call
  //in this case, it is the same as explicitly calling like this: methodWithImplicitArgument(67)
  val implicitCall = methodWithImplicitArgument

  //case class - lightweight data structure that already has certain utility methods added by the compiler
  case class Person(name: String){
    def greet = println(s"Hi, my name is $name")  //string interpolation
  }

  //implicit conversions - implicit defs
  //here we define a conversion from a string to the Person class
  implicit def fromStringToPerson(name: String) = Person(name)

  //check this out!!!
  //although "Bob" is just a generic string, the compiler checks for possible
  //interpolations, and finds that it can implicitly convert to class Person using
  //the implicit method fromStringToPerson
  "Bob".greet

  //implicit conversion - implicit classes
  implicit class Doggie(name: String){
    def bark = println(name)
  }

  //once again, the compiler can implicitly convert the string to Doggie
  //and you can call the 'bark' method on a string even though it's not explicitly
  //an instance of the Doggie class
  "MyDog".bark

}
