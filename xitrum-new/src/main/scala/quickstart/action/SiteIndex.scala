package quickstart.action

import xitrum.annotation.GET
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import akka.event.Logging

@GET("")
class SiteIndex extends DefaultLayout {
  beforeFilter{
    println("前置过滤器执行")
  }
  
  aroundFilter{
    action => 
      val begin = System.currentTimeMillis()
      action()
      val end = System.currentTimeMillis()
      val dt = end - begin
      println(s"The action took $dt [ms]")
  }
  
  def execute() {
    println("执行过Action")
    //If you don’t call respondXXX, Xitrum will keep the HTTP 
    //connection for you, and you can call respondXXX later
    //To check if the connection is still open, call channel.isOpen.
    val future: ChannelFuture = respondView()
    
    //通过respondXXX的返回值，可以在响应请求之后完成相关的操作，
    //如响应之后关闭连接，可以使用一下方式来完成
    //respondText("hello").addListener(ChannelFutureListener.CLOSE)
  }
  
  afterFilter{
    println("后置过滤器")
  }
}



