package com.wix.peninsula

import com.wix.peninsula.exceptions.JsonValidationException
import JsonValidators.StringValidator
import org.json4s.JsonAST._
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class JsonValidatorTest extends SpecificationWithJUnit {

  trait Context extends Scope {
    def validate(value: JValue) = StringValidator.validate(Json(value), "fieldName")
    def notThrowAnException = not(throwAn[Throwable])
  }

  "StringOrEmptyValidator.validate" should {
    "pass for a string field with value present" in new Context {
      validate(JString("present")) must notThrowAnException
    }

    "pass when a field is not present" in new Context {
      validate(JNothing) must notThrowAnException
    }

    "pass for an empty string field" in new Context {
      validate(JNothing) must notThrowAnException
    }

    "pass for a field with null value" in new Context {
      validate(JNull) must notThrowAnException
    }

    "fail for non-string field" in new Context {
      validate(JInt(1)) must throwA[JsonValidationException]
    }
  }

}
