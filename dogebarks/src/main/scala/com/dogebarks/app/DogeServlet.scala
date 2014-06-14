package com.dogebarks.app

import org.scalatra._
import scalate.ScalateSupport

class DogeServlet extends DogebarksStack with ScalateSupport {

  get("/") {
  	contentType="text/html"

  	layoutTemplate("/WEB-INF/templates/views/index.ssp")
  }


object Helpers {
  def current_user() = true;
}
  
}
