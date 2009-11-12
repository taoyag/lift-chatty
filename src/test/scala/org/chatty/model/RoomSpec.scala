package org.chatty.model

import net.liftweb.http.{S, LiftSession}
import net.liftweb.mapper._
import net.liftweb.util._

import org.junit.runner.RunWith

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class RoomSpec extends FlatSpec with ShouldMatchers {

  val n = "roomSpec"
  var room: Room = _
  val user = User.create
  val otherUser = User.create

  override def withFixture(test: NoArgTest) {
    try {
      (new bootstrap.liftweb.Boot).boot
      CleanUpper.cleanRoom(n)

      user.firstName("taoyag")
      user.save

      otherUser.firstName("other")
      otherUser.save

      room = Room.create
      room.name(n).save
      test()
    } catch {
      case e => e.printStackTrace; throw e
    } finally {
    }
  }

  "Room" should "名前を指定して保存できる" in {
    Room.findByName(n) match {
      case Full(r) =>
        r.name.is should equal(room.name.is)
        r.memberOnly.is should be (false)
      case _ =>
        fail("Room not found. %s".format(n))
    }
  }

  it should "名前と公開フラグを指定して保存できる" in {
    room.memberOnly(true).save should be (true)
    Room.findByName(n) match {
      case Full(r) =>
        r.name.is should equal(room.name.is)
        r.memberOnly.is should be (true)
      case _ =>
        fail("Room not found. %s".format(n))
    }
  }

  it should "#messagesでチャットルームのメッセージを返す" in {
    val r = Room.findByName(n) openOr fail("Room not found. %s".format(n))

    val m = Member.join(r, user)
    Message.create.
      room(r.id).
      member(m.id).
      message("this is a message.").
      save
    r.messages(10) should have size (1)

    Message.create.
      room(r.id).
      member(m.id).
      message("this is an another message.").
      save
    r.messages(10) should have size (2)
  }

  it should "#membersでチャットルームの全てのメンバーを返す" in {
    val r = Room.findByName(n) openOr fail("Room not found. %s".format(n))

    Member.join(r, user)
    r.members should have size (1)

    Member.join(r, otherUser)
    r.members should have size (2)
  }

  it should "#ownersでチャットルームの全てのオーナーを返す" in {
    val r = Room.findByName(n) openOr fail("Room not found. %s".format(n))

    Member.join(r, user)
    r.owners should be ('empty)

    val m = Member.join(r, otherUser)
    m.owner(true).save
    r.owners should have size (1)
  }
}

object CleanUpper {
  val cleanRoom = 
    "delete from %s where %s = ?".format(
        Room.dbTableName,
        Room.name.dbColumnName)

  val cleanMessages = 
    "delete from %s where %s = ?".format(
        Message.dbTableName,
        Message.room.dbColumnName)

  val cleanMembers = 
    "delete from %s where %s = ?".format(
        Member.dbTableName,
        Member.room.dbColumnName)

  def cleanRoom(name: String) {
    Room.findByName(name) match {
      case Full(r) =>
        DB.use(DefaultConnectionIdentifier) { conn =>
          cleanMessages(conn, r)
          cleanMembers(conn, r)
          DB.prepareStatement(cleanRoom, conn) { stmt =>
            stmt.setString(1, name)
            stmt.execute()
          }
        }
      case _ => println("Room %s was not found. Nothing to do.".format(name))
    }
  }

  def cleanMessages(conn: SuperConnection, room: Room) {
    DB.prepareStatement(cleanMessages, conn) { stmt =>
      stmt.setLong(1, room.id.is)
      stmt.execute()
    }
  }

  def cleanMembers(conn: SuperConnection, room: Room) {
    DB.prepareStatement(cleanMembers, conn) { stmt =>
      stmt.setLong(1, room.id.is)
      stmt.execute()
    }
  }
}
