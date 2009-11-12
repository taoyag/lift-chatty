package org.chatty.model

import net.liftweb.mapper._
import net.liftweb.http._
import net.liftweb.util._

/**
 * チャットルームでの1件の発言。
 */
class Message extends LongKeyedMapper[Message] 
    with IdPK 
    with TimestampMapper[Message] {

  def getSingleton = Message

  /** この発言があったチャットルーム。*/
  object room extends MappedLongForeignKey(this, Room) {
    override def dbIndexed_? = true
  }

  /** 発言したメンバー。*/
  object member extends MappedLongForeignKey(this, Member) {
    override def dbIndexed_? = true
  }

  /** 発言内容。*/
  object message extends MappedString(this, 1024)

  /** 発言したメンバーの名前。*/
  lazy val memberName = member.obj.map(_.name) openOr "Unknown"
}

/**
 * Messageのコンパニオンオブジェクト。
 */
object Message extends Message 
    with LongKeyedMetaMapper[Message]
    with TimestampMapper[Message] {

  override def fieldOrder = 
    List(id, room, member, message, createdAt, updatedAt)
}
