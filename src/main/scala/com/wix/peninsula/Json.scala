package com.wix.peninsula

import com.wix.peninsula.exceptions.UnexpectedJsonElementException
import org.json4s.JsonAST.{JArray, JDecimal, JDouble, JField, JInt, JNothing, JNull, JObject, JString, JValue}
import org.json4s._
import org.json4s.jackson.JsonMethods

import scala.PartialFunction._

case class Json(node: JValue = JObject(), implicit val formats: DefaultFormats = DefaultFormats) extends Extraction {

  def exists: Boolean = this.node match {
    case JNothing => false
    case _        => true
  }

  def exists(path: String): Boolean = {
    this(path).exists
  }

  def isNull: Boolean = this.node match {
    case JNull => true
    case _     => false
  }

  def isNull(path: String): Boolean = {
    this(path).isNull
  }

  def contains(value: String): Boolean = {
    extractStringOpt.contains(value)
  }

  def contains(path: String, value: String): Boolean = {
    this(path).contains(value)
  }

  def only(fieldNames: Set[String]): Json = this.node match {
    case JObject(fields) => Json(JObject(fields.filter( f => fieldNames.contains(f._1))))
    case JArray(nodes)   => Json(JArray(nodes.map(Json(_).only(fieldNames).node)))
    case _ => this
  }

  def only(fieldNames: Option[Set[String]]): Json = fieldNames.map(this.only).getOrElse(this)

  def mapObjects[T](func: (Json) => T): List[T] = {
    val JArray(objects: List[JValue]) = this.node
    objects.map(o => func(Json(o)))
  }

  def transformStringValues(func: (String) => String): Json = {
    val JObject(properties) = this.node
    Json(JObject(properties.map {
      case (key, JString(value)) => (key, JString(func(value)))
      case (key, value) => (key, value)
    }))
  }

  def foreachObject(func: (Json) => Unit): Unit = {
    val JArray(objects: List[JValue]) = this.node
    objects.foreach { (o) => func(Json(o)) }
  }

  def translateArray(translationsArray: Json, config: TransformationConfig, idField: String): List[JValue] = {
    translateArray(translationsArray.objectsById(idField), config, idField)
  }

  def translateArray(translationsById: Map[JValue, Json], config: TransformationConfig, idField: String): List[JValue] = {
    this.node match {
      case JArray(objects) => translateObjects(objects, translationsById, config, idField)
      case _ => throw UnexpectedJsonElementException("array", this)
    }
  }

  def translateObjects(objects: List[JValue], translationsById: Map[JValue, Json], config: TransformationConfig, idField: String): List[JValue] = {
    objects.map {
      case o: JObject =>
        val translatable = Json(o)
        val id = translatable(idField).node
        val translation = translationsById.get(id)
        translateObject(translatable, translation, config)
      case other => throw UnexpectedJsonElementException("json object", Json(other))
    }
  }

  private def translateObject(translatable: Json, translation: Option[Json], config: TransformationConfig): JValue = {
    val translated = translation match {
      case Some(t) => translatable.translate(t, config)
      case _ => translatable
    }
    translated.node
  }

  def objectsById(idField: String): Map[JValue, Json] = {
    this.node match {
      case a: JArray => objectsById(idField, a)
      case _ => throw UnexpectedJsonElementException("array", this)
    }
  }

  private def objectsById(idField: String, arrayOfObjects: JArray): Map[JValue, Json] = {
    arrayOfObjects match {
      case JArray(objects) => objects.map {
        case o: JObject => Json(o)(idField).node -> Json(o)
        case other => throw UnexpectedJsonElementException("json object", Json(other))
      }.toMap
    }
  }

  def translate(translation: Json, config: TransformationConfig): Json = {
    config.copyConfigs.foldLeft(this) { (json, c) =>
      c match {
        case CopyFieldConfig(from, to, mapper, _) => json.setIfValuePresent(to, translation(from))
        case CopyArrayConfig(from, to, elementConfig, Some(idField)) =>
          val arrayToTranslate = this(to)
          val arrayTranslation = translation(from)
          if (arrayToTranslate.node != JNothing && arrayTranslation.node != JNothing) {
            val translatedArray = arrayToTranslate.translateArray(arrayTranslation, elementConfig, idField)
            json.remove(to).set(to, Json(JArray(translatedArray)))
          }
          else json
      }
    }
  }

  def translate(json: Json): Json = {
    def mergeFields(left: List[JField], right: List[JField]): List[JField] = left match {
      case Nil => right
      case (lKey, lVal) :: xs => right find (_._1 == lKey) match {
        case Some((rKey, rVal)) => JField(lKey, merge(lVal, rVal)) :: mergeFields(xs, right diff List((rKey, rVal)))
        case None => JField(lKey, lVal) :: mergeFields(xs, right)
      }
    }

    def merge(lVal: JValue, rVal: JValue): JValue = (lVal, rVal) match {
      case (JObject(lFields), JObject(rFields)) => JObject(mergeFields(lFields, rFields))
      case (left, JNothing) => left
      case (_, right) => right
    }

    Json(merge(this.node, json.node))
  }

  def setIfValuePresent(toPath: String, value: Json): Json = {
    if (value.node != JNothing) this.remove(toPath).set(toPath, value)
    else this
  }

  override def toString: String = JsonMethods.pretty(JsonMethods.render(node))

  def getTypeString: String = node match {
    case _: JObject => "object"
    case _: JArray => "array"
    case _: JString => "string"
    case _: JDouble => "double"
    case _: JDecimal => "big decimal"
    case _: JInt => "big integer"
    case _: JBool => "boolean"
    case _: JNull.type => "null"
    case _: JNothing.type => "nothing"
    case _ => "unknown"
  }

  def compactRender: String = JsonMethods.compact(JsonMethods.render(node))

  def prettyRender: String = JsonMethods.pretty(JsonMethods.render(node))

  def transformArray(config: TransformationConfig): Json = {
    Json(JArray(this.mapObjects(_.transform(config).node)))
  }

  def transform(config: TransformationConfig): Json = {
    config.copyConfigs.foldLeft(Json(JObject())) { (json, c) =>
      c match {
        case CopyFieldConfig(from, to, mapper, validators) =>
          val jsonFrom = this(from)
          validators.foreach(_.validate(jsonFrom, from))
          json.setIfValuePresent(to, mapper.map(jsonFrom))
        case CopyArrayConfig(from, to, elementConfig, _) =>
          val value = this(from)
          if (value.node != JNothing)
            json.set(to, AllObjectsInArray(elementConfig).map(value))
          else
            json
        case MergeObjectConfig(from) =>
          val value = this(from)
          if (value.node != JNothing)
            json.merge(value)
          else
            json
      }
    }
  }

  def set(path: String, value: Json): Json = {
    this.merge(Json.create(path, value))
  }

  def set(path: String, value: String): Json = set(path, Json(JString(value)))

  def merge(json: Json): Json = Json(this.node merge json.node)

  def apply(jsonPath: String): Json = new Json(readFromPath(node, jsonPath))

  def remove(path: String): Json = {
    val pathSeq = path.split("\\.")
    val fieldName = pathSeq.head
    if (pathSeq.length == 1) {
      this.node match {
        case JObject(fields) => Json(JObject(fields.filterNot(_._1 == fieldName)))
        case _ => this
      }
    }
    else {
      val fieldValue = this(fieldName).remove(pathSeq.tail.mkString("."))
      val j = this.remove(fieldName).set(fieldName, fieldValue)
      j
    }
  }

  def replace(path: String, value: Json): Json = {
    Json(value.node.replace(path.split("\\.").toList, value.node))
  }

  def isObjectOfIntValues: Boolean = {
    this.node match {
      case JObject(t) => isStringToJIntTuples(t)
      case _          => false
    }
  }

  private def isStringToJIntTuples(tuples: List[(String, JValue)]): Boolean = {
    tuples.forall { case (_, weight) =>
      weight != null && weight.isInstanceOf[JInt]
    }
  }

  private def readFromPath(json: JValue, path: String): JValue = {
    val pathSeq: Array[String] = path.split("\\.")
    pathSeq.foldLeft(json) { (a: JValue, pathElementStr: String) => {
        try {
          JsonPathElement.parse(pathElementStr) match {
            case JsonPathElement(Some(name), None)        => a \ name
            case JsonPathElement(Some(name), Some(index)) => (a \ name) (index)
            case JsonPathElement(None, Some(index))       => a (index)
          }
        } catch {
          case _: IndexOutOfBoundsException => JNothing
        }
      }
    }
  }

  def excludeNullValues: Json = Json(excludeNullValues(node))

  private def excludeNullValues(node: JValue): JValue = node match {
    case a: JArray  => excludeNullValuesFromJArray(a)
    case o: JObject => excludeNullValuesFromJObject(o)
    case other      => other
  }

  private def excludeNullValuesFromJArray(jArray: JArray): JArray = {
    JArray(jArray.arr.filterNot(_ == JNull).map(excludeNullValues))
  }

  private def excludeNullValuesFromJObject(jObject: JObject): JObject = {
    JObject(jObject.obj
      .filterNot(cond(_) { case JField(_, JNull) => true })
      .map { case JField(name, value) => JField (name, excludeNullValues(value))
    })
  }

}

object Json {

  def create(path: String, value: Json): Json = Json(writeToPath(path, value.node))

  def parse(json: String): Json = Json(JsonMethods.parse(json))

  private def writeToPath(path: String, jValue: JValue): JObject = {
    import org.json4s.JsonDSL._
    val pathSeq = path.split("\\.")
    val bottomField = JField(pathSeq.last, jValue)
    val nestedField = pathSeq.dropRight(1).foldRight(bottomField)(JField(_, _))
    JObject(nestedField)
  }

}
