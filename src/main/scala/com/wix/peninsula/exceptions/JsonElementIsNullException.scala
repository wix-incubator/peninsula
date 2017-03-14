package com.wix.peninsula.exceptions

/**
  * @author Ivan V Kamenev <ivanka@wix.com>
  */
case class JsonElementIsNullException(path: String)
  extends RuntimeException(s"JSON element represented by '$path' is null")
