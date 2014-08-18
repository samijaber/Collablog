package com.dogebarks.app

import scala.slick.driver.H2Driver.simple._

object Schema {
	val blogs        = TableQuery[Blogs]
	val contributors = TableQuery[Contributors]
	val tweets       = TableQuery[Tweets]
}