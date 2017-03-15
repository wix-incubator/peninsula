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
      |    ],
      |    "developers": [
      |      {"name": "Valdemaras Repšys"},
      |      {"name": "Ivan Kamenev"}
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

    // find first developer in the list, he will be responsible for everything
    val responsiblePerson = root.extractString("response.developers(0).name")

    println(s"Who is to blame: $responsiblePerson")
    println(s"What exactly happened: $errorsDescriptions")

    // then send an email to the responsible person with list of errors
  }

}
