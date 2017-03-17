package com.wix.peninsula.exceptions

class JsonElementIsNullException(message: String) extends RuntimeException(message)

object JsonElementIsNullException {

  def apply(): JsonElementIsNullException = {
    new JsonElementIsNullException(s"JSON element is null")
  }

  def apply(path: String): JsonElementIsNullException = {
    new JsonElementIsNullException(s"JSON element represented by '$path' is null")
  }

}