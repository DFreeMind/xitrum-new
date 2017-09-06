package quickstart.scala.future

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.Success

object FutureA {
  def main(args: Array[String]): Unit = {
    val fut = Future {
      Thread.sleep(1000)
      1 + 1
    }

    //注册回调函数
    fut onComplete {
      case Success(r) => println(s"the result is ${r}")
      case _          => println("_")
    }
    println("now working")
    Thread.sleep(1000)

    //组合多个Future
    val fut1 = Future {
      println("enter task1")
      Thread.sleep(2000)
      1 + 1
    }

    val fut2 = Future {
      println("enter task2")
      Thread.sleep(1000)
      2 + 2
    }
    
    //第一种组合方式
    fut1.flatMap { v1 =>
      fut2.map { v2 =>
        println(s"the result is ${v1 + v2}")
      }
    }
    //第二种组合方式
    for {
    	v2 <- fut2
      v1 <- fut1
    } yield println(s"the for result is ${v1 + v2}")
    
    Thread.sleep(2500)
  }

}