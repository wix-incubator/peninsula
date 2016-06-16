package com.wix.peninsula

import com.wix.peninsula.JsonValidators.NonEmptyStringValidator
import org.json4s.JsonAST.JString
import org.specs2.mutable.SpecificationWithJUnit

class CopyConfigTest extends SpecificationWithJUnit {

  "CopyConfig" should {
    "default to identity mapper" in {
      CopyFieldConfig("from", "to").mapper must_== IdentityMapper
    }

    "set mapper" in {
      object Mapper extends JsonMapper {
        override def map(json: Json): Json = json
      }
      val config = CopyFieldConfig("from", "to")
      config.withMapper(Mapper).mapper must_== Mapper
    }
  }

  "IdentityMapper" should {
    "return the value mapped" in {
      val json = Json.parse("""{"a": "b"}""")
      IdentityMapper.map(json) must_== json
    }
  }



  "random test" in {

    import CopyConfigFactory._

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
	   "id":"1",
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

    json.transform(config) must_== 1

  }

}
