package com.wix.peninsula

case class JsonPathElement(name: String, index: Option[Int])

object JsonPathElement {

  val regex = """(.*)\((\d+)\)$""".r

  def parse(pathItem: String): JsonPathElement = {
    regex.findFirstMatchIn(pathItem)
      .map(m => JsonPathElement(m.group(1), Some(m.group(2).toInt)))
      .getOrElse(JsonPathElement(pathItem, None))
  }
}
