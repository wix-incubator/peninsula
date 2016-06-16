package com.wix.peninsula.examples

import org.specs2.mutable.SpecificationWithJUnit

class ExamplesTest extends SpecificationWithJUnit {

  "Basic Json transformation" in {
    import com.wix.peninsula._
    import com.wix.peninsula.CopyConfigFactory._

    val config = TransformationConfig()
      .add(copyFields("id", "slug"))
      .add(copyField("name" -> "title"))
      .add(copy("images"))

    val json = Json.parse("""
      {
         "id":1,
         "slug":"raw-metal",
         "name":"Raw Metal Gym",
         "images":{
            "top":"//images/top.jpg",
            "background":"//images/background.png"
         }
      }
    """)

    json.transform(config) must_== Json.parse("""
      {
        "id": 1,
        "slug": "raw-metal",
        "title": "Raw Metal Gym",
        "images": {
          "top": "//images/top.jpg",
          "background": "//images/background.png"
        }
      }
    """)
  }

  "A More Advanced Json Transformation" in {
    import com.wix.peninsula._
    import com.wix.peninsula.CopyConfigFactory._
    import com.wix.peninsula.JsonValidators.NonEmptyStringValidator
    import org.json4s.JsonAST.JString

    object HttpsAppender extends JsonMapper {
      override def map(json: Json): Json = Json(json.node match {
        case JString(url) => JString("https:" + url)
        case x => x
      })
    }

    val config = TransformationConfig()
      .add(copyField("id"))
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
             }
          }
        """)

    json.transform(config) must_== Json.parse(
      """
        {
        	"id" : 1,
        	"name" : "Raw metal gym",
        	"description" : "The best gym in town. Come and visit us today!",
        	  "media" : {
        	    "pictures" : {
        	      "headerBackground" : "https://images/top.jpg"
        	    }
        	  }
        }
      """)
  }


  "Basic Json Translation" in {
    import com.wix.peninsula._

    val json = Json.parse(
      """
        {
           "id":1,
           "slug":"raw-metal",
           "name":"Raw Metal Gym",
           "images":{
              "top":"//images/top.jpg",
              "background":"//images/background.png"
           }
        }
      """)

    val config = Json.parse(
      """
        {
           "name":"Metalinis Gymas",
           "images":{
              "background":"//images/translated-background.png"
           }
        }
      """)

    json.translate(config) must_== Json.parse(
      """
        {
          "id": 1,
          "slug": "raw-metal",
          "name":"Metalinis Gymas",
            "images": {
              "top": "//images/top.jpg",
              "background":"//images/translated-background.png"
            }
        }
      """)
  }

  """Custom Json Translation""" in  {
    import com.wix.peninsula._
    import com.wix.peninsula.CopyConfigFactory._

    val json = Json.parse(
      """
        {
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
      """
        {
           "title":"Metalinis Gymas",
           "media":{
              "backgroundImage":"//images/translated-background.png"
           }
        }
      """)

    val config = TransformationConfig()
      .add(copyField("title" -> "name"))
      .add(copyField("media.backgroundImage" -> "images.background"))

    json.translate(translation, config) must_== Json.parse(
      """
        {
          "id": 1,
          "slug": "raw-metal",
          "name":"Metalinis Gymas",
          "images": {
            "top": "//images/top.jpg",
            "background":"//images/translated-background.png"
          }
        }
      """)
  }

  "Extraction" in {
    import com.wix.peninsula.Json

    val json = Json.parse("""{"id": 1, "name": "John", "location": {"city": "Vilnius", "country": "LT"}}""")

    json.extractString("location.city") must_== "Vilnius"

    json.extractStringOpt("location.city") must_== Some("Vilnius")

    json.extractStringOpt("location.postCode") must_== None

    json.extractLong("id") must_== 1L
  }

  "Fields Filtering" in {
    import com.wix.peninsula.Json

    val json = Json.parse("""{"id": 1, "name": "John", "office": "Wix Townhall", "role": "Engineer"}""")

    json.only(Set("id", "role")) must_== Json.parse(
      """
        {
          "id" : 1,
          "role" : "Engineer"
        }
      """
    )
  }







}
