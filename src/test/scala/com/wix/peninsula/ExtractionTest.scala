package com.wix.peninsula

import com.wix.peninsula.domain.Person
import com.wix.peninsula.exceptions.{JsonElementIsNullException, JsonPathDoesntExistException, UnexpectedJsonElementException}
import org.specs2.mutable.SpecificationWithJUnit

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

  "extractOpt" should {

    "extract case class from path in an object" in {
      val json = Json.parse("""{ "person": { "id": 1, "name": "Hello"} }""")
      json.extractOpt[Person]("person") mustEqual Some(Person(id = 1, name = "Hello"))
    }

    "return None if element is null" in {
      val json = Json.parse("""{"person": null}""")
      json.extractOpt[Person]("person") must_== None
    }

    "return None if path doesn't exist" in {
      val json = Json.parse("""{ "person": { "id": 1, "name": "Hello"} }""")
      json.extractOpt[Person]("customer") must_== None
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

  "extractBooleanOpt" should {

    "return Some(value) in case it is boolean in JSON" in {
      val json = Json.parse("""{"is_checked": false}""")
      json.extractBooleanOpt("is_checked") must_== Some(false)
    }

    "return None if element is null" in {
      val json = Json.parse("""{"is_checked": null}""")
      json.extractBooleanOpt("is_checked") must_== None
    }

    "return None if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractBooleanOpt("root.is_checked") must_== None
    }

    "return None if type of JSON element doesn't match boolean" in {
      val json = Json.parse("""{"is_checked": "false"}""")
      json.extractBooleanOpt("is_checked") must_== None
    }

  }

  "extractInt" should {

    "extract value in case it matches integer in JSON" in {
      val json = Json.parse("""{"num_children": 42}""")
      json.extractInt("num_children") must_== 42
    }

    "extract and convert decimal value to integer if it can be done losslessly" in {
      val json = Json.parse("""{"num_children": 42.0}""")
      json.extractInt("num_children") must_== 42
    }

    "throw an exception if number exceeds integer's max value limit" in {
      val json = Json.parse("""{"num_children": 21341234123412341234}""")
      json.extractInt("num_children") must throwAn[UnexpectedJsonElementException]
    }

    "throw an exception if integer part of decimal number exceeds integer's max value limit" in {
      val json = Json.parse("""{"num_children": 21341234123412341234.0}""")
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
      val json = Json.parse("""{"num_children": "42"}""")
      json.extractInt("num_children") must throwAn[UnexpectedJsonElementException]
    }

  }

  "extractIntOpt" should {

    "extract value in case it matches integer in JSON" in {
      val json = Json.parse("""{"num_children": 42}""")
      json.extractIntOpt("num_children") must_== Some(42)
    }

    "extract and convert decimal value to integer if it can be done losslessly" in {
      val json = Json.parse("""{"num_children": 42.0}""")
      json.extractIntOpt("num_children") must_== Some(42)
    }

    "return None if number exceeds integer's max value limit" in {
      val json = Json.parse("""{"num_children": 21341234123412341234}""")
      json.extractIntOpt("num_children") must_== None
    }

    "return None if integer part of decimal number exceeds integer's max value limit" in {
      val json = Json.parse("""{"num_children": 21341234123412341234.0}""")
      json.extractIntOpt("num_children") must_== None
    }

    "return None if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractIntOpt("num_children") must_== None
    }

    "return None if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractIntOpt("root.num_children") must_== None
    }

    "return None if type of JSON element doesn't match number" in {
      val json = Json.parse("""{"num_children": "42"}""")
      json.extractIntOpt("num_children") must_== None
    }

  }

  "extractBigInt" should {

    "extract value in case it matches long in JSON" in {
      val json = Json.parse("""{"num_children": 2134123412341234}""")
      json.extractBigInt("num_children") must_== 2134123412341234L
    }

    "extract and convert decimal value to integer if it can be done losslessly" in {
      val json = Json.parse("""{"num_children": 42.0}""")
      json.extractBigInt("num_children") must_== 42
    }

    "extract value even if number exceeds long max value limit" in {
      val json = Json.parse("""{"num_children": 21341234123412341234}""")
      json.extractBigInt("num_children") must_== BigInt("21341234123412341234")
    }

    "throw an exception if integer part of decimal number exceeds long max value limit" in {
      val json = Json.parse("""{"num_children": 21341234123412341234.0}""")
      json.extractBigInt("num_children") must throwAn[UnexpectedJsonElementException]
    }

    "throw an exception if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractBigInt("num_children") must throwAn[JsonElementIsNullException]
    }

    "throw an exception if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractBigInt("root.num_children") must throwAn[JsonPathDoesntExistException]
    }

    "throw an exception if type of JSON element doesn't match number" in {
      val json = Json.parse("""{"num_children": "42"}""")
      json.extractBigInt("num_children") must throwAn[UnexpectedJsonElementException]
    }

  }

  "extractBigIntOpt" should {

    "extract value in case it matches long in JSON" in {
      val json = Json.parse("""{"num_children": 2134123412341234}""")
      json.extractBigIntOpt("num_children") must_== Some(BigInt(2134123412341234L))
    }

    "extract and convert decimal value to integer if it can be done losslessly" in {
      val json = Json.parse("""{"num_children": 42.0}""")
      json.extractBigIntOpt("num_children") must_== Some(BigInt(42))
    }

    "extract value even if number exceeds long max value limit" in {
      val json = Json.parse("""{"num_children": 21341234123412341234}""")
      json.extractBigIntOpt("num_children") must_== Some(BigInt("21341234123412341234"))
    }

    "return None if integer part of decimal number exceeds long max value limit" in {
      val json = Json.parse("""{"num_children": 21341234123412341234.0}""")
      json.extractBigIntOpt("num_children") must_== None
    }

    "return None if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractBigIntOpt("num_children") must_== None
    }

    "return None if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractBigIntOpt("root.num_children") must_== None
    }

    "return None if type of JSON element doesn't match number" in {
      val json = Json.parse("""{"num_children": "42"}""")
      json.extractBigIntOpt("num_children") must_== None
    }

  }

  "extractLong" should {

    "extract value in case it matches long in JSON" in {
      val json = Json.parse("""{"num_children": 22222222222}""")
      json.extractLong("num_children") must_== 22222222222L
    }

    "extract and convert decimal value to long if it can be done losslessly" in {
      val json = Json.parse("""{"num_children": 22222222222.0}""")
      json.extractLong("num_children") must_== 22222222222L
    }

    "throw an exception if number exceeds long's max value limit" in {
      val json = Json.parse("""{"num_children": 21341234123412341234}""")
      json.extractLong("num_children") must throwAn[UnexpectedJsonElementException]
    }

    "throw an exception if integer part of decimal number exceeds long's max value limit" in {
      val json = Json.parse("""{"num_children": 21341234123412341234.0}""")
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
      val json = Json.parse("""{"num_children": "22222222222"}""")
      json.extractLong("num_children") must throwAn[UnexpectedJsonElementException]
    }

  }

  "extractLongOpt" should {

    "extract value in case it matches long in JSON" in {
      val json = Json.parse("""{"num_children": 22222222222}""")
      json.extractLongOpt("num_children") must_== Some(22222222222L)
    }

    "extract and convert decimal value to long if it can be done losslessly" in {
      val json = Json.parse("""{"num_children": 22222222222.0}""")
      json.extractLongOpt("num_children") must_== Some(22222222222L)
    }

    "return None if number exceeds long's max value limit" in {
      val json = Json.parse("""{"num_children": 21341234123412341234}""")
      json.extractLongOpt("num_children") must_== None
    }

    "return None if integer part of decimal number exceeds long's max value limit" in {
      val json = Json.parse("""{"num_children": 21341234123412341234.0}""")
      json.extractLongOpt("num_children") must_== None
    }

    "return None if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractLongOpt("num_children") must_== None
    }

    "return None if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractLongOpt("root.num_children") must_== None
    }

    "return None if type of JSON element doesn't match number" in {
      val json = Json.parse("""{"num_children": "22222222222"}""")
      json.extractLongOpt("num_children") must_== None
    }

  }

  "extractDouble" should {

    "extract value in case it matches double in JSON" in {
      val json = Json.parse("""{"num_children": 42.0}""")
      json.extractDouble("num_children") must_== 42.0
    }

    "extract and convert integer value to double" in {
      val json = Json.parse("""{"num_children": 22222222222}""")
      json.extractDouble("num_children") must_== 22222222222.0
    }

    "throw an exception if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractDouble("num_children") must throwAn[JsonElementIsNullException]
    }

    "throw an exception if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractDouble("root.num_children") must throwAn[JsonPathDoesntExistException]
    }

    "throw an exception if type of JSON element doesn't match number" in {
      val json = Json.parse("""{"num_children": "22222222222.0"}""")
      json.extractDouble("num_children") must throwAn[UnexpectedJsonElementException]
    }

  }

  "extractDoubleOpt" should {

    "extract value in case it matches double in JSON" in {
      val json = Json.parse("""{"num_children": 42.0}""")
      json.extractDoubleOpt("num_children") must_== Some(42.0)
    }

    "extract and convert integer value to double" in {
      val json = Json.parse("""{"num_children": 22222222222}""")
      json.extractDoubleOpt("num_children") must_== Some(22222222222.0)
    }

    "return None if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractDoubleOpt("num_children") must_== None
    }

    "return None if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractDoubleOpt("root.num_children") must_== None
    }

    "return None if type of JSON element doesn't match number" in {
      val json = Json.parse("""{"num_children": "22222222222.0"}""")
      json.extractDoubleOpt("num_children") must_== None
    }

  }

  "extractBigDecimal" should {

    "extract value in case it matches number in JSON" in {
      val json = Json.parse("""{"num_children": 21341234123412341234}""")
      json.extractBigDecimal("num_children") must_== BigDecimal("21341234123412341234")
    }

    "throw an exception if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractBigDecimal("num_children") must throwAn[JsonElementIsNullException]
    }

    "throw an exception if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractBigDecimal("root.num_children") must throwAn[JsonPathDoesntExistException]
    }

    "throw an exception if type of JSON element doesn't match number" in {
      val json = Json.parse("""{"num_children": "22222222222.0"}""")
      json.extractBigDecimal("num_children") must throwAn[UnexpectedJsonElementException]
    }

  }

  "extractBigDecimalOpt" should {

    "extract value in case it matches number in JSON" in {
      val json = Json.parse("""{"num_children": 21341234123412341234}""")
      json.extractBigDecimalOpt("num_children") must_== Some(BigDecimal("21341234123412341234"))
    }

    "return None if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractBigDecimalOpt("num_children") must_== None
    }

    "return None if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractBigDecimalOpt("root.num_children") must_== None
    }

    "return None if type of JSON element doesn't match number" in {
      val json = Json.parse("""{"num_children": "22222222222.0"}""")
      json.extractBigDecimalOpt("num_children") must_== None
    }

  }

  "extractString" should {

    "extract value in case it is string in JSON" in {
      val json = Json.parse("""{"check": "hello"}""")
      json.extractString("check") must_== "hello"
    }

    "extract and convert boolean value to string" in {
      val json = Json.parse("""{"check": true}""")
      json.extractString("check") must_== "true"
    }

    "extract and convert integer value to string" in {
      val json = Json.parse("""{"check": 10}""")
      json.extractString("check") must_== "10"
    }

    "extract and convert double value to string" in {
      val json = Json.parse("""{"check": 10.0}""")
      json.extractString("check") must_== "10.0"
    }

    "throw an exception if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractString("num_children") must throwAn[JsonElementIsNullException]
    }

    "throw an exception if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractString("root.num_children") must throwAn[JsonPathDoesntExistException]
    }

    "throw an exception if type of JSON element is an object" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractString("root") must throwAn[UnexpectedJsonElementException]
    }

    "throw an exception if type of JSON element is an array" in {
      val json = Json.parse("""{"root": [{"branch": "foo"}]}""")
      json.extractString("root") must throwAn[UnexpectedJsonElementException]
    }
  }

  "extractStringOpt" should {

    "extract value in case it is string in JSON" in {
      val json = Json.parse("""{"check": "hello"}""")
      json.extractStringOpt("check") must_== Some("hello")
    }

    "extract and convert boolean value to string" in {
      val json = Json.parse("""{"check": true}""")
      json.extractStringOpt("check") must_== Some("true")
    }

    "extract and convert integer value to string" in {
      val json = Json.parse("""{"check": 10}""")
      json.extractStringOpt("check") must_== Some("10")
    }

    "extract and convert double value to string" in {
      val json = Json.parse("""{"check": 10.0}""")
      json.extractStringOpt("check") must_== Some("10.0")
    }

    "return None if element is null" in {
      val json = Json.parse("""{"num_children": null}""")
      json.extractStringOpt("num_children") must_== None
    }

    "return None if path doesn't exist" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractStringOpt("root.num_children") must_== None
    }

    "return None if type of JSON element is an object" in {
      val json = Json.parse("""{"root": {"branch": "foo"}}""")
      json.extractStringOpt("root") must_== None
    }

    "return None if type of JSON element is an array" in {
      val json = Json.parse("""{"root": [{"branch": "foo"}]}""")
      json.extractStringOpt("root") must_== None
    }

  }

}
