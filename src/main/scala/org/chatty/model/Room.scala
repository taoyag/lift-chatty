package org.chatty.model

import net.liftweb.mapper._
import net.liftweb.http._
import net.liftweb.util._

/**
 * チャットルーム。
 */
class Room extends LongKeyedMapper[Room] 
    with IdPK 
    with TimestampMapper[Room] {

  def getSingleton = Room

  /** このチャットルームの名前。 */
  object name extends MappedString(this, 100) {
    override def dbIndexed_? = true
  }

  /** このチャットルームをメンバー以外に公開しないことを表すフラグ。*/
  object memberOnly extends MappedBoolean(this)

  /**
   * このチャットルームの発言を新しいものからcountで指定された件数分返す。
   * @param count 発言の件数
   * @return 発言のリスト
   */
  def messages(count: Int) = 
    Message.findAll(By(Message.room, this.id), 
                    StartAt(0), MaxRows(count),
                    OrderBy(Message.createdAt, Descending)).reverse

  /** このチャットルームの全てのメンバーを返す。*/
  def members = Member.findAll(By(Member.room, this.id))

  /** このチャットルームの全てのオーナー権限を持つメンバーを返す。 */
  def owners = 
    Member.findAll(By(Member.room, this.id),
                  By(Member.owner, true))
}

/**
 * Roomのコンパニオンオブジェクト。
 */
object Room extends Room 
    with LongKeyedMetaMapper[Room] 
    with TimestampMetaMapper[Room] {

  override def fieldOrder =
    List(id, name, createdAt, updatedAt)

  /**
   * nameに一致するチャットルームを返す。
   * @param name チャットルームの名前
   * @return nameに一致するチャットルーム
   */
  def findByName(name: String) = find(By(Room.name, name))
}
