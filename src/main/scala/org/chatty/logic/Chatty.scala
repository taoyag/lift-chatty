package org.chatty.logic

import org.chatty.model.{User, Message}

import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable.{HashSet, ListBuffer}

case class AddListener(listener: Actor)
case class RemoveListener(listener: Actor)
case class UpdateMessage(messages: List[Message])
case class AddMessage(user: User, m: String)

object Chatty extends Actor {
  val listeners = new HashSet[Actor]
  val messages = new ListBuffer[Message]

  def notifyListeners(messages: List[Message]) = 
    listeners.foreach(_ ! UpdateMessage(messages))

  def act = {
    loop {
      react {
        case AddListener(l: Actor) =>
          listeners.incl(l)
          reply(UpdateMessage(messages.toList))
        case RemoveListener(l: Actor) =>
          listeners.excl(l)
        case AddMessage(user: User, m: String) =>
          val message = Message.create.owner(user)
          println("AddMessage" + message)
          message.message(m)
          notifyListeners(List(message))
      }
    }
  }

  start
}
