package org.chatty.model

import java.util.regex.Pattern._

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.http._

import scala.xml.Text

/**
 * 値が入力されているかを検証する。
 */
trait ValidateRequired extends MixableMappedField {
  self: MappedField[_, _] =>

  def requiredErrorMessage = S.?("Field is required")

  abstract override def validations = 
    validateRequired _ :: super.validations

  def validateRequired(value: Any): List[FieldError] =
    if (value == null || 
        (value.isInstanceOf[String] && value.asInstanceOf[String] == "")) {
      List(FieldError(this, Text(requiredErrorMessage)))
    } else {
      Nil
    }
}

/**
 * 値が一意であるかを検証する。
 */
trait ValidateUnique extends MixableMappedField {
  self: MappedString[_] =>

  def uniqueErrorMessage = S.?("Field must be unique")

  abstract override def validations = 
    valUnique(uniqueErrorMessage) _ ::
    super.validations
}

/**
 * 値が半角英数字であるかを検証する。
 */
trait ValidateAlphanumeric extends MixableMappedField {
  self: MappedString[_] =>

  def alphanumericErrorMessage = S.?("Field must be alphanumeric")

  abstract override def validations = 
    valRegex(compile("""^[a-zA-Z0-9_]*$"""), alphanumericErrorMessage) _ ::
    super.validations
}
