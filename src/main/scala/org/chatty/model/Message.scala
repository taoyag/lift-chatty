package org.chatty.model

import java.util.regex.Pattern._

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
  object message extends MappedString(this, 1024) {
    override def validations = 
      valRegex(compile("""^.+$"""), "message is required") _ ::
      super.validations
  }

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

  /**
   * 新しいメッセージを投稿する。
   * @param r 発言したチャットルーム
   * @param m 発言したメンバー
   * @param text 発言内容
   * @return 新しいメッセージ
   */
  def post(r: Room, m: Member, text: String) = 
    Message.create.room(r.id).member(m.id).message(text).saveMe
}
