package com.wix.peninsula

import org.specs2.mutable.SpecificationWithJUnit

class JsonPathElementTest extends SpecificationWithJUnit {

  "Parse simple element" in {
    JsonPathElement.parse("hello") mustEqual JsonPathElement("hello", None)
  }

  "Parse item with element" in {
    JsonPathElement.parse("hello(32)") mustEqual JsonPathElement("hello", Some(32))
  }

}