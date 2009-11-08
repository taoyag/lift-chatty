package org.chatty.snippet

import org.junit.runner.RunWith

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

import net.liftweb._
import http._
import net.liftweb.util._
import Helpers._
import lib._

@RunWith(classOf[JUnitRunner])
class HelloWorldTestSpecs extends FlatSpec with ShouldMatchers {
  val session = new LiftSession("", randomString(20), Empty)
  val stableTime = now

  override def withFixture(test: NoArgTest) {
    try {
      S.initIfUninitted(session) {
        DependencyFactory.time.doWith(stableTime) {
          test()
        }
      }
    } finally {
    }
  }

  "HelloWorld Snippet" should "Put the time in the node" in {
    val hello = new HelloWorld
    Thread.sleep(1000) // make sure the time changes

    val str = hello.howdy(<span>Hello at <b:time/></span>).toString

    str.indexOf(stableTime.toString) should be >= 0
    str.indexOf("Hello at") should be >= 0
  }
}