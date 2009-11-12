package org.chatty.model

import net.liftweb.http.{S, LiftSession}
import net.liftweb.mapper._
import net.liftweb.util._

import org.junit.runner.RunWith

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

object CleanUpper {
  val cleanRoom = 
    "delete from %s where %s = ?".format(
        Room.dbTableName,
        Room.name.dbColumnName)

  val cleanMessages = 
    "delete from %s where %s = ?".format(
        Message.dbTableName,
        Message.room.dbColumnName)

  val cleanMembers = 
    "delete from %s where %s = ?".format(
        Member.dbTableName,
        Member.room.dbColumnName)

  def cleanRoom(name: String) {
    Room.findByName(name) match {
      case Full(r) =>
        DB.use(DefaultConnectionIdentifier) { conn =>
          cleanMessages(conn, r)
          cleanMembers(conn, r)
          DB.prepareStatement(cleanRoom, conn) { stmt =>
            stmt.setString(1, name)
            stmt.execute()
          }
        }
      case _ => println("Room %s was not found. Nothing to do.".format(name))
    }
  }

  def cleanMessages(conn: SuperConnection, room: Room) {
    DB.prepareStatement(cleanMessages, conn) { stmt =>
      stmt.setLong(1, room.id.is)
      stmt.execute()
    }
  }

  def cleanMembers(conn: SuperConnection, room: Room) {
    DB.prepareStatement(cleanMembers, conn) { stmt =>
      stmt.setLong(1, room.id.is)
      stmt.execute()
    }
  }
}

