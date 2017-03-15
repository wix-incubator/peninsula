package com.wix.peninsula.examples

import com.wix.peninsula.Json

object FieldsFiltering extends App {

  val jsonStr =
    """{
      "mobilePublished": true,
      "published": true,
      "title": "Forum",
      "description": "Forum",
      "sectionUrl": "https://forums.wix.com/",
      "defaultPage": "",
      "seoUrl": "https://forums.wix.com/?ssr=true",
      "refreshOnWidthChange": true
    }
    """

  val json = Json.parse(jsonStr)

  val onlyTitleAndPublished = json.only(Set("title", "published"))

  println(onlyTitleAndPublished)

}
