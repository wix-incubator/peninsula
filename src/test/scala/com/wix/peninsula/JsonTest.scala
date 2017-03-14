package com.wix.peninsula

import com.wix.peninsula.exceptions.{JsonElementIsNullException, JsonPathDoesntExistException, JsonValidationException, UnexpectedJsonElementException}
import CopyConfigFactory._
import JsonValidators._
import com.wix.peninsula.domain.Person
import org.json4s.JsonAST._
import org.json4s.jackson.JsonMethods._
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

import scala.collection.mutable.ListBuffer

class JsonTest extends SpecificationWithJUnit {

  "Json" should {

    "parse json from string" in {
      Json.parse("""{"name": "hello"}""") must_== Json(parse("""{"name": "hello"}"""))
    }

    "read nested json values" in {
      val json = Json.parse( """{"app": {"name": "hello peninsula"}}""")

      json("app.name").node must_== JString("hello peninsula")
    }

    "create a simple json object" in {
      val value = Json(JString("hello"))

      val json = Json.create("path", value)

      json must_== Json.parse("""{"path": "hello"}""")
    }

    "create a nested json object" in {
      val value = Json(JString("hello"))

      val json = Json.create("parent.child", value)

      json must_== Json.parse("""{"parent": {"child": "hello"}}""")
    }

    "merge two jsons" in {
      val json1 = Json.parse("""{"id": 200, "person": {"name": "johnny"}}""")
      val json2 = Json.parse("""{"person": {"surname": "depp"}}""")

      json1.merge(json2) must_== Json.parse("""{ "id": 200, "person": {"name": "johnny", "surname": "depp"}}""")
    }

    "transform json" in {
      val json = Json.parse("""{"name": "hello", "lastname": "goodbye"}""")
      val copyConfig1 = CopyFieldConfig("name", "username")
      val copyConfig2 = CopyFieldConfig("lastname", "surname")
      val config = TransformationConfig(copyConfigs = Seq(copyConfig1, copyConfig2))

      val jsonResult = json.transform(config)

      jsonResult must_== Json.parse("""{"username": "hello", "surname": "goodbye"}""")
    }

    "Translation with transformation config" should {
      "translate simple json" in {
        val json = Json.parse("""{"id": 1, "person": {"name": "hello", "lastName": "goodbye"} }""")
        val translation = Json.parse("""{"name": "hello translated", "lastName": "goodbye translated"}""")

        val config = TransformationConfig(copyConfigs = Seq(
          copyField("name", "person.name"),
          copyField("lastName", "person.lastName")
        ))

        json.translate(translation, config) must_== Json.parse("""{"id": 1, "person": {"name": "hello translated", "lastName": "goodbye translated"} }""")
      }

      "not translate field if translation not available for it" in {
        val json = Json.parse("""{"id": 1, "person": {"name": "hello", "lastName": "goodbye"} }""")
        val translation = Json.parse("""{"name": "hello translated"}""")

        val config = TransformationConfig(copyConfigs = Seq(
          copyField("name", "person.name"),
          copyField("lastName", "person.lastName")
        ))

        json.translate(translation, config) must_== Json.parse("""{"id": 1, "person": {"name": "hello translated", "lastName": "goodbye"} }""")
      }

      "translate nested json" in {
        val json1 = Json.parse("""{"app": {"id": 1, "dev": "facebook"}, "name": "hello", "lastName": "goodbye"}""")
        val json2 = Json.parse("""{"app": {"dev": "google"}, "name": "hello translated", "lastName": "goodbye translated"}""")

        val config = TransformationConfig(copyConfigs = Seq(
          copyField("app.dev"),
          copyField("name"),
          copyField("lastName")
        ))

        json1.translate(json2, config) must_== Json.parse(
          """
           {"app": {"id": 1, "dev": "google"},
            "name": "hello translated",
            "lastName": "goodbye translated"}
          """)
      }
    }

    "Translation without transformation config" should {
      "translate simple key-value json" in {
        Json.parse("""{"name": "John"}""")
          .translate(Json.parse("""{"name": "James"}""")) must_== Json.parse(
          """{"name": "James"}"""
        )
      }

      "merge in new fields" in {
        Json.parse("""{"name": "John"}""")
          .translate(Json.parse("""{"surname": "Doe"}""")) must_== Json.parse(
          """{"name": "John", "surname": "Doe"}"""
        )
      }

      "replace values in arrays" in {
        Json.parse("""{"numbers": [1, 2]}""")
          .translate(Json.parse("""{"numbers": [3, 4]}""")) must_== Json.parse(
          """{"numbers": [3, 4]}"""
        )
      }
    }

    "extract objects by id map correctly" in {
      val json = Json.parse("""[{"id": 1, "dev": "f"}, {"id": 2, "dev": "g"}]""")


      json.objectsById( "id") must_==
        Map(JInt(1) -> Json.parse("""{"id": 1, "dev": "f"}"""), JInt(2) -> Json.parse("""{"id": 2, "dev": "g"}"""))
    }

    "translate arrays by id json" in {
      val json = Json.parse(
        """{"apps": [{"id": 1, "dev": "facebook", "name": "facebook like"}, {"id": 2, "dev": "google"}], "collection": "hh"}""")
      val translation = Json.parse(
        """{"collection": "a proper haha", "apps": [{"id": 2, "dev": "gūglas"}, {"id": 1, "dev": "veidaknygė"}]}""")

      val appConfig = TransformationConfig(copyConfigs = Seq(
        copyField("id"),
        copyField("dev")
      ))

      val config = TransformationConfig(copyConfigs = Seq(
        copyArrayOfObjects("apps", appConfig, idField = "id"),
        copyField("collection")
      ))

      json.translate(translation, config) must_== Json.parse(
        """{"collection": "a proper haha",
           "apps": [{"id": 1, "name": "facebook like", "dev": "veidaknygė"}, {"id": 2, "dev": "gūglas"}]
          }"""
      )
    }

    trait JsonArraysContext extends Scope {
      val jsonObject = Json.parse( """{"apps": "two"}""")
      val jsonArray = Json.parse( """[{"name": "one"}, {"name": "two"}]""")
      val jsonArrayOfPrimitives = Json.parse( """["app"]""")
      val emptyConfig = TransformationConfig()
    }

    "when json is not an array translate array should throw exception" in new JsonArraysContext {
      jsonObject.translateArray(jsonArray, emptyConfig, "id") must throwAn[UnexpectedJsonElementException]
    }

    "when translation is not an array translate array should throw exception" in new JsonArraysContext {
      jsonArray.translateArray(jsonObject, emptyConfig, "id") must throwAn[UnexpectedJsonElementException]
    }

    "when translation is not an array of objects translate array should throw exception" in new JsonArraysContext {
      jsonArray.translateArray(jsonArrayOfPrimitives, emptyConfig, "id") must throwAn[UnexpectedJsonElementException]
    }

    "when json is an array of primitives translate array should throw exception" in new JsonArraysContext {
      jsonArrayOfPrimitives.translateArray(jsonArray, emptyConfig, "id") must throwAn[UnexpectedJsonElementException]
    }

    "throw validation exception when a required field is missing on transform" in {
      val json = Json.parse( """{"name": "hello"}""")

      val config = TransformationConfig(copyConfigs = Seq(
        copyField("id").withValidators(NonEmptyStringValidator)
      ))

      json.transform(config) must throwA[JsonValidationException]
    }

    "throw validation exception when a copyField field is actually an object" in {
      val json = Json.parse( """{"id": {"hello": 1}}""")

      val config = TransformationConfig(copyConfigs = Seq(
        copyField("id")
      ))

      json.transform(config) must throwA[JsonValidationException]
    }

    "throw validation exception when a copyField field is actually an array" in {
      val json = Json.parse( """{"id": [1, 2]}""")

      val config = TransformationConfig(copyConfigs = Seq(
        copyField("id")
      ))

      json.transform(config) must throwA[JsonValidationException]
    }

    "can set string value" in {
      Json.parse("""{"name": "jon"}""").set("surname", "snow") must_== Json.parse("""{"name": "jon", "surname": "snow"}""")
    }

    "iterate through array elements with foreach" in {
      val json = Json.parse("""[{"name": 1}, {"name": 2}]""")
      val buf = ListBuffer[Json]()
      json.foreachObject( (json) => buf += json  )
      buf.toList must_== List(Json(JObject(JField("name", JInt(1)))), Json(JObject(JField("name", JInt(2)))))
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

      "throw an exception if type of JSON element doesn't match boolean" in {
        val json = Json.parse("""{"is_checked": "false"}""")
        json.extractBooleanOpt("is_checked") must throwAn[UnexpectedJsonElementException]
      }

    }

    "extract string" in {
      val json = Json.parse("""{"check": "hello"}""")
      json.extractString("check") must_== "hello"
    }

    "extract string opt" in {
      val json = Json.parse("""{"check": "hello"}""")
      json.extractStringOpt("check") must_== Some("hello")
      json.extractStringOpt("hell") must_== None
    }

    "extract long" in {
      val json = Json.parse("""{"check": 2}""")
      json.extractLong("check") must_== 2
    }

    "extract long opt" in {
      val json = Json.parse("""{"check": 2}""")
      json.extractLongOpt("check") must_== Some(2)
      json.extractLongOpt("hell") must_== None
    }

    "copy array of primitives when translating" in {
      val json = Json(JObject())
      val translation = Json.parse("""{"a" : [1, 2]}""")

      val translated = json.translate(translation, TransformationConfig(copyConfigs =
        Seq(copyArrayOfPrimitives("a" -> "b"))
      ))

      translated must_== Json.parse("""{"b": [1, 2]}""")
    }

    "remove field correctly when two fields with the same name" in {
      val json = Json.parse("""{"name":"a", "user": {"name": "valdemaras"}}""")

      json.remove("name") must_== Json.parse("""{"user": {"name": "valdemaras"}}""")
    }


    "throw exception when non string validated as string" in {
      val json = Json.parse("""{"a": 1}""")

      val config = TransformationConfig(copyConfigs = Seq(
        copyField("a").withValidators(StringValidator)
      ))

      json.transform(config) must throwAn[JsonValidationException]
    }

    "do not throw exception when non existent field validated as string" in {
      val json = Json.parse("""{"a": 1}""")

      val config = TransformationConfig(copyConfigs = Seq(
        copyField("b").withValidators(StringValidator)
      ))

      json.transform(config) must_== Json(JObject())
    }

    "filter fields in an object" in {
      val json = Json.parse(
        """{"appDefinitionId":"a",
            "name":"b",
            "slug":"c",
            "slugHistory":["d"],
            "widgets": [{"name": "e"}, {"name": "f"}]}""")

      val filteredJson = json.only(Set("appDefinitionId", "slugHistory"))

      filteredJson must_== Json.parse("""{"appDefinitionId": "a", "slugHistory": ["d"]}""")
    }

    "filter fields in an array" in {
      val json = Json.parse("""
        [ {"appDefinitionId":"a", "name":"b", "slug":"c"},
          {"appDefinitionId":"e", "name":"f", "slug":"g"} ]
                            """)

      val filteredJson = json.only(Set("appDefinitionId"))

      filteredJson must_== Json.parse("""[{"appDefinitionId": "a"}, {"appDefinitionId": "e"}]""")
    }

    "filter fields in an array when element not an object" in {
      val json = Json.parse("""[ {"appDefinitionId":"a", "name":"b", "slug":"c"}, 43 ]""")

      val filteredJson = json.only(Set("appDefinitionId"))

      filteredJson must_== Json.parse("""[{"appDefinitionId": "a"}, 43]""")
    }

    "remove null obejcts from a simple json" in {
      val json = Json(JObject(JField("firstName", JString("Bill")), JField("lastName", JNull)))

      json.excludeNullValues must_== Json(JObject(JField("firstName", JString("Bill"))))
    }

    "remove null obejcts from an array" in {
      val json = Json.parse(
        """{"words": [ {"hello": "hi", "yes": null}, {"one": "two"}, null ]}""")

      json.excludeNullValues must_== Json.parse("""{"words": [ {"hello": "hi"}, {"one": "two"} ]}""")
    }

    "transform string values in a json object" in {
      val json = Json.parse("""{"a": "1", "b": "2", "c": null, "d": 1}""")

      json.transformStringValues(_ + "t") must_== Json.parse("""{"a": "1t", "b": "2t", "c": null, "d": 1}""")
    }

    "check if object is an object of integer values" in {
      val json = Json.parse("""{"a": 1}""")

      json.isObjectOfIntValues must beTrue
    }

    "isObjectOfIntValues should return false if object values are string" in {
      val json = Json.parse("""{"a": "string"}""")

      json.isObjectOfIntValues must beFalse
    }

    "Extract case class from an object" in {
      val json = Json.parse("""{ "id": 1, "name": "Hello"}""")

      json.extract[Person]() mustEqual Person(id = 1, name = "Hello")
    }

    "Extract a sequence of case classes from a json array" in {
      val json = Json.parse("""[{ "id": 1, "name": "Hello"}, {"id": 2, "name": "Goodbye"}] """)

      json.extract[Seq[Person]]() mustEqual Seq(Person(id = 1, name = "Hello"), Person(id = 2, name = "Goodbye"))
    }

    "Extract an element from an array" in {
      val json = Json.parse("""{"customers": [{ "id": 1, "name": "Hello"}, {"id": 2, "name": "Goodbye"}]} """)

      json("customers(1)") mustEqual Json.parse("""{ "id": 2, "name": "Goodbye"}""")
    }

  }

}
