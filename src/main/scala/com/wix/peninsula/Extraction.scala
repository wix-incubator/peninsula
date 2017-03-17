package com.wix.peninsula

import com.wix.peninsula.exceptions.{JsonElementIsNullException, JsonPathDoesntExistException, UnexpectedJsonElementException}
import org.json4s.JsonAST._

import scala.util.{Failure, Success, Try}

trait Extraction extends ExtractionHelper {

  this: Json =>

  def extract[T: Manifest]: T = {
    node match {
      case JNull    => throw JsonElementIsNullException()
      case JNothing => throw JsonPathDoesntExistException()
      case other    => other.extract[T]
    }
  }

  def extractTry[T: Manifest]: Try[T] = {
    node match {
      case JNull    => Failure(JsonElementIsNullException())
      case JNothing => Failure(JsonPathDoesntExistException())
      case other    => Success(other.extract[T])
    }
  }

  def extract[T: Manifest](path: String): T = {
    this(path).node match {
      case JNull    => throw JsonElementIsNullException(path)
      case JNothing => throw JsonPathDoesntExistException(path)
      case other    => other.extract[T]
    }
  }

  def extractTry[T: Manifest](path: String): Try[T] = {
    this(path).node match {
      case JNull    => Failure(JsonElementIsNullException(path))
      case JNothing => Failure(JsonPathDoesntExistException(path))
      case other    => Success(other.extract[T])
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

  def extractBooleanTry(path: String): Try[Boolean] = {
    this(path).node match {
      case JBool(v) => Success(v)
      case JNull    => Failure(JsonElementIsNullException(path))
      case JNothing => Failure(JsonPathDoesntExistException(path))
      case other    => Failure(UnexpectedJsonElementException("boolean", Json(other)))
    }
  }

  def extractInt(path: String): Int = {
    this(path).node match {
      case JInt(v)      => tryExtractIntValue(v, Json(JInt(v))).get
      case JNull        => throw JsonElementIsNullException(path)
      case JNothing     => throw JsonPathDoesntExistException(path)
      case other        => throw UnexpectedJsonElementException("integer", Json(other))
    }
  }

  def extractIntTry(path: String): Try[Int] = {
    this(path).node match {
      case JInt(v)      => tryExtractIntValue(v, Json(JInt(v)))
      case JNull        => Failure(JsonElementIsNullException(path))
      case JNothing     => Failure(JsonPathDoesntExistException(path))
      case other        => Failure(UnexpectedJsonElementException("integer", Json(other)))
    }
  }

  def extractBigInt(path: String): BigInt = {
    this(path).node match {
      case JInt(v)      => v
      case JNull        => throw JsonElementIsNullException(path)
      case JNothing     => throw JsonPathDoesntExistException(path)
      case other        => throw UnexpectedJsonElementException("big integer", Json(other))
    }
  }

  def extractBigIntTry(path: String): Try[BigInt] = {
    this(path).node match {
      case JInt(v)      => Success(v)
      case JNull        => Failure(JsonElementIsNullException(path))
      case JNothing     => Failure(JsonPathDoesntExistException(path))
      case other        => Failure(UnexpectedJsonElementException("big integer", Json(other)))
    }
  }

  def extractLong(path: String): Long = {
    this(path).node match {
      case JInt(v)      => tryExtractLongValue(v, Json(JInt(v))).get
      case JNull        => throw JsonElementIsNullException(path)
      case JNothing     => throw JsonPathDoesntExistException(path)
      case other        => throw UnexpectedJsonElementException("long", Json(other))
    }
  }

  def extractLongTry(path: String): Try[Long] = {
    this(path).node match {
      case JInt(v)      => tryExtractLongValue(v, Json(JInt(v)))
      case JNull        => Failure(JsonElementIsNullException(path))
      case JNothing     => Failure(JsonPathDoesntExistException(path))
      case other        => Failure(UnexpectedJsonElementException("long", Json(other)))
    }
  }

  def extractDouble(path: String): Double = {
    this(path).node match {
      case JDouble(v)   => v
      case JNull        => throw JsonElementIsNullException(path)
      case JNothing     => throw JsonPathDoesntExistException(path)
      case other        => throw UnexpectedJsonElementException("double", Json(other))
    }
  }

  def extractDoubleTry(path: String): Try[Double] = {
    this(path).node match {
      case JDouble(v)   => Success(v)
      case JNull        => Failure(JsonElementIsNullException(path))
      case JNothing     => Failure(JsonPathDoesntExistException(path))
      case other        => Failure(UnexpectedJsonElementException("double", Json(other)))
    }
  }

  def extractBigDecimal(path: String): BigDecimal = {
    this(path).node match {
      case JDecimal(v)  => v
      case JNull        => throw JsonElementIsNullException(path)
      case JNothing     => throw JsonPathDoesntExistException(path)
      case other        => throw UnexpectedJsonElementException("big decimal", Json(other))
    }
  }

  def extractBigDecimalTry(path: String): Try[BigDecimal] = {
    this(path).node match {
      case JDecimal(v)  => Success(v)
      case JNull        => Failure(JsonElementIsNullException(path))
      case JNothing     => Failure(JsonPathDoesntExistException(path))
      case other        => Failure(UnexpectedJsonElementException("big decimal", Json(other)))
    }
  }

  def extractString(path: String): String = {
    this(path).node match {
      case JString(v)   => v
      case JNull        => throw JsonElementIsNullException(path)
      case JNothing     => throw JsonPathDoesntExistException(path)
      case other        => throw UnexpectedJsonElementException("string", Json(other))
    }
  }

  def extractStringTry(path: String): Try[String] = {
    this(path).extractStringTry
  }

  protected def extractStringTry: Try[String] = {
    this.node match {
      case JString(v)   => Success(v)
      case JNull        => Failure(JsonElementIsNullException())
      case JNothing     => Failure(JsonPathDoesntExistException())
      case other        => Failure(UnexpectedJsonElementException("string", Json(other)))
    }
  }

}
