package com.dogebarks.app

import scala.slick.driver.H2Driver.simple._

class Tweets(tag: Tag) extends Table[(String, String, String, String, String, Option[String])](tag, "TWEETS") {
	def id 				= column[String]("URL", O.PrimaryKey)
	def text 			= column[String]("TEXT")
	def userId 		= column[String]("USERID")
	def hashtag 	= column[String]("HASHTAG")
	def createdAt = column[String]("CREATEDAT")
	def media			= column[Option[String]]("MEDIA")
	def * 				= (id, text, userId, hashtag, createdAt, media)

	// def writer = ("WRITER", userId, contributors)(_.userId)
}