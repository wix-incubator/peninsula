package com.wix.peninsula

import org.json4s.JsonAST.JArray

trait JsonMapper {
  def map(json: Json): Json
}

object IdentityMapper extends JsonMapper {
  override def map(json: Json): Json = json
}

case class AllObjectsInArray(config: TransformationConfig) extends JsonMapper {
  override def map(json: Json): Json = {
    val JArray(children) = json.node
    val elements = children.map(Json(_).transform(config).node)
    Json(JArray(elements))
  }
}
