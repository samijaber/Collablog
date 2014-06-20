package com.dogebarks.app

import org.scalatra._
import scalate.ScalateSupport

import org.scribe.builder.api.TwitterApi
import org.scribe.builder.ServiceBuilder
import org.scribe.oauth.OAuthService
import org.scribe.model._

import xml.{XML, NodeSeq}

class DogeServlet extends DogebarksStack with ScalateSupport {

object Helpers {
	def current_user() = true;
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
	redirect("/")
}

get("/blog") {
	val req = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/statuses/user_timeline.json")
	TwitterOAuth.service.signRequest(accTkn, req)
	val resp = req.send()
	resp.getBody()
}

}
