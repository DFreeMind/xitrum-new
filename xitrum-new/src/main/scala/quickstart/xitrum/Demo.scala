package quickstart.xitrum

import xitrum.util.SeriDeseri

object Demo extends App {
  //JSON 示例
  case class Person(name: String, age: Int, phone: Option[String])
  val person1 = Person("Jack", 20, None)
  val json = SeriDeseri.toJson(person1)
  println(json)
  val person2 = SeriDeseri.fromJson[Person](json)
  println(person2)
}