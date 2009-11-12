package org.chatty.model

import net.liftweb.http.{S, LiftSession}
import net.liftweb.mapper._
import net.liftweb.util._

import org.junit.runner.RunWith

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class MessageSpec extends FlatSpec with ShouldMatchers {

  val n = "messageSpec"
  var room: Room = _
  var member: Member = _
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

      room = Room.create.name(n).saveMe
      member = Member.join(room, user)
      test()
    } catch {
      case e => e.printStackTrace; throw e
    } finally {
    }
  }

  "Message" should "保存できる" in {
    val m = Message.post(room, member, "This is a test message.")
    m.message.is should equal ("This is a test message.")
    m.room.is should equal (room.id.is)
    m.member.is should equal (member.id.is)
    m.memberName should equal (member.name)
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

