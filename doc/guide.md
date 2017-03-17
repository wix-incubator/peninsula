# Peninsula
Occasionaly, in software design, the choices we make are dictated not by best practices and what we believe is right. Instead they are more influenced by a desire to reduce our cognitive load and avoid complexity. Which is not bad per se, it's just that the perfect design should have both: have a feeling of the rigt way of doing things and also minimize the cognitive load.

One area where we found engineers choose less complexity over good design is dealing with jsons in our scala microservices. And this is caused by the complexity of dealing with simple jsons in scala using existing solutions. JSON - stands for Javascript Object Notation. Let's see how easy it is to perform a simple task on a javascipt object in Javascript.

```javascript
var json = {"response": { "status": "error", "error": "Some error message" }}

if (json.status === "error") {
  console.log(json.error)
}
```

In Peninsula we made doing simple things simple - like in Javascript.
```scala
import com.wix.peninsula.Json;

val json = Json.parse("""{"response": { "status": "error", "error": "Some error message" }}""");

if (json.contains("response.status", "error")) {
  println(json.extractString("response.error"))
}
```

You're working with json data - so the main domain object is `Json`. 

No ObjectMappers. No module registrations. No impicit Formats or serialization strategies.

## Path notation

With the simplicity in mind we developed a simple notation to define a path of a json value. We took inspiration from how Javascript operates thus making it familiar to developers who know javascript or even simple OOP.

Let's take an example json
```scala
val json = Json.parse("""{"response": { "users": {"name": "John"}, {"name": "Peter"}]}}""")
```

Every object of type Json can be subselected this way to produce another Json object.
```scala
json("response") 
//result: Json({ "users": [{"name": "John"}, {"name": "Peter"}]}})
```

Use dot notation to navivigate trees of json objects
```scala
json("response.users") 
//result: Json([{"name": "John"}, {"name": "Peter"}])
```

Peninsula also lets you work conveniently with json arrays
```scala
json("response.users[0]") //result: Json({"name": "John"}))
json("response.users[0].name") //result: Json("John")
json("response.users.name") //result: Json("["John", "Peter"]")
```

## Extractions and inspections - making it predictable

Path notation can be used to extract primitive values from the json. We made extractions to be simple, consistent in how they behave and react to unexpected and also intuitive for the majority fo developers. We surveyed engineers at Wix to find out what intuitive is.

Every extraction method comes in 2 variations: extract and extractAs.
```scala
json.extractString("path")
json.extractAsString("path")
```

`extractString` will return a string only if a string value exists in the given path. Otherwise it will throw one of the following exceptions.
```
JsonElementIsNull //exception if value is null
JsonPathDoesntExist //exception if the property on the path is not present
UnexpectedJsonElementException //if the type of the value doesn't match the type expected
```

There are 6 extract methods for different types
```scala
extractString(path: String): String
extractBoolean(path: String): Boolean
extractInt(path: String): Int
extractBigInt(path: String): BitInt
extractLong(path: String): Long
extractDouble(path: String): Double
```

All of them behave consistently regarding null values, non existent property, wrong types. In all 3 cases they throw a respective exception.

Alternatively, if you prefere more loose type checking, you will want to use the 'extractAs' methods that will try and coerce a wrong type into becoming the type desired. 
```scala
extractAsString(path: String): String
extractAsBoolean(path: String): Boolean
extractAsInt(path: String): Int
extractAsBigInt(path: String): BitInt
extractAsLong(path: String): Long
extractAsDouble(path: String): Double
```

Example:
```scala
val json = Json.parse("""{"status": 1}""");
json.extractAsString("status")
//result: "1" 

json.extractString("status") 
//will throw UnexpectedJsonElementException(Expected json type: string, found json type: big integer, actual json was: 1)
```

As a sorts of syntactic sugar we also provide the `extract Try` and `extractAs Try` methods for each primitive type
```scala
extractStringTry(path: String): String
extractBooleanTry(path: String): Boolean
extractIntTry(path: String): Int
extractBigIntTry(path: String): BitInt
extractLongTry(path: String): Long
extractDoubleTry(path: String): Double
extractAsStringTry(path: String): String
extractAsBooleanTry(path: String): Boolean
extractAsIntTry(path: String): Int
extractAsBigIntTry(path: String): BitInt
extractAsLongTry(path: String): Long
extractAsDoubleTry(path: String): Double
```

The try methods are equivalent to wrapping an extract method into a Try. I.e. `json.extractString("path")` is equivalent to `Try(json.extractString("path"))`

However, we recommend using extractString when you expect the value to exist and not to be null. 
And when you expect the property value to be absent sometimes will be absent or nullyou should use `json.extractStringTry`

Alternatively you can make sure the value exists in advance using inspections - so you're safe to use the simple version of the extract method e.g.

```scala
if (json.isString(path) {
  json.extractString(path)
}
```

`isString` true guarantees the success of `extractString` with the same path.

Peninsula has inspections for all primitive types
```scala
isString(path: String)
isDouble(path: String)
isBigDecimal(path: String)
isInt(path: String)
isBigInt(path: String)
isBoolean(path: String)
```

Also for non primitives and other scenarios
```scala
isArray(path: String)
isObject(path: String)
isNull(path: String)
exists(path: String) // true if value on the path exists. Even if it's null 
```

## Extracting case classes and structures

Peninsula provides two methods that you can use to extract any type. Including case classes.

```scala
val json = Json.parse("""{"users": [{"firstName": "John", "lastName": "Doe"}, {"firstName": "Jonas"}]}""")

case class User(firstName: String, lastName: Option[String])
json.extract[User]("users[0]") 
//result: User("firstName", "lastName")

json.extract[Seq[User]]("users")
//result: Seq(User("John", "Doe"), User("Jonas", None))
```

## Other useful methods
```scala
json.prettyPrint // prints json in a human readable way
json.compactPrint // prints json in a compact way for wiring over network
```
