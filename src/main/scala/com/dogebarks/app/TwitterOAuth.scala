package com.dogebarks.app

import org.scribe.builder.api.TwitterApi
import org.scribe.builder.ServiceBuilder
import org.scribe.oauth.OAuthService
import org.scribe.model._

class UnauthorizedException extends RuntimeException

object TwitterOAuth {
	def service: OAuthService= new ServiceBuilder()
					.provider(classOf[TwitterApi.SSL])
					//provide secrets when launching server
					.apiKey(Secret.apiKey)
					.apiSecret(Secret.apiSecret)
					.callback("http://0.0.0.0:8080/auth/callback")
					.build

	def requestToken(): Token = service.getRequestToken

	def getAuthUrl(requestToken: Token): String = service.getAuthorizationUrl(requestToken)

	def getAccessToken(requestToken: Token, verifierCode: String): Token = {
		val verifier: Verifier = new Verifier(verifierCode)
		service.getAccessToken(requestToken, verifier)
	}

	def request(verb: Verb, url: String, accessToken: Token): String = {
		val request: OAuthRequest = new OAuthRequest(verb, url)
		service.signRequest(accessToken, request)
		val response: Response = request.send
		if (response.getCode == 401) throw new UnauthorizedException
		response.getBody
	}

	def get(url: String, accessToken: Token): String = {
		request(Verb.GET, url, accessToken)
	}
}