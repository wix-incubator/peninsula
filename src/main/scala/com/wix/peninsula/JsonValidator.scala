package com.wix.peninsula

import com.wix.peninsula.exceptions.JsonValidationException
import org.json4s.JsonAST._

trait JsonValidator {
  def validate(json: Json, fieldName: String): Unit
}

object JsonValidators {

  object StringValidator extends JsonValidator {
    override def validate(json: Json, fieldName: String): Unit = {
      json.node match {
        case _: JString | JNothing | JNull =>
        case other => throw JsonValidationException(s"Field: $fieldName, expected a string, but found: $other")
      }
    }
  }

  object NonEmptyStringValidator extends JsonValidator {
    override def validate(json: Json, fieldName: String): Unit = {
      json.node match {
        case JString(s) if s.length > 0 =>
        case other => throw JsonValidationException(s"Field: $fieldName, expected a non empty string, but found: $other")

      }
    }
  }

  object DefaultFieldValidator extends JsonValidator {
    override def validate(json: Json, fieldName: String): Unit = {
      json.node match {
        case a: JArray => throw JsonValidationException(s"Field: $fieldName, expected a simple field but found an array: $a")
        case a: JObject => throw JsonValidationException(s"Field: $fieldName, expected a simple field but found an object: $a")
        case _ =>
      }
    }
  }

}
