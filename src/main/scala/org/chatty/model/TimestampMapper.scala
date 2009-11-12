package org.chatty.model

import java.util.Date

import net.liftweb.mapper._
import net.liftweb.util._

trait TimestampMapper[A <: TimestampMapper[A]] extends Mapper[A] {
  self: A =>

  object createdAt extends MappedDateTime(this)
  object updatedAt extends MappedDateTime(this)
}

trait TimestampMetaMapper[A <: TimestampMapper[A]] extends MetaMapper[A] {
  self: A =>

  val fillCreatedAt = (a: A) => { a.createdAt(new Date); () }
  val fillUpdatedAt = (a: A) => { a.updatedAt(new Date); () }
  val doBeforeCreate = List(fillCreatedAt)
  val doBeforeUpdate = List(fillUpdatedAt)

  override def beforeCreate = doBeforeCreate
  override def beforeSave = doBeforeUpdate
}
