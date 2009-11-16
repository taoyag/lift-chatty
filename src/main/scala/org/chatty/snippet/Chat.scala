package org.chatty.snippet

import net.liftweb.http._
import net.liftweb.http.SHtml._
import net.liftweb.util._

import org.chatty.logic._
import org.chatty.model._

import scala.xml.{NodeSeq, Text}

class Chat {
  def roomName = S.param("id") match {
    case Full(id) =>
      Room.findByKey(id.toLong).map({ r =>
        Text(r.name)
      }) openOr Text("No room found")
    case _ =>
      Text("Invalid parameter")
  }

  def chat(html: NodeSeq) = {
    val name = StringHelpers.randomString(10)
    for {
      session <- S.session
      id <- S.param("id")
    } session.setupComet("ChatActor", Full(name), CurrentRoomId(id.toLong))

    <lift:comet type="ChatActor" name={name}>
      <div id="log">
        <chat:message>Loading...</chat:message>
      </div>
      <div id="form"><chat:form/></div>
    </lift:comet>
  }
}

case class CurrentRoomId(id: Long)
