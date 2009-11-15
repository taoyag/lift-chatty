package org.chatty.logic

import org.chatty.model._

import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable.{HashSet, ListBuffer, HashMap}

case class AddListener(user: User, listener: Actor)
case class RemoveListener(user: User, listener: Actor)
case class UpdateMessage(messages: List[Message])
case class AddMessage(user: User, message: String)

object Chatty {
  val chatties = new HashMap[Long, Chatty]
  def chatty(room: Room) = {
    if (!chatties.contains(room.id)) {
      chatties += (room.id.toLong -> new Chatty(room))
    }
    chatties(room.id)
  }
}

class Chatty(room: Room) extends Actor {
  val listeners = new HashMap[Long, HashSet[Actor]]

  def notifyListeners(ls: HashSet[Actor], messages: List[Message]) = 
    ls.foreach(_ ! UpdateMessage(messages))

  def getListeners(key: Long) = {
    if (!listeners.contains(key)) {
      listeners += (key -> new HashSet[Actor])
    }
    listeners(key)
  }

  def act = {
    loop {
      react {
        case AddListener(u: User, l: Actor) =>
          println("AddListener")
          getListeners(room.id).incl(l)
          println("reply")
          reply(UpdateMessage(room.messages(10)))
          this ! AddMessage(u, "%s has joined!".format(u.firstName))
        case RemoveListener(u: User, l: Actor) =>
          getListeners(room.id).excl(l)
          this ! AddMessage(u, "%s has left!".format(u.firstName))
        case AddMessage(u: User, text: String) =>
          println("AddMessage")
          Member.findByRoomAndUser(room.id, u.id).map({ m =>
            println("Member found")
            val msg = Message.create.room(room.id).member(m).message(text)                        
            msg.validate match {
              case Nil =>
                println("validate success")
                msg.save
                println("save success:"+msg)
                notifyListeners(getListeners(room.id), List(msg))
              case x =>
                println(x)
            }
          })
      }
    }
  }

  start
}
