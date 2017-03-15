package com.wix.peninsula.examples

import com.wix.peninsula.CopyConfigFactory._
import com.wix.peninsula.{Json, TransformationConfig}

object SimpleTransformation extends App {

  val config = TransformationConfig()
    .add(copyFields("id", "slug"))
    .add(copyField("name" -> "title"))
    .add(copy("images"))

  val json = Json.parse(
  """{
        "id":1,
        "slug":"raw-metal",
        "name":"Raw Metal Gym",
        "images":{
          "top":"//images/top.jpg",
          "background":"//images/background.png"
        }
     }
  """)

  val transformedJson = json.transform(config)

  println(transformedJson)

}
