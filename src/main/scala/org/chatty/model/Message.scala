package org.chatty.model

import net.liftweb.mapper._
import net.liftweb.http._
import net.liftweb.util._

class Message extends LongKeyedMapper[Message] with IdPK {
  def getSingleton = Message

  object owner extends MappedLongForeignKey(this, User)
  object message extends MappedPoliteString(this, 1024)
}

object Message extends Message with LongKeyedMetaMapper[Message]
