package com.wix.peninsula.exceptions

case class JsonPathDoesntExistException(path: String)
  extends RuntimeException(s"JSON element represented by path '$path' doesn't exist")
