package quickstart.mykka.robot

trait Action {
  val message: String
  val time: Int
}

case class TurnOnLinght(time:Int) extends Action{
  val message = "我去开灯了 (#^.^#)"
}

case class BoilWater(time: Int) extends Action{
  val message = "我去注水啦 ♪(^∇^*)"
}