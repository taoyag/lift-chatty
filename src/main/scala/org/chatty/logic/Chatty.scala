package org.chatty.logic

import org.chatty.model._

import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable.{HashSet, ListBuffer, HashMap}

case class AddListener(room: Room, listener: Actor)
case class RemoveListener(room:Room, listener: Actor)
case class UpdateMessage(messages: List[Message])
case class AddMessage(room: Room, user: User, message: String)
//case class AddMessage(user: User, m: String)

object Chatty extends Actor {
  val listeners = new HashMap[Long, HashSet[Actor]]
  val messages = new ListBuffer[Message]

  def notifyListeners(listeners: HashSet[Actor], messages: List[Message]) = 
    listeners.foreach(_ ! UpdateMessage(messages))

  def getListeners(key: Long) = {
    if (!listeners.contains(key)) {
      listeners + (key -> new HashSet[Actor])
    }
    listeners(key)
  }

  def act = {
    loop {
      react {
        case AddListener(r: Room, l: Actor) =>
          getListeners(r.id).incl(l)
          reply(UpdateMessage(r.messages(10)))
        case RemoveListener(r: Room, l: Actor) =>
          getListeners(r.id).excl(l)
        case AddMessage(r: Room, u: User, text: String) =>
          Member.findByRoomAndUser(r.id, u.id).map({ m =>
            val msg = Message.create.room(r.id).member(m).message(text)                        
            msg.validate match {
              case Nil =>
                msg.save
                println("AddMessage" + msg)
                notifyListeners(getListeners(r.id), List(msg))
              case x =>
                println(x)
            }
          })
//        case AddMessage(user: User, m: String) =>
//          val message = Message.create.owner(user.id)
//          val message = Message.create
//          message.message(m)
//          notifyListeners(List(m))
      }
    }
  }

  start
}
