package com.wix.peninsula

import com.wix.peninsula.exceptions.{JsonElementIsNullException, JsonPathDoesntExistException, UnexpectedJsonElementException}
import org.json4s.JsonAST._

trait Extraction extends ExtractionHelper {

  this: Json =>

  def extract[T: Manifest]: T = {
    node.extract[T]
  }

  def extractOpt[T: Manifest]: Option[T] = {
    node match {
      case JNull    => None
      case JNothing => None
      case other    => Some(other.extract[T])
    }
  }

  def extract[T: Manifest](path: String): T = {
    this(path).node match {
      case JNull    => throw JsonElementIsNullException(path)
      case JNothing => throw JsonPathDoesntExistException(path)
      case other    => other.extract[T]
    }
  }

  def extractOpt[T: Manifest](path: String): Option[T] = {
    this(path).node match {
      case JNull    => None
      case JNothing => None
      case other    => Some(other.extract[T])
    }
  }

  def extractBoolean(path: String): Boolean = {
    this(path).node match {
      case JBool(v) => v
      case JNull    => throw JsonElementIsNullException(path)
      case JNothing => throw JsonPathDoesntExistException(path)
      case other    => throw UnexpectedJsonElementException("boolean", Json(other))
    }
  }

  def extractBooleanOpt(path: String): Option[Boolean] = {
    this(path).node match {
      case JBool(v) => Some(v)
      case _        => None
    }
  }

  def extractInt(path: String): Int = {
    this(path).node match {
      case JInt(v)      => extractIntValueOrThrow(v, Json(JInt(v))).get
      case JDouble(v)   => extractIntValueOrThrow(BigDecimal(v), Json(JDouble(v))).get
      case JDecimal(v)  => extractIntValueOrThrow(v, Json(JDecimal(v))).get
      case JNull        => throw JsonElementIsNullException(path)
      case JNothing     => throw JsonPathDoesntExistException(path)
      case other        => throw UnexpectedJsonElementException("integer", Json(other))
    }
  }

  def extractIntOpt(path: String): Option[Int] = {
    this(path).node match {
      case JInt(v)      => extractIntValue(v)
      case JDouble(v)   => extractIntValue(BigDecimal(v))
      case JDecimal(v)  => extractIntValue(v)
      case _            => None
    }
  }

  def extractBigInt(path: String): BigInt = {
    this(path).node match {
      case JInt(v)      => v
      case JDouble(v)   => extractBigIntValueOrThrow(BigDecimal(v), Json(JDouble(v))).get
      case JDecimal(v)  => extractBigIntValueOrThrow(v, Json(JDecimal(v))).get
      case JNull        => throw JsonElementIsNullException(path)
      case JNothing     => throw JsonPathDoesntExistException(path)
      case other        => throw UnexpectedJsonElementException("big integer", Json(other))
    }
  }

  def extractBigIntOpt(path: String): Option[BigInt] = {
    this(path).node match {
      case JInt(v)      => Some(v)
      case JDouble(v)   => extractBigIntValue(BigDecimal(v))
      case JDecimal(v)  => extractBigIntValue(v)
      case _            => None
    }
  }

  def extractLong(path: String): Long = {
    this(path).node match {
      case JInt(v)      => extractLongValueOrThrow(v, Json(JInt(v))).get
      case JDouble(v)   => extractLongValueOrThrow(BigDecimal(v), Json(JDouble(v))).get
      case JDecimal(v)  => extractLongValueOrThrow(v, Json(JDecimal(v))).get
      case JNull        => throw JsonElementIsNullException(path)
      case JNothing     => throw JsonPathDoesntExistException(path)
      case other        => throw UnexpectedJsonElementException("long", Json(other))
    }
  }

  def extractLongOpt(path: String): Option[Long] = {
    this(path).node match {
      case JInt(v)      => extractLongValue(v)
      case JDouble(v)   => extractLongValue(BigDecimal(v))
      case JDecimal(v)  => extractLongValue(v)
      case _            => None
    }
  }

  def extractDouble(path: String): Double = {
    this(path).node match {
      case JInt(v)      => extractDoubleValueOrThrow(v, Json(JInt(v))).get
      case JDouble(v)   => v
      case JDecimal(v)  => extractDoubleValueOrThrow(v, Json(JDecimal(v))).get
      case JNull        => throw JsonElementIsNullException(path)
      case JNothing     => throw JsonPathDoesntExistException(path)
      case other        => throw UnexpectedJsonElementException("double", Json(other))
    }
  }

  def extractDoubleOpt(path: String): Option[Double] = {
    this(path).node match {
      case JInt(v)      => extractDoubleValue(v)
      case JDouble(v)   => Some(v)
      case JDecimal(v)  => extractDoubleValue(v)
      case _            => None
    }
  }

  def extractBigDecimal(path: String): BigDecimal = {
    this(path).node match {
      case JInt(v)      => BigDecimal(v)
      case JDouble(v)   => BigDecimal(v)
      case JDecimal(v)  => v
      case JNull        => throw JsonElementIsNullException(path)
      case JNothing     => throw JsonPathDoesntExistException(path)
      case other        => throw UnexpectedJsonElementException("big decimal", Json(other))
    }
  }

  def extractBigDecimalOpt(path: String): Option[BigDecimal] = {
    this(path).node match {
      case JInt(v)      => Some(BigDecimal(v))
      case JDouble(v)   => Some(BigDecimal(v))
      case JDecimal(v)  => Some(v)
      case _            => None
    }
  }

  def extractString(path: String): String = {
    this(path).node match {
      case JString(v)   => v
      case JDecimal(v)  => v.toString
      case JInt(v)      => v.toString
      case JDouble(v)   => String.valueOf(v)
      case JBool(v)     => String.valueOf(v)
      case JNull        => throw JsonElementIsNullException(path)
      case JNothing     => throw JsonPathDoesntExistException(path)
      case other        => throw UnexpectedJsonElementException("string", Json(other))
    }
  }

  def extractStringOpt(path: String): Option[String] = {
    this(path).node match {
      case JString(v)   => Some(v)
      case JDecimal(v)  => Some(v.toString)
      case JInt(v)      => Some(v.toString)
      case JDouble(v)   => Some(String.valueOf(v))
      case JBool(v)     => Some(String.valueOf(v))
      case _            => None
    }
  }

}
