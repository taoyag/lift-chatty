package org.chatty.snippet

import net.liftweb.http._
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

  def view(html: NodeSeq) = S.param("id") match {
    case Full(id) =>
      Room.findByKey(id.toLong).map({ r =>
        bind("room", html, 
            "name" -> r.name,
            "memberOnly" -> r.memberOnly)
      }) openOr Text("No room found")
    case _ =>
      Text("No room found")
  }

  def edit(html: NodeSeq) = S.param("id") match {
    case Full(id) =>
      Room.findByKey(id.toLong).map({ r =>
        r.toForm(Full("save"), "/room/")
      }) openOr Text("No room found")
    case _ =>
      Text("No room found")
  }

  /**
   * 新しいチャットルームの入力フィールドを表示し、保存する。
   */
  def create(html: NodeSeq) = {
    val room = new Room
    room.toForm(Full("save"), { _.save })
  }

  private def rooms = Room.findAll()
}

