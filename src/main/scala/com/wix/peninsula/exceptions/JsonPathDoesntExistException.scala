package com.wix.peninsula.exceptions

class JsonPathDoesntExistException(message: String) extends RuntimeException(message)

object JsonPathDoesntExistException {

  def apply(): JsonPathDoesntExistException = {
    new JsonPathDoesntExistException(s"JSON element doesn't exist")
  }

  def apply(path: String): JsonPathDoesntExistException = {
    new JsonPathDoesntExistException(s"JSON element represented by path '$path' doesn't exist")
  }

}
