# Peninsula
Peninsula is a Scala lib providing a collection of useful tools working with json AST and facilitating json transformations without ever converting your jsons into domain objects.

Its main goal is to eventually make building data centric [coast-to-coast](http://mandubian.com/2013/01/13/JSON-Coast-to-Coast/) applications an easier and a more intuitive process.

Technically Peninsula is an abstraction layer on top of [Json4s](https://github.com/json4s/json4s).

Disclaimer: even if it's used on production at Wix - this is an  early stage lib - with plenty of space for optimization and improvement. Contributions and comments are very welcome!

## Installation
Add the following dependency to your pom if you use maven
```xml
<dependency>
    <groupId>com.wix</groupId>
    <artifactId>peninsula</artifactId>
    <version>0.1.5</version>
</dependency>
```
For SBT users
```scala
val peninsula = "com.wix" % "peninsula" % "0.1.5"
libraryDependencies += peninsula
```

## Examples

All examples below can also be found in the following test: [ExampleTest.scala](https://github.com/wix/peninsula/blob/master/src/test/scala/com/wix/peninsula/examples/ExamplesTest.scala)

#### Extraction
Easily extract top level and nested values from json.

```scala
import com.wix.peninsula.Json

case class Person(id: Long, name: String)
case class Item(name: String, sale: Boolean)

val json = Json.parse(
  """
    {
      "id": 1,
      "name": "John",
      "location": {"city": "Vilnius", "country": "LT"},
      "customer": { "id": 1, "name": "John"},
      "items": [{"name": "tomatoes", "sale": true}, {"name": "snickers", "sale": false}]
    }
  """)
  
json.extractString("location.city") mustEqual "Vilnius"
result: "Vilnius"

json.extractStringOpt("location.city")
result: Some("Vilnius")

json.extractStringOpt("location.postCode")
result: None

json.extract[Person]("customer")
result: Person(id = 1, name = "John")

json.extract[Seq[Boolean]]("items.sale") mustEqual Seq(true, false)
result: Seq(true, false)

json.extract[Seq[Item]]("items")
result: Seq(Item(name = "tomatoes", sale = true), Item(name = "snickers", sale = false))

json.extract[Item]("items(1)") mustEqual 
result: Item(name = "snickers", sale = false)
```

You can also subselect a `json` by path and extract values from it 
```scala
val location: Json = json("location")

location.extractString("city")
result: "Vilnius"

location.extractString("country")
result: "LT"

val items: Json = json("items")

items.extractString("(0).name")
result: "tomatoes"

items.extractString("(1).name")
result: "snickers"

items.extract[Seq[String]]("name")
result: Seq("tomatoes", "snickers")
```

[Here](#api-reference)you can find comprehensive list of extraction methods.

#### Basic Json Transformation
Build a transformation configuration to describe the rules that later can be used
for transforming one json into another. TransformationConfig represents a collection
of copy configurations that each copy one bit from the original json into the resulting json in a specific way.

In the example below `copyFields("id", "slug")` copies 2 fields *id* and *slug* as they are without making any changes to the property names of values. It expects the value to be a primitive type.

`copyField("name" -> "title")` copies the name field - but renames the property to *title*.

`copy("images")` copies the *images* field into the resulting json without making any assumptions about the value type. i.e. images value can be an object or an array.

```scala
import com.wix.peninsula.CopyConfigFactory._
import com.wix.peninsula._

val config = TransformationConfig()
  .add(copyFields("id", "slug"))
  .add(copyField("name" -> "title"))
  .add(copy("images"))

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

json.transform(config)
com.wix.peninsula.Json =
{
  "id": 1,
  "slug": "raw-metal",
  "title": "Raw Metal Gym",
  "images": {
    "top": "//images/top.jpg",
    "background": "//images/background.png"
  }
}
```

#### A More Advanced Json Transformation

In the below example the `mergeObject` copier merges anything in the object specified onto the top level of the resulting json.

Nested properties can be accessed using dot based selectors e.g. `media.pictures.headerBackground`.

Also note how json values can be validated and transformed before copying them over to the resulting json.

```scala
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

json.transform(config)
result: com.wix.peninsula.Json =
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
```

#### Basic Json Translation

Translation differs from transformation in that it keeps the original json and merges the translation on top it.
In a basic case, when the translation and the original json have the same structure - no transformation config is needed.

```scala
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

json.translate(config)

result: com.wix.peninsula.Json =
{
  "id": 1,
  "slug": "raw-metal",
  "name":"Metalinis Gymas",
  "images": {
    "top": "//images/top.jpg",
    "background":"//images/translated-background.png"
  }
}
```
#### Custom Json Translation



```scala
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
       },
       "features": [
          { "id": 1, "description": "Convenient location" },
          { "id": 2, "description": "Lots of space" }
       ]
    }
  """)

val translation = Json.parse(
  """
    {
       "title":"Metalinis Gymas",
       "media": {
          "backgroundImage":"//images/translated-background.png"
       },
       "features": [
        { "id": 2, "description": "space translated" },
        { "id": 1, "description": "location translated" }
       ]
    }
  """)

val featureConfig = TransformationConfig().add(copyField("description"))

val config = TransformationConfig()
  .add(copyField("title" -> "name"))
  .add(copyField("media.backgroundImage" -> "images.background"))
  .add(copyArrayOfObjects(fromTo = "features", config = featureConfig, idField = "id"))

json.translate(translation, config)
result: com.wix.peninsula.Json =
{
  "id": 1,
  "slug": "raw-metal",
  "name":"Metalinis Gymas",
  "images": {
    "top": "//images/top.jpg",
    "background":"//images/translated-background.png"
  },
  "features": [
    { "id": 1, "description": "location translated" },
    { "id": 2, "description": "space translated" }
  ]
}
```

#### Fields Filtering
Define which fields you want to be included into the resulting json and filter all the others out.
This might get in handy for implementation of restful json endpoints.

```scala
import com.wix.peninsula.Json

val json = Json.parse("""{"id": 1, "name": "John", "office": "Wix Townhall", "role": "Engineer"}""")

json.only(Set("id", "role"))
result: com.wix.peninsula.Json =
{
  "id" : 1,
  "role" : "Engineer"
}
```

## API reference

There are different methods that allow you to extract values and structures from JSON:

```scala
json.extract[T](path: String): T
json.extractOpt[T](path: String): Option[T]
json.extractBoolean(path: String): Boolean
json.extractBooleanOpt(path: String): Option[Boolean]
json.extractInt(path: String): Int
json.extractIntOpt(path: String): Option[Int]
json.extractBigInt(path: String): BigInt
json.extractBigIntOpt(path: String): Option[BigInt]
json.extractLong(path: String): Long
json.extractLongOpt(path: String): Option[Long]
json.extractDouble(path: String): Double
json.extractDoubleOpt(path: String): Option[Double]
json.extractBigDecimal(path: String): BigDecimal
json.extractBigDecimalOpt(path: String): Option[BigDecimal]
json.extractString(path: String): String
json.extractStringOpt(path: String): Option[String]
```