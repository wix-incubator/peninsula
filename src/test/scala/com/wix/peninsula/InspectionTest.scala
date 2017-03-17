package com.wix.peninsula

import org.specs2.mutable.SpecificationWithJUnit

class InspectionTest extends SpecificationWithJUnit {

  "Inspection" should {

    "check if value exists" in {
      val json = Json.parse("""{"error": 1}""")

      json("error").exists mustEqual true
      json("something").exists mustEqual false
      json.exists("error") mustEqual true
      json.exists("something") mustEqual false
    }

    "check if json contains string value" in {
      val json = Json.parse("""{"status": "success"}""")

      json("status").contains("success") mustEqual true
      json("status").contains("failure") mustEqual false
      json.contains("status", "success") mustEqual true
      json.contains("status", "failure") mustEqual false
    }

    "check if value is null" in {
      val json = Json.parse("""{"status": null, "id": 1}""")

      json("status").isNull mustEqual true
      json("error").isNull mustEqual false
      json("id").isNull mustEqual false
      json.isNull("status") mustEqual true
      json.isNull("error") mustEqual false
      json.isNull("id") mustEqual false
    }

  }

}
