package com.wix.peninsula

import org.specs2.execute.Result
import org.specs2.matcher.MustExpectable
import org.specs2.mutable.SpecificationWithJUnit


class StringifyTest extends SpecificationWithJUnit {

  "matcher error" should {
    "be informative" >> {
      val result = MustExpectable.apply(Json(null, null)).applyMatcher(beEqualTo(Json()))
      result.message must beEqualTo("'Json(null,null)' is not equal to '{ }'")
    }
  }

  "toString" should {

    "return <null> when initialized with nulls" >> {
      Json(null, null).toString must beEqualTo("Json(null,null)")
    }

    "return <null> when node initialized with null" >> {
      Json(null).toString must beMatching("Json\\(null,org.json4s.DefaultFormats.+\\)")
    }

    "return <null format> when format is null" >> {
      Json.parse("""{"a":1}""").copy(formats = null).toString must beEqualTo("Json(JObject(List((a,JInt(1)))),null)")
    }

    "default object" >> {
      Json().toString must beEqualTo("{ }")
    }

    "render pretty JSON" >> {
      Json.parse("""{"a":1}""").toString must beEqualTo(
        """{
          |  "a" : 1
          |}""".stripMargin)
    }
  }

}
