package com.wix.peninsula.examples

import com.wix.peninsula.Json

object SimpleTranslation extends App {
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

  val translation = Json.parse(
  """{
       "name":"Metalinis Gymas",
       "images":{
         "background":"//images/translated-background.png"
       }
     }
  """)

  val translatedJson = json.translate(translation)

  println(translatedJson)
}
