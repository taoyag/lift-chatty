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
import org.chatty.snippet._

import scala.xml.{NodeSeq, Text}

class ChatActor extends CometActor {
  override def defaultPrefix = Full("chat")
  val input = "input"
  var room: Room = _
  var messages: List[Message] = Nil

  def form = {
    def addMessage(m: String) = {
      println(this)
      if (m != "") {
        println("addMessage:room="+room.id)
        Chatty.chatty(room) ! AddMessage(User.currentUser.open_!, m)

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
  }

  override def localShutdown {
    println("localShutdown")
    Chatty.chatty(room) ! RemoveListener(User.currentUser.open_!, this)
  }

  def build(m: Message) = 
    <div class="message">
      <div class="user">{ m.memberName }</div>
      <div class="text">{ m.message }</div>
    </div>

  def appendMessages(messages: List[Message]) = 
    AppendHtml("log", messages.flatMap(build _))

  override def lowPriority : PartialFunction[Any, Unit] = {
    case UpdateMessage(m) =>
      println("lowPriority:"+m)
      partialUpdate(appendMessages(m))

    case CurrentRoomId(id) =>
      Room.findByKey(id).map({ r =>
        room = r
        println("AddListener")
        Chatty.chatty(room) !? AddListener(User.currentUser.open_!, this) match {
          case UpdateMessage(m) => appendMessages(m)
        }
      }) openOr Text("No room found")
  }
}
