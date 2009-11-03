package org.chatty.comet

import net.liftweb._

import http._
//import CometActor
//import SHtml
import js.JsCmds.SetHtml

import util._
import Helpers.TimeSpan
import TimeHelpers._

import scala.xml.Text

import java.util.Date

class Clock extends CometActor {
  override def defaultPrefix = Full("clock")

  def render = bind("time" -> timeSpan)

  def timeSpan = (<span id="time">{timeNow}</span>)

  ActorPing.schedule(this, Tick, TimeSpan(10000L))

  override def lowPriority : PartialFunction[Any, Unit] = {
    case Tick => {
      println("Got tick " + new Date());
      partialUpdate(SetHtml("time", Text(timeNow.toString)))

      ActorPing.schedule(this, Tick, TimeSpan(10000L))
    }
  }
}
case object Tick
