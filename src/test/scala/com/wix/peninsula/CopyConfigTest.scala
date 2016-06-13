package com.wix.peninsula

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

}
