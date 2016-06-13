package com.wix.peninsula.exceptions

import com.wix.peninsula.Json

case class UnexpectedJsonElementException(expectedType: String, actual: Json)
  extends RuntimeException(s"Expected json type: $expectedType, but the json was: $actual")
