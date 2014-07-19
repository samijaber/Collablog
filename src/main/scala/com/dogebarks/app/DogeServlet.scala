package com.dogebarks.app

import org.scalatra._
import scalate.ScalateSupport
import scala.util.parsing.json._

import org.scribe.builder.api.TwitterApi
import org.scribe.builder.ServiceBuilder
import org.scribe.oauth.OAuthService
import org.scribe.model._

import xml.{XML, NodeSeq}

import scala.slick.jdbc.JdbcBackend.Database

class DogeServlet(db: Database) extends DogebarksStack with ScalateSupport with SlickRoutes {
var accTkn: Token = _;

object Helpers {
	def current_user() = {
		try { 
			val respBody = TwitterOAuth.get("https://api.twitter.com/1.1/account/verify_credentials.json", accTkn)
			true
		} catch {
		  case e: Exception => false
		}
	}
}

//TODO: Add login page
// before("/*") {
// 	if (Helpers.current_user()) {
// 		//go to login page
// 	}
// }

get("/") {
	if (Helpers.current_user()) {
		println ("ok")
	}
	else {
		println ("no")
	}
	contentType="text/html"
	ssp("/index")
}

get("/auth") {
	val token: Token = TwitterOAuth.requestToken()
  val authUrl = TwitterOAuth.getAuthUrl(token)
  redirect(authUrl)
}

get("/auth/callback") {
	val requestToken: Token = new Token(params("oauth_token"), Secret.apiSecret)
	accTkn = TwitterOAuth.getAccessToken(requestToken, params("oauth_verifier"))
	redirect("/")
}

get("/blog") {
	contentType="text/html"	
	val respBody = TwitterOAuth.get("https://api.twitter.com/1.1/statuses/user_timeline.json", accTkn)
	JSON.parseFull(respBody) match  {
		case Some(l:List[Map[String,Any]]) => ssp("/blog", "tweets" -> printTweets(l))
		case _ => ssp("/blog", "tweets" -> "Error occured. JSON not parsed correctly")
	}
}

get("/home") {
	homeTimeline(accTkn)
}

def printTweets(tweets:List[Map[String, Any]]): List[Any] = {
	tweets match {
		case tweet :: ts => tweet("text") :: printTweets(ts)
		case Nil => Nil
	}
}

def homeTimeline(accessToken: Token): List[Map[String,Any]]  = {
    val response: String = TwitterOAuth.get("https://api.twitter.com/1.1/statuses/home_timeline.json", accessToken)
    val statusesO = JSON.parseFull(response)
    statusesO.get.asInstanceOf[List[Map[String,Any]]]
}

}
