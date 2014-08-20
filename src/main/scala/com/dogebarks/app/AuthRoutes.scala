package com.dogebarks.app

import org.scalatra._
import scalate.ScalateSupport
import scala.util.parsing.json._
import org.scribe.model.Token
import DogeServlet._

trait AuthRoutes extends ScalatraServlet with ScalateSupport with Users {
	def get_username(accToken: Token): Option[String] = {
		try { 
			val respBody = TwitterOAuth.get("https://api.twitter.com/1.1/account/verify_credentials.json", accToken)
			JSON.parseFull(respBody) match  {
				case Some(m:Map[String,Any]) => Some(m("screen_name").toString())
				case _ => None
			}
		} catch {
		  case e: Exception => None
		}
	}

	get("/auth/logout") {
		session.put("sessionStorage", None)
		redirect("/")
	}

	get("/auth/login") {
		ssp("/login")
	}

	get("/auth") {
		val token: Token = TwitterOAuth.requestToken()
	  val authUrl = TwitterOAuth.getAuthUrl(token)
	  redirect(authUrl)
	}

	get("/auth/callback") {
		val requestToken: Token = new Token(params("oauth_token"), sys.env("API_SECRET"))
		val accToken: Token = TwitterOAuth.getAccessToken(requestToken, params("oauth_verifier"))
		val name: Option[String] = get_username(accToken)
		if (name.isEmpty)
			redirect("auth/logout")
			
		val sessionx = SessionUser(name.get, new TweetReader(accToken))
		session.put("sessionStorage", sessionx) 
		redirect("/")
	}

	get(!loggedIn) {
		redirect("/auth/login")
	}
}