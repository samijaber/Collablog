package com.dogebarks.app

import org.scalatra._

trait Users extends ScalatraServlet {
	case class SessionUser(name: String, reader: TweetReader)

	def sessionStorage: SessionUser = {
		try { 
			session("sessionStorage").asInstanceOf[SessionUser]
		} catch {
		  case e: Exception => redirect("/login") 
		}
	}

	def initStorage() = if (!session.keySet.exists(_ == "sessionStorage")) session.put("sessionStorage", None)

	def loggedIn: Boolean = {
		initStorage 
		session("sessionStorage") match {
			case x: SessionUser => if (x.name.isEmpty) false
			case _ => false
		}
		true
	}
}