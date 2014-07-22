package com.dogebarks.app

import scala.slick.driver.H2Driver.simple._

object Schema {
	class Blogs(tag: Tag) extends Table[(String, String, String, String)](tag, "BLOGS") {
		def hashtag 	= column[String]("HASHTAG", O.PrimaryKey)
		def owner 		= column[String]("OWNER")
		def title			= column[String]("TITLE")
		def createdAt = column[String]("CREATEDAT")
		def * 				= (hashtag, owner, title, createdAt)
	}
	val blogs = TableQuery[Blogs]


	class Contributors(tag: Tag) extends Table[(String, String, String)](tag, "CONTRIBUTORS") {
		def userId 		= column[String]("USERID")
		def hashtag 	= column[String]("HASHTAG")
		def addedAt 	= column[String]("ADDEDAT")
		def * 				= (userId, hashtag, addedAt)

		// def story 		= foreignKey("STORY", hashtag, blogs)(_.hashtag)
	}
	val contributors = TableQuery[Contributors]


	class Tweets(tag: Tag) extends Table[(String, String, String, String)](tag, "TWEETS") {
		def url 			= column[String]("URL", O.PrimaryKey)
		def text 			= column[String]("TEXT")
		def userId 		= column[String]("USERID")
		def createdAt = column[String]("CREATEDAT")
		def * 				= (url, text, userId, createdAt)

		// def writer = ("WRITER", userId, contributors)(_.userId)
	}
	val tweets = TableQuery[Tweets]
}