package com.dogebarks.app

import scala.slick.driver.H2Driver.simple._

class Contributors(tag: Tag) extends Table[(String, String, String)](tag, "CONTRIBUTORS") {
	def userId 		= column[String]("USERID")
	def hashtag 	= column[String]("HASHTAG")
	def addedAt 	= column[String]("ADDEDAT")
	def * 				= (userId, hashtag, addedAt)

	// def story 		= foreignKey("STORY", hashtag, blogs)(_.hashtag)
}