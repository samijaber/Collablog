package com.dogebarks.app

import org.scalatra._
import scalate.ScalateSupport
import scala.util.parsing.json._

import org.scribe.builder.api.TwitterApi
import org.scribe.builder.ServiceBuilder
import org.scribe.oauth.OAuthService
import org.scribe.model._

import xml.{XML, NodeSeq}


class DogeServlet extends DogebarksStack with ScalateSupport {
var accTkn: Token = _;

object Helpers {
	def current_user() = {
		try { 
			val respBody = callAPI("https://api.twitter.com/1.1/account/verify_credentials.json")
			true
		} catch {
		  case e: Exception => false
		}
	}
}

def callAPI(url: String) = {
	val req = new OAuthRequest(Verb.GET, url)
	TwitterOAuth.service.signRequest(accTkn, req)
	val resp = req.send()
	resp.getBody()
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
	val respBody = callAPI("https://api.twitter.com/1.1/statuses/user_timeline.json")
	JSON.parseFull(respBody) match  {
		case Some(l:List[Map[String,Any]]) => printTweets(l)
		case _ => "Error occured. JSON not parsed correctly"
	}
}

def printTweets(tweets:List[Map[String, Any]]): List[Any] = {
	tweets match {
		case tweet :: ts => tweet("text") :: printTweets(ts)
		case Nil => Nil
	}
}

}
