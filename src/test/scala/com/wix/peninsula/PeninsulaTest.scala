package com.wix.peninsula

import com.wix.peninsula.exceptions.JsonValidationException
import CopyConfigFactory._
import JsonValidators._
import org.json4s.JsonAST.JBool
import org.specs2.mutable.SpecificationWithJUnit


class PeninsulaTest extends SpecificationWithJUnit {

  "AppTransformer" should {

    "copy multiple fields" in {
      val json = Json.parse("""{"app": {"name": "hello peninsula"}, "surname": "goodbye"}""")

      val config = TransformationConfig()
        .add(copyFields("app.name", "surname"))

      val transformedJson = json.transform(config)

      transformedJson must_== json
    }

    "copy over a simple json" in {
      val json = Json.parse("""{"name": "hello peninsula"}""")
      val Config = TransformationConfig(copyConfigs = Seq(
        copyField("name")
      ))

      val transformedJson = json.transform(Config)

      transformedJson must_== json
    }


    "copy over from a path to a different path" in {
      val json = Json.parse("""{"app": {"name": "hello peninsula"}}""")
      val Config = TransformationConfig(copyConfigs = Seq(
        copyField("app.name" -> "application.username")
      ))

      val transformedJson = json.transform(Config)

      transformedJson must_== Json.parse("""{"application": {"username": "hello peninsula"}}""")
    }

    "copy over array with a nested config" in {
      val json = Json.parse("""{"apps": [{"name": "hello peninsula", "id": 1}, {"name": "goodbye", "id": 2}] }""")

      val AppConfig = TransformationConfig(copyConfigs = Seq(
        copyField("name", "username"),
        copyField("id", "appId")
      ))
      val Config = TransformationConfig(copyConfigs = Seq(
        copyArrayOfObjects("apps", AppConfig)
      ))

      val transformedJson = json.transform(Config)

      transformedJson must_== Json.parse(
        """{"apps": [{"username": "hello peninsula", "appId": 1}, {"username": "goodbye", "appId": 2}] }""")
    }

    "transform a top level array" in {
      val json = Json.parse("""[{"name": "hello peninsula", "id": 1}, {"name": "goodbye", "id": 2}]""")

      val AppConfig = TransformationConfig(copyConfigs = Seq(
        copyField("name", "username"),
        copyField("id", "appId")
      ))

      val transformedJson = json.transformArray(AppConfig)

      transformedJson must_== Json.parse(
        """[{"username": "hello peninsula", "appId": 1}, {"username": "goodbye", "appId": 2}]""")
    }

    "not generate empty array when nothing in source json" in {
      val json = Json.parse("""{"hello": 1}""")
      val AppConfig = TransformationConfig(copyConfigs = Seq(
        copyField("name", "username")
      ))
      val Config = TransformationConfig(copyConfigs = Seq(
        copyField("hello"),
        copyArrayOfObjects("apps", AppConfig)
      ))

      val transformedJson = json.transform(Config)

      transformedJson must_== Json.parse(
        """{"hello": 1}""")
    }

    "copy over and map simple values" in {
      val json = Json.parse("""{"name": "hello peninsula", "isTpa": "1"}""")
      object BooleanMapper extends JsonMapper {
        override def map(json: Json): Json = Json(JBool(value = true))
      }
      val Config = TransformationConfig(copyConfigs = Seq(
        copyField("name", "name"),
        copyField("isTpa", "isTpa").withMapper(BooleanMapper)
      ))

      val transformedJson = json.transform(Config)

      transformedJson must_== Json.parse("""{"name": "hello peninsula", "isTpa": true}""")
    }

    "throw exception when json invalid and with validator applied on multiple fields" in {
      val json = Json.parse("""{"name": "hello peninsula", "isTpa": 1}""")
      val Config = TransformationConfig().add(
        copyFields("name", "isTpa").withValidators(StringValidator)
      )

      json.transform(Config) must throwA[JsonValidationException]
    }

    "throw exception when json invalid and with validator applied on multiple fields with a FromTo copy" in {
      val json = Json.parse("""{"name": "hello peninsula", "isTpa": 1}""")
      val Config = TransformationConfig().add(
        copyFieldsFromTo("name" -> "hello", "isTpa" -> "tpa").withValidators(StringValidator)
      )

      json.transform(Config) must throwA[JsonValidationException]
    }

    "merge fields onto the top level" in {
      val json = Json.parse("""{"app": "comments", "fields": {"weight": 5, "description": "great comments"}}""")
      val Config = TransformationConfig(copyConfigs = Seq(
        copyField("app" -> "name"),
        mergeObject("fields")
      ))

      val transformedJson = json.transform(Config)

      transformedJson must_== Json.parse("""{"name": "comments", "weight": 5, "description": "great comments"}""")
    }



  }

}

