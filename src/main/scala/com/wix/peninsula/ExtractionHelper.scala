package com.wix.peninsula

import com.wix.peninsula.exceptions.UnexpectedJsonElementException

import language.reflectiveCalls
import scala.util.{Failure, Success, Try}

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

  def tryExtractIntValue(v: String, json: Json): Try[Int] = {
    try {
      if (v.toInt.toString == v) {
        Success(v.toInt)
      } else {
        Failure(UnexpectedJsonElementException("integer", json))
      }
    } catch {
      case _: NumberFormatException =>
        Failure(UnexpectedJsonElementException("integer", json))
    }
  }

  def tryExtractIntValue(v: IntConvertible, json: Json): Try[Int] = {
    extractIntValue(v) match {
      case Some(intVal) => Success(intVal)
      case None => Failure(UnexpectedJsonElementException("integer", json))
    }
  }

  def extractIntValue(v: IntConvertible): Option[Int] = {
    if (v.isValidInt) {
      Some(v.toInt)
    } else {
      None
    }
  }

  def tryExtractBigIntValue(v: String, json: Json): Try[BigInt] = {
    try {
      if (BigInt(v).toString == v) {
        Success(BigInt(v))
      } else {
        Failure(UnexpectedJsonElementException("big integer", json))
      }
    } catch {
      case _: NumberFormatException =>
        Failure(UnexpectedJsonElementException("big integer", json))
    }
  }

  def tryExtractBigIntValue(v: BigDecimal, json: Json): Try[BigInt] = {
    extractBigIntValue(v) match {
      case Some(bigIntValue) => Success(bigIntValue)
      case None => Failure(UnexpectedJsonElementException("big integer", json))
    }
  }

  def extractBigIntValue(v: BigDecimal): Option[BigInt] = {
    if (v.isValidLong) {
      v.toBigIntExact()
    } else {
      None
    }
  }

  def tryExtractLongValue(v: String, json: Json): Try[Long] = {
    try {
      if (v.toLong.toString == v) {
        Success(v.toLong)
      } else {
        Failure(UnexpectedJsonElementException("long", json))
      }
    } catch {
      case _: NumberFormatException =>
        Failure(UnexpectedJsonElementException("long", json))
    }
  }

  def tryExtractLongValue(v: LongConvertible, json: Json): Try[Long] = {
    extractLongValue(v) match {
      case Some(longValue) => Success(longValue)
      case None => Failure(UnexpectedJsonElementException("long", json))
    }
  }

  def extractLongValue(v: LongConvertible): Option[Long] = {
    if (v.isValidLong) {
      Some(v.toLong)
    } else {
      None
    }
  }

  def tryExtractDoubleValue(v: String, json: Json): Try[Double] = {
    try {
      if (v.toDouble.toString == v) {
        Success(v.toDouble)
      } else {
        Failure(UnexpectedJsonElementException("double", json))
      }
    } catch {
      case _: NumberFormatException =>
        Failure(UnexpectedJsonElementException("double", json))
    }
  }

  def tryExtractDoubleValue(v: DoubleConvertible, json: Json): Try[Double] = {
    extractDoubleValue(v) match {
      case Some(doubleValue) => Success(doubleValue)
      case None => Failure(UnexpectedJsonElementException("double", json))
    }
  }

  def extractDoubleValue(v: DoubleConvertible): Option[Double] = {
    if (v.isValidDouble) {
      Some(v.toDouble)
    } else {
      None
    }
  }

  def tryExtractBigDecimalValue(v: String, json: Json): Try[BigDecimal] = {
    try {
      if (BigDecimal(v).toString == v) {
        Success(BigDecimal(v))
      } else {
        Failure(UnexpectedJsonElementException("big decimal", json))
      }
    } catch {
      case _: NumberFormatException =>
        Failure(UnexpectedJsonElementException("big decimal", json))
    }
  }

}
