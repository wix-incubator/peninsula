package com.wix.peninsula.exceptions

case class JsonValidationException(message: String)
  extends RuntimeException(message)
