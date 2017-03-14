package com.wix.peninsula.exceptions

case class JsonElementIsNullException(path: String)
  extends RuntimeException(s"JSON element represented by '$path' is null")
