package org.chatty.comet

import net.liftweb.http.CometActor
import net.liftweb.http.js._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.jquery.JqJsCmds._
import net.liftweb.http._
import net.liftweb.http.S._
import net.liftweb.http.SHtml._
import net.liftweb.util.Full

import org.chatty.logic._
import org.chatty.model._

import scala.xml.NodeSeq

class ChatActor extends CometActor {
  override def defaultPrefix = Full("chat")
  val input = "input"
  var messages: List[Message] = Nil

  def form = {
    val user = User.currentUser.open_!

    def addMessage(m: String) = {
      if (m != "") {
        Chatty ! AddMessage(user, m)

        //SetValById(input, JE.Str(""))
        SetValById(input, JE.Str("")) &
        JsCmds.Run("$('#%s').focus()".format(input))
      } else {
        Noop
      }
    }

    <span>
      { ajaxText("", addMessage(_), ("id" -> input)) }
    </span>
  }

  def render = {
    bind("message" -> <div></div>,
         "form"    -> form)
  }

  override def localSetup {
    Chatty !? AddListener(this) match {
      case UpdateMessage(m) => messages = m
    }
  }

  override def localShutdown {
    Chatty ! RemoveListener(this)
  }

  def build(m: Message) = 
    <div class="message">
      <div class="user">{ m.owner }</div>
      <div class="text">{ m.message }</div>
    </div>

  def appendMessages(messages: List[Message]) = 
    AppendHtml("log", messages.flatMap(build _))

  override def lowPriority : PartialFunction[Any, Unit] = {
    case UpdateMessage(m) => {
      messages = m
      partialUpdate(appendMessages(m))
    }
  }
}
