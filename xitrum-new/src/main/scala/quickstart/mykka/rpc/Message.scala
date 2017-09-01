package quickstart.mykka.rpc

/**
 * 定义case class 类
 */
//定义message接口,用于网络之间传送数据
trait Message extends Serializable

//Worker向Master发送注册消息,高速自己的资源情况
case class RegisterWorker(id: String, cores: Int, memory: Int) extends Message

//Master向Worker发送消息,告知存活的Master的地址(用于Master集群HA)
case class RegisteredWorker(masterUrl: String) extends Message

//Worker定期向Master发送心跳,表明自己还在存活
case class Heartbeat(id: String) extends Message

//Worker内部发送消息(单例),定期发送心跳
case object SendHeartbeat

//Master内部消息,清除过期的Master
case object CheckTimeOutWorker
