package com.dogebarks.app

import org.scalatra._
import scalate.ScalateSupport

class DogeServlet extends DogebarksStack with ScalateSupport {

get("/") {
	contentType="text/html"
	ssp("/index")
	// layoutTemplate("/WEB-INF/templates/views/index.ssp")
}

get("/auth") {
	"twitter auth to do here"
	//twitter callback redirect
}

object Helpers {
	def current_user() = true;
}
  
}
