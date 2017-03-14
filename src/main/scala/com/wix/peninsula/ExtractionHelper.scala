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

  type LongConvertible = {
    def isValidLong: Boolean
    def toLong: Long
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

  def extractBigIntOrThrow(v: BigDecimal, json: Json): Option[BigInt] = {
    if (v.isValidLong) {
       v.toBigIntExact() match {
        case v: Some[BigInt] => v
        case None => throw UnexpectedJsonElementException("big integer", json)
      }
    } else {
      throw UnexpectedJsonElementException("big integer", json)
    }
  }

  def extractLongOrThrow(v: LongConvertible, json: Json): Option[Long] = {
    extractLongValue(v) match {
      case v: Some[Long] => v
      case None => throw UnexpectedJsonElementException("long", json)
    }
  }

  def extractLongValue(v: LongConvertible): Option[Long] = {
    if (v.isValidLong) {
      Some(v.toLong)
    } else {
      None
    }
  }

}
