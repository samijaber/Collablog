package com.dogebarks.app

import org.scalatra._
import scalate.ScalateSupport
import scala.collection.JavaConversions._

import java.text.SimpleDateFormat
import java.util.Date

import Schema._
import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import scala.slick.lifted.TableQuery._
import scala.slick.jdbc.meta._

case class DogeServlet(db: Database) extends DogebarksStack with ScalateSupport with AuthRoutes {
	val timeFormat   = new SimpleDateFormat()

	def add_contributor(hashtag: String, user: String) = {
		db withDynSession {
			val time = timeFormat.format(new Date())
			contributors += (user, hashtag, time)
		}
	}

	before() {
		contentType="text/html"

		db withDynSession {
			if (MTable.getTables.list().isEmpty)
				(blogs.ddl ++ contributors.ddl ++ tweets.ddl).create
		}
	}

	get("/") {
		redirect("/user/" + sessionStorage.name)
	}

	get("/user/:name") {
		db withDynSession {
			val q = for {
				b <- blogs if b.owner === params("name")
			} yield b

			ssp("/main", "name" -> params("name"), "blogs" -> q.list())
		}
	}

	get("/user/:name/new_blog") {
		val time = timeFormat.format(new Date())
		db withDynSession {
			blogs += (params("hashtag"), sessionStorage.name, params("title"), time, 0L)
		}

		add_contributor(params("hashtag"), sessionStorage.name)
		redirect(sessionStorage.name + "/blog/" + params("hashtag"))
	}

	get("/blog/:id/delete_blog") {
		// delete_blog(params("title"))
	}

	get("/blog/:id") {
		db withDynSession {
			//get last blog update time
			val q1 = for {
				b <- blogs if b.hashtag === params("id")
			} yield b
			val q1Arr = q1.list()

			//get all users 
			val q2 = for {
				u <- contributors if u.hashtag === params("id")
			} yield u
			for (users <- q2.list()) {
				sessionStorage.reader.update_tweets(db, params("id"), users._1)
			}

			//Retrieve saved tweets with this hashtag ranked by date
			val q3 = for {
				t <- tweets if t.hashtag === params("id")
			} yield t

			ssp("/blog", "title" -> params("id"), "tweets" -> q3.list())
		}
	}

	get("/blog/:title/new_contributor") {
		val time = timeFormat.format(new Date())
		add_contributor(params("hashtag"), params("user"))
		redirect("/blog/" + params("hashtag"))
	}
}