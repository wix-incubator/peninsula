package com.wix.peninsula

import org.json4s.JsonAST._

import scala.util.{Failure, Success}

/**
  * @author Ivan V Kamenev <ivanka@wix.com>
  */
trait Inspection {

  this: Json =>

  def exists: Boolean = this.node match {
    case JNothing => false
    case _        => true
  }

  def exists(path: String): Boolean = {
    this(path).exists
  }

  def contains(value: String): Boolean = {
    extractStringTry match {
      case Success(v) => v == value
      case Failure(_) => false
    }
  }

  def contains(path: String, value: String): Boolean = {
    this(path).contains(value)
  }

  def isNull: Boolean = this.node match {
    case JNull => true
    case _     => false
  }

  def isNull(path: String): Boolean = {
    this(path).isNull
  }

  def isString: Boolean = this.node match {
    case JString(_) => true
    case _     => false
  }

  def isString(path: String): Boolean = {
    this(path).isString
  }

  def isDouble: Boolean = this.node match {
    case JDouble(_) => true
    case _     => false
  }

  def isDouble(path: String): Boolean = {
    this(path).isDouble
  }

  def isBigDecimal: Boolean = this.node match {
    case JDecimal(_) => true
    case _     => false
  }

  def isBigDecimal(path: String): Boolean = {
    this(path).isBigDecimal
  }

  def isInt: Boolean = this.node match {
    case JInt(v) => if (v.isValidInt) true else false
    case _     => false
  }

  def isInt(path: String): Boolean = {
    this(path).isInt
  }

  def isBigInt: Boolean = this.node match {
    case JInt(_) => true
    case _     => false
  }

  def isBigInt(path: String): Boolean = {
    this(path).isBigInt
  }

  def isBoolean: Boolean = this.node match {
    case JBool(_) => true
    case _     => false
  }

  def isBoolean(path: String): Boolean = {
    this(path).isBoolean
  }

  def isObject: Boolean = this.node match {
    case JObject(_) => true
    case _     => false
  }

  def isObject(path: String): Boolean = {
    this(path).isObject
  }

  def isArray: Boolean = this.node match {
    case JArray(_) => true
    case _     => false
  }

  def isArray(path: String): Boolean = {
    this(path).isArray
  }

}
