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

object Helpers {
	def current_user() = true;
}

def callAPI(url: String) = {
	val req = new OAuthRequest(Verb.GET, url)
	TwitterOAuth.service.signRequest(accTkn, req)
	val resp = req.send()
	resp.getBody()
}

var accTkn: Token = _;

get("/") {
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

	val respBody = callAPI("https://api.twitter.com/1.1/account/verify_credentials.json")
	// respBody
	redirect("/")
}

get("/blog") {
	val respBody = callAPI("https://api.twitter.com/1.1/statuses/user_timeline.json")
	var parsedList: Option[Any] = JSON.parseFull(respBody)
	parsedList match  {
		case l: List[Map[String, Any]] => 
			var parsedBody:Map[String, Any] = l.head
			parsedBody.foreach {case(k, v) => if (k == "text") v }
	}
	
	// for( x <- parsedBody) {
	// 	x
	// }
}

}
