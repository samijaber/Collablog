package com.dogebarks.app

import scala.slick.driver.H2Driver.simple._
import java.sql._

object Schema {
	class Blogs(tag: Tag) extends Table[(String, String, String)](tag, "BLOGS") {
		def hashtag = column[String]("HASHTAG", O.PrimaryKey)
		def owner 	= column[String]("OWNER")
		def title		= column[String]("TITLE")
		def * 			= (hashtag, owner, title)
	}
	val blogs = TableQuery[Blogs]


	class Contributors(tag: Tag) extends Table[(String, String)](tag, "CONTRIBUTORS") {
		def userId 		= column[String]("USERID")
		def hashtag 	= column[String]("HASHTAG")
		def * 				= (userId, hashtag)

		// def story 		= foreignKey("STORY", hashtag, blogs)(_.hashtag)
	}
	val contributors = TableQuery[Contributors]


	class Tweets(tag: Tag) extends Table[(String, String, String, Timestamp)](tag, "TWEETS") {
		def url 			= column[String]("URL", O.PrimaryKey)
		def text 			= column[String]("TEXT")
		def userId 		= column[String]("USERID")
		def createdAt = column[Timestamp]("CREATEDAT")
		def * 				= (url, text, userId, createdAt)

		// def writer = ("WRITER", userId, contributors)(_.userId)
	}
	val tweets = TableQuery[Tweets]
}