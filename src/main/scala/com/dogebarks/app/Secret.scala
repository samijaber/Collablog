package com.dogebarks.app

object Secret {
	lazy val apiKey = sys.env("API_KEY")
	lazy val apiSecret = sys.env("API_SECRET")
	// lazy val authCallback = System.getenv("callbackURL")
}