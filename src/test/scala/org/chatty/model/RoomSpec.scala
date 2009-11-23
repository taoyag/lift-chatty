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

  it should ".findByUserでユーザーが閲覧可能なチャットルームを返す" in {
    val n1 = "findByUser1"
    val n2 = "findByUser2"
    val r1 = Room.create.name(n1).saveMe
    val r2 = Room.create.name(n2).saveMe
    try {
      Member.join(r1, user)
      Member.join(r2, user)

      val rooms = Room.findByUser(user.id)
      rooms.foreach(_.memberOnly should equal (false))
    } finally {
      CleanUpper.cleanRoom(n1)
      CleanUpper.cleanRoom(n2)
    }
  }
}

