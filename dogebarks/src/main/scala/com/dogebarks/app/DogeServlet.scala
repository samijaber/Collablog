package com.dogebarks.app

import org.scalatra._
import scalate.ScalateSupport

//add missing XML & NodeSeq Scala imports

//SCRIBEUUUUUUUU
//PS: Remove unnecessary imports
import org.scribe.builder._
import org.scribe.builder.api._
import org.scribe.exceptions._
import org.scribe.extractors._
import org.scribe.model._
import org.scribe.oauth._
import org.scribe.services._
import org.scribe.utils._

class DogeServlet extends DogebarksStack with ScalateSupport {

object TwitterOAuth {
	def service : OAuthService= new ServiceBuilder()
					.provider(classOf[TwitterApi])
					//provide secrets when launching server
					.apiKey("key here")
					.apiSecret("secret here")
					.build();

	def requestToken(): Token = service.getRequestToken

	def getAuthUrl(requestToken: Token): String = service.getAuthorizationUrl(requestToken)

	def getAccessToken(requestToken: Token, verifierCode: String): Token = {
		val verifier: Verifier = new Verifier(verifierCode);
		service.getAccessToken(requestToken, verifier);
	}

	def request(verb: Verb, url: String, accessToken: Token): String = {
		val request: OAuthRequest = new OAuthRequest(verb, url)
		service.signRequest(accessToken, request)
		val response: Response = request.send
		if (response.getCode == 401) throw new Exception
		response.getBody
	}

	def authenticate(url: String, accessToken: Token): String = {
		request(Verb.GET, url, accessToken)
	}

	def homeTimeline(accessToken: Token): NodeSeq = {
		val url = "//choose proper URL"
	  	val response: String = authenticate(url, accessToken)
	  	val statuses = XML.loadString(response)
	  	statuses \ "status"
	}
}

object Helpers {
	def current_user() = true;
}

get("/") {
	contentType="text/html"
	ssp("/index")
}

get("/auth") {
	//twitter callback redirect
}

}
