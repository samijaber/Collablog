package com.dogebarks.app

import org.scalatra._
import scalate.ScalateSupport
import scala.util.parsing.json._
import org.scribe.model.Token
import DogeServlet._

trait AuthRoutes extends ScalatraServlet with ScalateSupport {
	case class SessionUser(name: String, reader: TweetReader)

	def sessionStorage(): SessionUser = {
		try { 
			session("sessionStorage").asInstanceOf[SessionUser]
		} catch {
		  case e: Exception => redirect("/login") 
		}
	}

	def initStorage = if (!session.keySet.exists(_ == "sessionStorage")) session.put("sessionStorage", None)

	def auth = {
		initStorage 
		session("sessionStorage") match {
			case None => redirect("/login")
			case Some(x: SessionUser) => if (x.name.isEmpty) redirect("/login")
		}
	}

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

	get("/logout") {
		val sess = sessionStorage()
		session.put("sessionStorage", None)
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
		val requestToken: Token = new Token(params("oauth_token"), sys.env("API_SECRET"))
		val accToken: Token = TwitterOAuth.getAccessToken(requestToken, params("oauth_verifier"))
		val name: Option[String] = get_username(accToken)
		if (name.isEmpty)
			redirect("/logout")
			
		val sessionx = SessionUser(name.get, new TweetReader(accToken))
		session.put("sessionStorage", sessionx) 
		redirect("/")
	}
}