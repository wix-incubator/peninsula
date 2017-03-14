package com.wix.peninsula.exceptions

/**
  * @author Ivan V Kamenev <ivanka@wix.com>
  */
case class JsonPathDoesntExistException(path: String)
  extends RuntimeException("JSON element represented by path '$path' doesn't exist")
