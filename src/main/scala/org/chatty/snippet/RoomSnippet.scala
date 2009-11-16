package org.chatty.snippet

import net.liftweb.http._
import net.liftweb.http.SHtml._
import net.liftweb.util._
import net.liftweb.util.Helpers._

import org.chatty.model._

import scala.xml.{NodeSeq, Text}

class RoomSnippet {

  /**
   * チャットルームの一覧を返す。
   */
  def list(html: NodeSeq) = {
    rooms.flatMap(r =>
      bind("room", html,
          "name" -> r.name,
          "memberOnly" -> r.memberOnly,
          FuncAttrBindParam("view_href", _ =>
            Text("view/" + (r.primaryKeyField)), "href"),
          FuncAttrBindParam("edit_href", _ =>
            Text("edit/" + (r.primaryKeyField)), "href"),
          FuncAttrBindParam("delete_href", _ =>
            Text("delete/" + (r.primaryKeyField)), "href")
      )
    )
  }

  def join(html: NodeSeq) = S.param("id") match {
    case Full(id) =>
      Room.findByKey(id.toLong).map({ r =>
        def joinRoom(): Unit = {
          Member.join(r, User.currentUser.open_!)
          S.notice("Joined")
        }
        
        bind("room", html,
            "submit" -> 
              {if (!isMember(r)) submit("Join this room", joinRoom) 
              else NodeSeq.Empty})
      }) openOr S.error("No room found")
    case _ =>
      S.error("No room found")
  }

  def isMember(r: Room) = {
    Member.findByRoomAndUser(r.id, User.currentUser.open_!.id) match {
      case Full(m) => true
      case _ => false
    }
  }

  def view(html: NodeSeq) = S.param("id") match {
    case Full(id) =>
      Room.findByKey(id.toLong).map({ r =>
        def chatting = {
          FuncAttrBindParam(
            "chat_href", _ =>
            if (isMember(r)) Text("/chat/" + (r.primaryKeyField)) else Text(""), 
            "href")
        }

        def chattingStyle = {
          FuncAttrBindParam(
            "chat_style", _ =>
            if (isMember(r)) Text("display:inline") else Text("display:none"),
            "style")
        }

        bind("room", html, 
            chatting,
            chattingStyle,
            "name" -> r.name,
            "description" -> r.description,
            "memberOnly" -> r.memberOnly)
      }) openOr Text("No room found")
    case _ =>
      Text("No room found")
  }

  def edit(html: NodeSeq) = S.param("id") match {
    case Full(id) =>
      Room.findByKey(id.toLong).map({ r =>
        def save(): Unit = User.currentUser match {
          case Full(u) =>
            doSave(u, r, false)
          case _ =>
            S.error("invalid user")
        }

        bind("room", html,
            "name" -> r.name.toForm,
            "description" -> r.description.toForm,
            "memberOnly" -> r.memberOnly.toForm,
            "submit" -> submit("Save", save))
      }) openOr Text("No room found")
    case _ =>
      Text("No room found")
  }

  /**
   * 新しいチャットルームの入力フィールドを表示し、保存する。
   */
  def create(html: NodeSeq) = {
    val room = Room.create

    def save(): Unit = User.currentUser match {
      case Full(u) =>
        doSave(u, room, true)
      case _ =>
        S.error("invalid user")
    }

    bind("room", html,
      "name" -> room.name.toForm,
      "description" -> room.description.toForm,
      "memberOnly" -> room.memberOnly.toForm,
      "submit" -> submit("Save", save))
  }

  private def doSave(user: User, room: Room, create: Boolean) = {
    room.validate match {
      case Nil =>
        room.save
        if (create) Member.join(room, user).owner(true).save
        S.redirectTo("/room/")
      case e =>
        S.error(e)
    }
  }

  private def rooms = User.currentUser match {
    case Full(u) =>
      Room.findByUser(u.id)
    case _ =>
       Nil
  }
}

