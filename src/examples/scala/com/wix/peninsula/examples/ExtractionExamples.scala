package com.wix.peninsula.examples

import com.wix.peninsula.Json

trait ExtractionExamples

object WhoIsToBlame extends App {

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

object WeirdUserDatabase extends App {

  val jsonStr =
    """
      {
        "wix_users": [
          {
            "id": "uuid1",
            "name": "Avishai"
          },
          {
            "id": 2,
            "name": "Pwned by G0Dandy"
          },
          {
            "id": true,
            "name": "Pwned by WorldPressure"
          },
          {
            "id": null,
            "name": "Pwned by Vovan the Freelancer"
          },
          {
            "id": {";": "truncate table 'wix_users'"},
            "name": "Sincerely yours, DBA"
          },
          {
            "name": "Random guy without id"
          }
        ]
      }
    """

  val json = Json.parse(jsonStr)

  println(""""uuid1" is extracted as """ + json.extractStringTry("wix_users(0).id"))
  println("""2 is extracted as """ + json.extractStringTry("wix_users(1).id"))
  println("""true is extracted as """ + json.extractStringTry("wix_users(2).id"))
  println("""null is extracted as """ + json.extractStringTry("wix_users(3).id"))
  println("""{";": "truncate table 'users'"} is extracted as """ + json.extractStringTry("wix_users(4).id"))
  println("""nonexistent element is extracted as """ + json.extractStringTry("wix_users(5).id"))

}

case class Character(id: Int, firstName: String, secondName: String, relatives: List[Relative])
case class Relative(id: Int, name: String, preferences: Option[Preferences])
case class Preferences(iceCream: Boolean, candy: Boolean, gold: Boolean)

object DonaldDuckIsOnCharge extends App {

  val jsonStr =
    """
      {
        "id": 1,
        "firstName": "Donald",
        "secondName": "Duck",
        "relatives": [
          {
            "id": 1,
            "name": "Huey",
            "preferences": {
              "iceCream": true,
              "candy": true,
              "gold": false
            }
          },
          {
            "id": 2,
            "name": "Dewey"
          },
          {
            "id": 3,
            "name": "Louie"
          }
        ]
      }
    """

  val json = Json.parse(jsonStr)

  println("Disney character is: " + json.extract[Character])
  println("Huey's preferences are: " + json.extract[Preferences]("relatives(0).preferences"))

}
