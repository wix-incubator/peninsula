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

}
