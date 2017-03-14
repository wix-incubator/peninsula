package com.wix.peninsula

import com.wix.peninsula.exceptions.UnexpectedJsonElementException

import language.reflectiveCalls

/**
  * @author Ivan V Kamenev <ivanka@wix.com>
  */
trait ExtractionHelper {

  type IntConvertible = {
    def isValidInt: Boolean
    def toInt: Int
  }

  def extractIntOrThrow(v: IntConvertible, json: Json): Option[Int] = {
    extractIntValue(v) match {
      case v: Some[Int] => v
      case None => throw UnexpectedJsonElementException("integer", json)
    }
  }

  def extractIntValue(v: IntConvertible): Option[Int] = {
    if (v.isValidInt) {
      Some(v.toInt)
    } else {
      None
    }
  }

}
