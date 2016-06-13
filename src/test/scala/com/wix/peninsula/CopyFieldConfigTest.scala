package com.wix.peninsula

import com.wix.peninsula.JsonValidators._
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

class CopyFieldConfigTest extends SpecWithJUnit {

  trait Context extends Scope {
    val copyConfig = CopyFieldConfig(from = "from", to = "to")
  }

  "CopyFieldConfig" should {
    "have a default field validator" in new Context {
      copyConfig.validators mustEqual Seq(DefaultFieldValidator)
    }

    "add append a validator using withValidators" in new Context {
      val actualValidators = copyConfig.withValidators(NonEmptyStringValidator).validators

      actualValidators mustEqual Seq(DefaultFieldValidator, NonEmptyStringValidator)
    }
  }
}
