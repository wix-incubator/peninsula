Peninsula is a lightweight scala library enabling you to do json transformations without every converting your jsons into domain objects. Its main goal is to make building [coast-to-coast](http://mandubian.com/2013/01/13/JSON-Coast-to-Coast/) applications an easier and a more intuitive process.

It's also a collection of useful tools for working with Json AST.

Peninsula is an abstraction layer on top of [Json4s](https://github.com/json4s/json4s).

## Examples
#### Json transformation
Build a transformation configuration to describe the rules that later can be used
for transforming one json into another. TransformationConfig represents a collection
of copy configurations that each copyg one bit from the original json into the resulting json in a specific way.

In the example below `copyFields("id", "slug")` copies 2 fields id and slug as they are without making any changes to the property names of values. It expects the value to be a primitive type.

`copyField("name" -> "title")` copies the name field - but renames the property to *title*.

`copy("images")` copies the *images* field into the resulting json without making any assumptions about the value type. i.e. images value can be an object or an array.

```scala

scala> val config = TransformationConfig()
						.add(copyFields("id", "slug"))
						.add(copyField("name" -> "title"))
						.add(copy("images"))

scala> val json = Json.parse(
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

scala> json.transform(config)
res0: com.wixpress.peninsula.Json =
{
	"id": 1,
	"slug": "raw-metal",
	"name": "Raw Metal Gym",
	"images": {
		"top": "//images/top.jpg",
		"background": "//images/background.png"
	}
}
```


#### Extraction
Easily extract top level and nested values from json.

```scala
scala> val json = Json.parse("""{"id": 1, "name": "John", "location": {"city": "Vilnius", "country": "LT"}}""")

scala> json.extractString("location.city")
res: String = Vilnius

scala> json.extractStringOpt("location.city")
res: Option[String] = Some(Vilnius)

scala> json.extractStringOpt("location.postCode")
res: Option[String] = None

scala> json.extractLong("id")
res: Long = 1
```

#### Fields Filtering
Define which fields you want to be included into the resulting json and filter all the others out.
This might get in handy for implementation of restful json endpoints.

```scala
scala> val json = Json.parse("""{"id": 1, "name": "John", "office": "Wix Townhall", "role": "Enginner"}""")

scala> json.only(Set("id", "role")).compactRender
res: com.wixpress.peninsula.Json =
{
  "id" : 1,
  "role" : "Enginner"
}
```
