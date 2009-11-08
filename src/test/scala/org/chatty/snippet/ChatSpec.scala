package org.chatty.snippet

import net.liftweb.http.{S, LiftSession}
import net.liftweb.mapper.BaseMetaMapper
import net.liftweb.util._

import org.junit.runner.RunWith

import org.chatty.model.User
import org.chatty.snippet.Chat

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class ChatSpec extends FlatSpec with ShouldMatchers {
  val session = new LiftSession("", StringHelpers.randomString(20), Empty)

  override def withFixture(test: NoArgTest) {
    try {
      (new bootstrap.liftweb.Boot).boot

      S.initIfUninitted(session) {
        val user = User.create
        user.firstName("t")
        user.lastName("taoyag")
        user.save
        User.logUserIn(user)
        test()
      }
    } finally {
    }
  }

  "Chat" should "foo" in {
    val chat = new Chat
    chat.form should not be null
  }
}
