package com.dogebarks.app

import org.scalatra._
import scalate.ScalateSupport
import scala.util.parsing.json._

import org.scribe.builder.api.TwitterApi
import org.scribe.builder.ServiceBuilder
import org.scribe.oauth.OAuthService
import org.scribe.model._

import Schema._
import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import scala.slick.lifted.TableQuery._
import scala.slick.jdbc.meta._

case class DogeServlet(db: Database) extends DogebarksStack with ScalateSupport {
	val ddls = blogs.ddl ++ contributors.ddl ++ tweets.ddl

	object Helpers {
		def get_username(): Option[String] = {
			try { 
				val respBody = TwitterOAuth.get("https://api.twitter.com/1.1/account/verify_credentials.json", SessionUser.accTkn.get)
				JSON.parseFull(respBody) match  {
					case Some(m:Map[String,Any]) => Some(m("screen_name").toString())
					case _ => None
				}
			} catch {
			  case e: Exception => None
			}
		}

		def printTweets(tweets:List[Map[String, Any]]): List[Any] = {
			tweets match {
				case tweet :: ts => tweet("text") :: printTweets(ts)
				case Nil => Nil
			}
		}

		def auth = if (SessionUser.accTkn.isEmpty) redirect("/login")

		def shutdown = {
			db withDynSession {
				ddls.drop
			}
		}

		def insert(table: TableQuery[_]) = {
			table.insertStatement
			table.insertInvoker
		}
	}
	import Helpers._

	before() {
		contentType="text/html"

		db withDynSession {
			if (MTable.getTables.list().isEmpty)
				ddls.create
		}

		//TODO: Add name to templateAttribute map in DogebarksStack
		if (!SessionUser.name.isEmpty)
			templateAttributes("name") = SessionUser.name.get
		else
			templateAttributes("name") = "default"

	}

	before("/main*") {
		auth
	}

	get("/") {
		auth
		redirect("/main")
	}

	get("/main") {
		ssp("/main", "name" -> SessionUser.name.get)
	}

	get("/main/new_blog"){
		db withDynSession {
			blogs += (params("hashtag"), SessionUser.name.get, params("title"))
			blogs.insertStatement
			blogs.insertInvoker
			
			contributors += (SessionUser.name.get, params("hashtag"))
			contributors.insertStatement
			contributors.insertInvoker
		}

		redirect("/main/blog/" + params("title"))
	}

	get("/main/blog/:id") {
		ssp("/blog", "title" -> params("id"))
	}

	//=====
	//OAuth
	//=====
	object SessionUser {
		var accTkn: Option[Token] = None
		var name: Option[String] = None		
	}

	get("/logout") {
		SessionUser.accTkn = None
		redirect("/main")
	}

	get("/login") {
		ssp("/login")
	}

	get("/auth") {
		val token: Token = TwitterOAuth.requestToken()
	  val authUrl = TwitterOAuth.getAuthUrl(token)
	  redirect(authUrl)
	}

	get("/auth/callback") {
		val requestToken: Token = new Token(params("oauth_token"), Secret.apiSecret)
		SessionUser.accTkn = Some(TwitterOAuth.getAccessToken(requestToken, params("oauth_verifier")))
		SessionUser.name = get_username
		redirect("/")
	}
}