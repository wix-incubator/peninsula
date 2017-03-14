package com.wix.peninsula

case class JsonPathElement(name: Option[String], index: Option[Int])

object JsonPathElement {

  private val SimpleNameRegex    = """(.*[^\)])$""".r
  private val NameWithIndexRegex = """(.+)\((\d+)\)$""".r
  private val IndexOnlyRegex     = """^\((\d+)\)$""".r


  def parse(pathItem: String): JsonPathElement = {
    pathItem match {
      case SimpleNameRegex(name)           => JsonPathElement(Some(name), None)
      case NameWithIndexRegex(name, index) => JsonPathElement(Some(name), Some(index.toInt))
      case IndexOnlyRegex(index)           => JsonPathElement(None, Some(index.toInt))
      case _                               => JsonPathElement(Some(pathItem), None)
    }
  }

  private def parseNameWithIndex(pathItem: String): Option[JsonPathElement] = {
    NameWithIndexRegex.findFirstMatchIn(pathItem)
      .map(m => JsonPathElement(Some(m.group(1)), Some(m.group(2).toInt)))
  }

}
