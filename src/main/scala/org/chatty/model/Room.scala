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
    override def validations = valUnique("not a unique name") _ :: super.validations
  }

  /** このチャットルームをメンバー以外に公開しないことを表すフラグ。*/
  object memberOnly extends MappedBoolean(this) {
    override def defaultValue = false
  }

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

  val sqlFindByUser = 
    """select distinct r.* from %s r 
        join %s m on r.%s = m.%s
        where m.%s = ?
        order by r.%s""".format(
          Room.dbTableName,
          Member.dbTableName,
          Room.id.dbColumnName,
          Member.room.dbColumnName,
          Member.user.dbColumnName,
          Room.id.dbColumnName)

  /**
   * nameに一致するチャットルームを返す。
   * @param name チャットルームの名前
   * @return nameに一致するチャットルーム
   */
  def findByName(name: String) = find(By(Room.name, name))

  /**
   * ユーザーが所属するチャットルームを返す。
   * @param userId 対象のユーザーID
   * @return ユーザーが所属するチャットルーム
   */
  def findByUser(userId: Long) = 
    findAllByPreparedStatement({ conn =>
      val stmt = conn.connection.prepareStatement(sqlFindByUser)
      stmt.setLong(1, userId)
      stmt
    })
}
