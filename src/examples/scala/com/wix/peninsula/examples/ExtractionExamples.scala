package com.wix.peninsula.examples

import com.wix.peninsula.Json

object ExtractionExamples extends App {

  val jsonStr =
    """
      {
        "response": {
          "status": "error",
          "errors": [
            {
              "code": 101,
              "description": "login is invalid"
            },
            {
              "code": 102,
              "description": "password is too short"
            }
          ],
          "developers": [
            {"name": "Valdemaras Repšys"},
            {"name": "Ivan Kamenev"}
          ]
        }
      }
    """

  val json = Json.parse(jsonStr)

  val isError = json("response.status").contains("error")

  if (isError) {
    val errorsDescriptions = json("response.errors.description").extract[List[String]]

    val responsiblePerson = json.extractString("response.developers(0).name")

    println(s"Who is to blame: $responsiblePerson")
    println(s"What exactly happened: $errorsDescriptions")
  }

}
