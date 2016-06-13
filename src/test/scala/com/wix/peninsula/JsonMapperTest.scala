package com.wix.peninsula

import CopyConfigFactory._
import org.specs2.mutable.SpecificationWithJUnit

class JsonMapperTest extends SpecificationWithJUnit {

  "map all objects in array when an object has a nested array" in {
    val jsonText = """[ { "name":"hotels", "features":[ { "feature_id":"2" } ] } ]"""
    val json = Json.parse(jsonText)

    val mapper = AllObjectsInArray(TransformationConfig(copyConfigs = Seq(copyField("name"))))

    mapper.map(json) must_== Json.parse("""[ { "name": "hotels" } ]""")
  }

}
