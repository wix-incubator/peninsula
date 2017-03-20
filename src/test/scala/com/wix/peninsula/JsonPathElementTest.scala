package com.wix.peninsula

import org.specs2.mutable.SpecificationWithJUnit

class JsonPathElementTest extends SpecificationWithJUnit {

  "Parse simple element" in {
    JsonPathElement.parse("hello") mustEqual JsonPathElement(Some("hello"), None)
  }

  "Parse element with index" in {
    JsonPathElement.parse("hello[32]") mustEqual JsonPathElement(Some("hello"), Some(32))
  }

  "Parse index only element" in {
    JsonPathElement.parse("[32]") mustEqual JsonPathElement(None, Some(32))
  }

}