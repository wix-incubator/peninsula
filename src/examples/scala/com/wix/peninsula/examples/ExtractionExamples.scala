package com.wix.peninsula.examples

import com.wix.peninsula.Json

/**
  * @author Ivan V Kamenev <ivanka@wix.com>
  */
object ExtractionExamples extends App {

  val json =
    """
      |{
      |  "response": {
      |    "status": "error",
      |    "errors": [
      |      {
      |        "code": 101,
      |        "description": "login is invalid"
      |      },
      |      {
      |        "code": 102,
      |        "description": "password is too short"
      |      }
      |    ]
      |  }
      |}
    """.stripMargin

  val root = Json.parse(json)

  // check whether response status is error
  val isError = root("response.status").contains("error")

  if (isError) {
    // retrieve description for every error in the list
    val errorsDescriptions = root("response.errors.description").extract[List[String]]


    println(s"Errors happened: $errorsDescriptions")
  }

}
