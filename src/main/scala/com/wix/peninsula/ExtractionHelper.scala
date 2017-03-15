package com.wix.peninsula

import com.wix.peninsula.exceptions.UnexpectedJsonElementException

import language.reflectiveCalls

trait ExtractionHelper {

  type IntConvertible = {
    def isValidInt: Boolean
    def toInt: Int
  }

  type LongConvertible = {
    def isValidLong: Boolean
    def toLong: Long
  }

  type DoubleConvertible = {
    def isValidDouble: Boolean
    def toDouble: Double
  }

  def extractIntValueOrThrow(v: IntConvertible, json: Json): Option[Int] = {
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

  def extractBigIntValueOrThrow(v: BigDecimal, json: Json): Option[BigInt] = {
    extractBigIntValue(v) match {
      case v: Some[BigInt] => v
      case None => throw UnexpectedJsonElementException("big integer", json)
    }
  }

  def extractBigIntValue(v: BigDecimal): Option[BigInt] = {
    if (v.isValidLong) {
      v.toBigIntExact()
    } else {
      None
    }
  }

  def extractLongValueOrThrow(v: LongConvertible, json: Json): Option[Long] = {
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

  def extractDoubleValueOrThrow(v: DoubleConvertible, json: Json): Option[Double] = {
    extractDoubleValue(v) match {
      case v: Some[Double] => v
      case None => throw UnexpectedJsonElementException("double", json)
    }
  }

  def extractDoubleValue(v: DoubleConvertible): Option[Double] = {
    if (v.isValidDouble) {
      Some(v.toDouble)
    } else {
      None
    }
  }

}
