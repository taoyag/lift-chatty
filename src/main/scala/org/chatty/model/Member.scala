package org.chatty.model

import net.liftweb.mapper._
import net.liftweb.http._

/**
 * チャットルームのメンバー。
 */
class Member extends LongKeyedMapper[Member]
    with IdPK
    with TimestampMapper[Member] {

  def getSingleton = Member

  /** 所属するチャットルーム。*/
  object room extends MappedLongForeignKey(this, Room)
  /** このメンバー自身。*/
  object user extends MappedLongForeignKey(this, User)
  /** チャットルームのオーナーを表すフラグ。*/
  object owner extends MappedBoolean(this)

  /** メンバーの名前。 */
  def name = user.obj.map(_.firstName.is) openOr "Unknown"
}

/**
 * Memberのコンパニオンオブジェクト。
 */
object Member extends Member
    with LongKeyedMetaMapper[Member]
    with TimestampMetaMapper[Member] {

  override def fieldOrder = 
    List(id, room, user, owner, createdAt, updatedAt)

  /**
   * チャットルームにユーザーを参加させる。
   * @param r チャットルーム
   * @param u 参加するユーザー
   * @return 成功の場合はtrue
   */
  def join(r: Room, u: User) = 
    this.create.room(r).user(u).save
}
