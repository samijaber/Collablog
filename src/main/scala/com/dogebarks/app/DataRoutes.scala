package com.dogebarks.app

import org.scalatra._
import scalate.ScalateSupport

import scala.slick.jdbc.JdbcBackend.Database
import Schema._
import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import scala.slick.lifted.TableQuery._

trait DataRoutes extends ScalatraServlet {

	val db: Database
	val ddls = blogs.ddl ++ contributors.ddl ++ tweets.ddl

	get("/db/create-tables") {
		db withDynSession {
			ddls.create
		}
	}

	get("/db/drop-tables") {
		db withDynSession {
			ddls.drop
		}
	}

}