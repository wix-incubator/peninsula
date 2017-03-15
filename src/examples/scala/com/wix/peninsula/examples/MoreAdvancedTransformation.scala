package com.wix.peninsula.examples

import com.wix.peninsula.CopyConfigFactory._
import com.wix.peninsula.JsonValidators.NonEmptyStringValidator
import com.wix.peninsula.{Json, JsonMapper, TransformationConfig}
import org.json4s.JsonAST.JString

object MoreAdvancedTransformation extends App {

  object HttpsAppender extends JsonMapper {
    override def map(json: Json): Json = Json(json.node match {
      case JString(url) => JString("https:" + url)
      case x => x
    })
  }

  val config = TransformationConfig()
    .add(copyField("id"))
    .add(copyField("icons(0)" -> "icon"))
    .add(mergeObject("texts"))
    .add(copyField("images.top" -> "media.pictures.headerBackground")
      .withValidators(NonEmptyStringValidator)
      .withMapper(HttpsAppender))

  val json = Json.parse(
    """
          {
             "id":1,
             "slug":"raw-metal",
             "name":"Raw Metal Gym",
             "texts": {
               "name": "Raw metal gym",
               "description": "The best gym in town. Come and visit us today!"
             },
             "images":{
                "top":"//images/top.jpg",
                "background":"//images/background.png"
             },
             "icons": ["large.png", "small.png"]
          }
    """)

  val transfromedJson = json.transform(config)

  println(transfromedJson.prettyRender)

}
