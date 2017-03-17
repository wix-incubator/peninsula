package com.wix.peninsula

import com.wix.peninsula.domain.Person
import com.wix.peninsula.exceptions.{JsonElementIsNullException, JsonPathDoesntExistException, UnexpectedJsonElementException}
import org.specs2.mutable.SpecificationWithJUnit

import scala.util.Success

class ExtractionTest extends SpecificationWithJUnit {

  "extract" should {

    "extract case class from an object" in {
      val json = Json.parse("""{ "id": 1, "name": "Hello"}""")
      json.extract[Person] mustEqual Person(id = 1, name = "Hello")
    }

    "extract case class from path in an object" in {
      val json = Json.parse("""{ "person": { "id": 1, "name": "Hello"} }""")
      json.extract[Person]("person") mustEqual Person(id = 1, name = "Hello")
    }

    "extract a sequence of case classes from a json array" in {
      val json = Json.parse("""[{ "id": 1, "name": "Hello"}, {"id": 2, "name": "Goodbye"}] """)
      json.extract[Seq[Person]] mustEqual Seq(Person(id = 1, name = "Hello"), Person(id = 2, name = "Goodbye"))
    }

    "throw an exception if element is null" in {
      val json = Json.parse("""{"person": null}""")
      json.extract[Person]("person") must throwAn[JsonElementIsNullException]
    }

    "throw an exception if path doesn't exist" in {
      val json = Json.parse("""{ "person": { "id": 1, "name": "Hello"} }""")
      json.extract[Person]("customer") must throwAn[JsonPathDoesntExistException]
    }

  }

  "extractTry" should {

    "return case class from path in an object" in {
      val json = Json.parse("""{ "person": { "id": 1, "name": "Hello"} }""")
      json.extractTry[Person]("person") mustEqual Success(Person(id = 1, name = "Hello"))
    }

    "return Failure if element is null" in {
      val json = Json.parse("""{"person": null}""")
      json.extractTry[Person]("person") must beFailedTry.withThrowable[JsonElementIsNullException]
    }

    "return Failure if path doesn't exist" in {
      val json = Json.parse("""{ "person": { "id": 1, "name": "Hello"} }""")
      json.extractTry[Person]("customer") must beFailedTry.withThrowable[JsonPathDoesntExistException]
    }

  }

  "extractBoolean" should {

    "extract value in case it is boolean in JSON" in {
      val json = Json.parse("""{"is_checked": false}""")
      json.extractBoolean("is_checked") must_== false
    }

    "throw an exception if element is null" in {
      val json = Json.parse("""{"is_checked": null}""")
      json.extractBoolean("is_checked") must throwAn[JsonElementIsNullException]
    }

    "throw an exception if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractBoolean("root.is_checked") must throwAn[JsonPathDoesntExistException]
    }

    "throw an exception if type of JSON element doesn't match boolean" in {
      val json = Json.parse("""{"is_checked": "false"}""")
      json.extractBoolean("is_checked") must throwAn[UnexpectedJsonElementException]
    }

  }

  "extractBooleanTry" should {

    "return Success(value) in case it is boolean in JSON" in {
      val json = Json.parse("""{"is_checked": false}""")
      json.extractBooleanTry("is_checked") must_== Success(false)
    }

    "return Failure if element is null" in {
      val json = Json.parse("""{"is_checked": null}""")
      json.extractBooleanTry("is_checked") must beFailedTry.withThrowable[JsonElementIsNullException]
    }

    "return Failure if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractBooleanTry("root.is_checked") must beFailedTry.withThrowable[JsonPathDoesntExistException]
    }

    "return Failure if type of JSON element doesn't match boolean" in {
      val json = Json.parse("""{"is_checked": "false"}""")
      json.extractBooleanTry("is_checked") must beFailedTry.withThrowable[UnexpectedJsonElementException]
    }

  }

  "extractInt" should {

    "extract value in case it matches integer in JSON" in {
      val json = Json.parse("""{"num_children": 42}""")
      json.extractInt("num_children") must_== 42
    }

    "throw an exception if number exceeds integer's max value limit" in {
      val json = Json.parse("""{"num_children": 213412341234123}""")
      json.extractInt("num_children") must throwAn[UnexpectedJsonElementException]
    }

    "throw an exception if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractInt("num_children") must throwAn[JsonElementIsNullException]
    }

    "throw an exception if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractInt("root.num_children") must throwAn[JsonPathDoesntExistException]
    }

    "throw an exception if type of JSON element doesn't match number" in {
      val json = Json.parse("""{"num_children": 42.0}""")
      json.extractInt("num_children") must throwAn[UnexpectedJsonElementException]
    }

    "throw an exception if type of JSON element doesn't match number" in {
      val json = Json.parse("""{"num_children": "42"}""")
      json.extractInt("num_children") must throwAn[UnexpectedJsonElementException]
    }

  }

  "extractIntTry" should {

    "extract Success(value) in case it matches integer in JSON" in {
      val json = Json.parse("""{"num_children": 42}""")
      json.extractIntTry("num_children") must_== Success(42)
    }

    "return Failure if number exceeds integer's max value limit" in {
      val json = Json.parse("""{"num_children": 213412341234123}""")
      json.extractIntTry("num_children") must beFailedTry.withThrowable[UnexpectedJsonElementException]
    }

    "return Failure if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractIntTry("num_children") must beFailedTry.withThrowable[JsonElementIsNullException]
    }

    "return Failure if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractIntTry("root.num_children") must beFailedTry.withThrowable[JsonPathDoesntExistException]
    }

    "return Failure if type of JSON element doesn't match number" in {
      val json = Json.parse("""{"num_children": 42.0}""")
      json.extractIntTry("num_children") must beFailedTry.withThrowable[UnexpectedJsonElementException]
    }

    "return Failure if type of JSON element doesn't match number" in {
      val json = Json.parse("""{"num_children": "42"}""")
      json.extractIntTry("num_children") must beFailedTry.withThrowable[UnexpectedJsonElementException]
    }

  }

  "extractBigInt" should {

    "extract value in case it matches big integer number in JSON" in {
      val json = Json.parse("""{"num_children": 2134123412341234}""")
      json.extractBigInt("num_children") must_== BigInt(2134123412341234L)
    }

    "throw an exception if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractBigInt("num_children") must throwAn[JsonElementIsNullException]
    }

    "throw an exception if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractBigInt("root.num_children") must throwAn[JsonPathDoesntExistException]
    }

    "throw an exception if type of JSON element doesn't match integer" in {
      val json = Json.parse("""{"num_children": 42.0}""")
      json.extractBigInt("num_children") must throwAn[UnexpectedJsonElementException]
    }

    "throw an exception if type of JSON element doesn't match integer" in {
      val json = Json.parse("""{"num_children": "42"}""")
      json.extractBigInt("num_children") must throwAn[UnexpectedJsonElementException]
    }

  }

  "extractBigIntOpt" should {

    "extract Success(value) in case it matches big integer number in JSON" in {
      val json = Json.parse("""{"num_children": 2134123412341234}""")
      json.extractBigIntTry("num_children") must_== Success(BigInt(2134123412341234L))
    }

    "return Failure if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractBigIntTry("num_children") must beFailedTry.withThrowable[JsonElementIsNullException]
    }

    "return Failure if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractBigIntTry("root.num_children") must beFailedTry.withThrowable[JsonPathDoesntExistException]
    }

    "return Failure if type of JSON element doesn't match integer" in {
      val json = Json.parse("""{"num_children": 42.0}""")
      json.extractBigIntTry("num_children") must beFailedTry.withThrowable[UnexpectedJsonElementException]
    }

    "return Failure if type of JSON element doesn't match integer" in {
      val json = Json.parse("""{"num_children": "42"}""")
      json.extractBigIntTry("num_children") must beFailedTry.withThrowable[UnexpectedJsonElementException]
    }

  }

  "extractLong" should {

    "extract value in case it matches long in JSON" in {
      val json = Json.parse("""{"num_children": 22222222222}""")
      json.extractLong("num_children") must_== 22222222222L
    }

    "throw an exception if number exceeds long's max value limit" in {
      val json = Json.parse("""{"num_children": 21341234123412341234}""")
      json.extractLong("num_children") must throwAn[UnexpectedJsonElementException]
    }

    "throw an exception if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractLong("num_children") must throwAn[JsonElementIsNullException]
    }

    "throw an exception if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractLong("root.num_children") must throwAn[JsonPathDoesntExistException]
    }

    "throw an exception if type of JSON element doesn't match number" in {
      val json = Json.parse("""{"num_children": 22222222222.0}""")
      json.extractLong("num_children") must throwAn[UnexpectedJsonElementException]
    }

    "throw an exception if type of JSON element doesn't match number" in {
      val json = Json.parse("""{"num_children": "22222222222"}""")
      json.extractLong("num_children") must throwAn[UnexpectedJsonElementException]
    }

  }

  "extractLongTry" should {

    "extract Success(value) in case it matches long in JSON" in {
      val json = Json.parse("""{"num_children": 22222222222}""")
      json.extractLongTry("num_children") must_== Success(22222222222L)
    }

    "return Failure if number exceeds long's max value limit" in {
      val json = Json.parse("""{"num_children": 21341234123412341234}""")
      json.extractLongTry("num_children") must beFailedTry.withThrowable[UnexpectedJsonElementException]
    }

    "return Failure if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractLongTry("num_children") must beFailedTry.withThrowable[JsonElementIsNullException]
    }

    "return Failure if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractLongTry("root.num_children") must beFailedTry.withThrowable[JsonPathDoesntExistException]
    }

    "return Failure if type of JSON element doesn't match number" in {
      val json = Json.parse("""{"num_children": 22222222222.0}""")
      json.extractLongTry("num_children") must beFailedTry.withThrowable[UnexpectedJsonElementException]
    }

    "return Failure if type of JSON element doesn't match number" in {
      val json = Json.parse("""{"num_children": "22222222222"}""")
      json.extractLongTry("num_children") must beFailedTry.withThrowable[UnexpectedJsonElementException]
    }

  }

  "extractDouble" should {

    "extract value in case it matches double in JSON" in {
      val json = Json.parse("""{"num_children": 42.0}""")
      json.extractDouble("num_children") must_== 42.0
    }

    "throw an exception if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractDouble("num_children") must throwAn[JsonElementIsNullException]
    }

    "throw an exception if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractDouble("root.num_children") must throwAn[JsonPathDoesntExistException]
    }

    "throw an exception if type of JSON element doesn't match double" in {
      val json = Json.parse("""{"num_children": 22222222222}""")
      json.extractDouble("num_children") must throwAn[UnexpectedJsonElementException]
    }

    "throw an exception if type of JSON element doesn't match double" in {
      val json = Json.parse("""{"num_children": "22222222222.0"}""")
      json.extractDouble("num_children") must throwAn[UnexpectedJsonElementException]
    }

  }

  "extractDoubleTry" should {

    "extract Success(value) in case it matches double in JSON" in {
      val json = Json.parse("""{"num_children": 42.0}""")
      json.extractDoubleTry("num_children") must_== Success(42.0)
    }

    "throw an exception if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractDoubleTry("num_children") must beFailedTry.withThrowable[JsonElementIsNullException]
    }

    "throw an exception if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractDoubleTry("root.num_children") must beFailedTry.withThrowable[JsonPathDoesntExistException]
    }

    "throw an exception if type of JSON element doesn't match double" in {
      val json = Json.parse("""{"num_children": 22222222222}""")
      json.extractDoubleTry("num_children") must beFailedTry.withThrowable[UnexpectedJsonElementException]
    }

    "throw an exception if type of JSON element doesn't match double" in {
      val json = Json.parse("""{"num_children": "22222222222.0"}""")
      json.extractDoubleTry("num_children") must beFailedTry.withThrowable[UnexpectedJsonElementException]
    }

  }

  "extractBigDecimal" should {

    "throw an exception if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractBigDecimal("num_children") must throwAn[JsonElementIsNullException]
    }

    "throw an exception if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractBigDecimal("root.num_children") must throwAn[JsonPathDoesntExistException]
    }

    "throw an exception if type of JSON element doesn't match decimal" in {
      val json = Json.parse("""{"num_children": 22222222222}""")
      json.extractBigDecimal("num_children") must throwAn[UnexpectedJsonElementException]
    }

    "throw an exception if type of JSON element doesn't match decimal" in {
      val json = Json.parse("""{"num_children": "22222222222.0"}""")
      json.extractBigDecimal("num_children") must throwAn[UnexpectedJsonElementException]
    }

  }

  "extractBigDecimalTry" should {

    "throw an exception if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractBigDecimalTry("num_children") must beFailedTry.withThrowable[JsonElementIsNullException]
    }

    "throw an exception if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractBigDecimalTry("root.num_children") must beFailedTry.withThrowable[JsonPathDoesntExistException]
    }

    "throw an exception if type of JSON element doesn't match decimal" in {
      val json = Json.parse("""{"num_children": 22222222222}""")
      json.extractBigDecimalTry("num_children") must beFailedTry.withThrowable[UnexpectedJsonElementException]
    }

    "throw an exception if type of JSON element doesn't match decimal" in {
      val json = Json.parse("""{"num_children": "22222222222.0"}""")
      json.extractBigDecimalTry("num_children") must beFailedTry.withThrowable[UnexpectedJsonElementException]
    }

  }

  "extractString" should {

    "extract value in case it is string in JSON" in {
      val json = Json.parse("""{"check": "hello"}""")
      json.extractString("check") must_== "hello"
    }

    "throw an exception if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractString("num_children") must throwAn[JsonElementIsNullException]
    }

    "throw an exception if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractString("root.num_children") must throwAn[JsonPathDoesntExistException]
    }

    "throw an exception if type of JSON element is not a string" in {
      val json = Json.parse("""{"root": 10}""")
      json.extractString("root") must throwAn[UnexpectedJsonElementException]
    }

  }

  "extractStringTry" should {

    "extract Success(value) in case it is string in JSON" in {
      val json = Json.parse("""{"check": "hello"}""")
      json.extractStringTry("check") must_== Success("hello")
    }

    "throw an exception if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractStringTry("num_children") must beFailedTry.withThrowable[JsonElementIsNullException]
    }

    "throw an exception if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractStringTry("root.num_children") must beFailedTry.withThrowable[JsonPathDoesntExistException]
    }

    "throw an exception if type of JSON element is not a string" in {
      val json = Json.parse("""{"root": 10}""")
      json.extractStringTry("root") must beFailedTry.withThrowable[UnexpectedJsonElementException]
    }

  }

}
