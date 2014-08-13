package com.dogebarks.app

import org.scalatra._
import scalate.ScalateSupport
import scala.collection.JavaConversions._
import scala.util.parsing.json._

import java.text.SimpleDateFormat
import java.util.Date

import twitter4j.{Twitter, TwitterFactory}
import twitter4j.conf._
import twitter4j._

import DogeServlet._
import Schema._
import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import scala.slick.lifted.TableQuery._
import scala.slick.jdbc.meta._

import org.scribe.model.Token

object TweetReader {
	var twitter: Option[Twitter] = None;
	val timeFormat = new SimpleDateFormat()

	def setTwitter(accTkn: Token) = {
		twitter = Some(new TwitterFactory (new ConfigurationBuilder()
			  .setOAuthConsumerKey(Secret.apiKey)
			  .setOAuthConsumerSecret(Secret.apiSecret)
			  .setOAuthAccessToken(accTkn.getToken)
			  .setOAuthAccessTokenSecret(accTkn.getSecret)
			  .build)
		.getInstance)
	}

	def update_tweets(db: Database, hashtag: String, user: String) = {
		if (twitter.isEmpty)
			throw new IllegalStateException("twitter not set")

		db withDynSession {
	    val q1 = for { b <- blogs if b.hashtag === hashtag } yield b.lastId

			val query: twitter4j.Query = new twitter4j.Query("#" + hashtag + " from:" + user);
			var lng: Long = q1.first
			query.setSinceId(0L)

			val result: QueryResult = twitter.get.search(query);
			
	    for (status <- result.getTweets()) {
	    	val date = timeFormat.format(status.getCreatedAt())
	    	val mediaUrl = {
	    		val mediaArr = status.getMediaEntities()
	    		if (mediaArr.isEmpty)
	    			None
	  			else
						Some(mediaArr(0).getMediaURL())
	    	}
	    	try { 
	    		tweets += (status.getId().toString(), status.getText(), status.getUser().getScreenName(), hashtag, date, mediaUrl)
	    	} catch {
	    	  case e: Exception => 
	    	  	// println("tweet already exists")
	    	}
	    }

	    val q2 = for { b <- blogs if b.hashtag === hashtag} yield b.lastId
	    val sinceVal = result.getMaxId().asInstanceOf[java.lang.Long]
	    val k: Long = sinceVal.toLong * 1L
	    q2.update(k)

			val statement = q2.updateStatement
			val invoker = q2.updateInvoker		    

		}
	}
}