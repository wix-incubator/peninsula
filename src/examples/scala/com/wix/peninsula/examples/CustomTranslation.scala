package com.wix.peninsula.examples

import com.wix.peninsula.{Json, TransformationConfig}
import com.wix.peninsula.CopyConfigFactory._

object CustomTranslation extends App {

  val json = Json.parse(
    """{
           "id":1,
           "slug":"raw-metal",
           "name":"Raw Metal Gym",
           "images":{
              "top":"//images/top.jpg",
              "background":"//images/background.png"
           },
           "features": [
              { "id": 1, "description": "Convenient location" },
              { "id": 2, "description": "Lots of space" }
           ]
        }
    """)

  val translation = Json.parse(
    """{
           "title":"Metalinis Gymas",
           "media": {
              "backgroundImage":"//images/translated-background.png"
           },
           "features": [
            { "id": 2, "description": "space translated" },
            { "id": 1, "description": "location translated" }
           ]
        }
    """)

  val featureConfig = TransformationConfig().add(copyField("description"))

  val config = TransformationConfig()
    .add(copyField("title" -> "name"))
    .add(copyField("media.backgroundImage" -> "images.background"))
    .add(copyArrayOfObjects(fromTo = "features", config = featureConfig, idField = "id"))

  val translatedJson = json.translate(translation, config)

  println(translatedJson)

}
