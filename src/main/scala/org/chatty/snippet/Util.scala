package org.chatty.snippet

import scala.xml.{NodeSeq}
import org.chatty._
import model._

class Util {
  def in(html: NodeSeq) = 
    if (User.loggedIn_?) html else NodeSeq.Empty

  def out(html: NodeSeq) =
    if (!User.loggedIn_?) html else NodeSeq.Empty
}
