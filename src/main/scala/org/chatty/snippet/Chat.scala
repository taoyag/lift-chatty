package org.chatty.snippet

import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmds._

import org.chatty.model.User
import org.chatty.logic.{Chatty, AddMessage}

import scala.xml.NodeSeq

class Chat {
  def form = {
    val user = User.currentUser

    <span>
      { ajaxText("", m => {
          Chatty ! AddMessage(user.open_!, m)
          Noop
        }) }
    </span>
  }
}
