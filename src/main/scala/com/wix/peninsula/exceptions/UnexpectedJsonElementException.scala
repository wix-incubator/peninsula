package com.wix.peninsula.exceptions

import com.wix.peninsula.Json

case class UnexpectedJsonElementException(expectedType: String, actual: Json)
  extends RuntimeException(s"Expected json type: $expectedType, found json type: ${actual.getTypeString}, actual json was: $actual")
