# Peninsula
Peninsula is a Scala library enabling you to do json transformations without ever converting your jsons into domain objects. Its main goal is to make building [coast-to-coast](http://mandubian.com/2013/01/13/JSON-Coast-to-Coast/) applications an easier and a more intuitive process.

It's also a collection of useful tools for working with Json AST.

Peninsula is an abstraction layer on top of [Json4s](https://github.com/json4s/json4s).

## Examples

#### Basic Json Transformation
Build a transformation configuration to describe the rules that later can be used
for transforming one json into another. TransformationConfig represents a collection
of copy configurations that each copyg one bit from the original json into the resulting json in a specific way.

In the example below `copyFields("id", "slug")` copies 2 fields id and slug as they are without making any changes to the property names of values. It expects the value to be a primitive type.

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
     }
  }
  """)

val translation = Json.parse(
  """
  {
     "title":"Metalinis Gymas",
     "media":{
        "backgroundImage":"//images/translated-background.png"
     }
  }
  """)

val config = TransformationConfig()
  .add(copyField("title" -> "name"))
  .add(copyField("media.backgroundImage" -> "images.background"))

json.translate(translation, config)
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

#### Extraction
Easily extract top level and nested values from json.

```scala
import com.wix.peninsula.Json

val json = Json.parse("""{"id": 1, "name": "John", "location": {"city": "Vilnius", "country": "LT"}}""")

json.extractString("location.city")
result: String = Vilnius

json.extractStringOpt("location.city")
result: Option[String] = Some(Vilnius)

json.extractStringOpt("location.postCode")
result: Option[String] = None

json.extractLong("id")
result: Long = 1
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
