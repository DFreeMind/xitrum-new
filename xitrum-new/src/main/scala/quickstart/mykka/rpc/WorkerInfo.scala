package quickstart.mykka.rpc

class WorkerInfo(val id: String, val cores: Int, val memory: Int) {

  //最后一次发送心跳时间
  var lastHeartbeatTime: Long = _
}
