package com.mariussoutier.avroexample.test

import scala.collection.JavaConverters._

import org.scalacheck.Gen._
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._

import com.mariussoutier.avroexample.model._

package object generators {

  val intId = choose[Int](min = 123456, max = Integer.MAX_VALUE - 1)

  val userGen = for {
    id <- intId
    name <- alphaStr
    location <- oneOf("Berlin", "Hamburg", "Munich", "Cologne")
  } yield {
    val user = User.newBuilder()
    user.setId(id)
    user.setIdStr(id.toString)
    user.setName(name)
    user.setLocation(name)
    user.build()
  }

  val coordGen = for {
    longitude <- choose(-180.0, 180.0)
    latitude <- choose(-90.0, 90.0)
  } yield {
    val coords = Coordinates.newBuilder
    // Avro in Scala can be cruel...
    coords.setCoordinates(List(Double.box(longitude), Double.box(latitude)).asJava)
    coords.build()
  }

  val hashtagGen = for {
    // we use a predetermined list so the calculations will actually count something
    text <- oneOf("scala", "java", "python", "ruby")
    start <- choose(1, 120)
  } yield {
    val hashtag = Hashtag.newBuilder
    hashtag.setText(text)
    hashtag.setIndices(List(Int.box(start), Int.box(start + text.length)).asJava)
    hashtag.build()
  }

  val urlGen = for {
    url <- oneOf("http://twitter.com", "http://apple.com", "http://mariussoutier.com")
    start <- choose(1, 50)
  } yield {
    val urlRecord = Url.newBuilder
    urlRecord.setUrl(url)
    urlRecord.setDisplayUrl(url)
    urlRecord.setExpandedUrl(url)
    urlRecord.setIndices(List(Int.box(start), Int.box(start + url.length)).asJava)
    urlRecord.build()
  }

  val userMentionGen = for {
    id <- intId
    name <- alphaStr
    start <- choose(1, 50)
  } yield {
    val mention = UserMention.newBuilder
    mention.setId(id)
    mention.setIdStr(id.toString)
    mention.setIndices(List(Int.box(start), Int.box(start + name.length)).asJava)
    mention.setName(name)
    mention.setScreenName(name.replaceAll(" ", "_").toLowerCase)
    mention.build()
  }

  val entitiesGen = for {
    hashtagCount <- choose(0,3)
    hashtags <- listOfN(hashtagCount, hashtagGen)
    userMentionCount <- choose(0,2)
    userMentions <- listOfN(userMentionCount, userMentionGen)
    urlCount <- choose(0,1)
    urls <- listOfN(urlCount, urlGen)
  } yield {
    val ent = Entities.newBuilder
    ent.setHashtags(hashtags.asJava)
    ent.setMentions(userMentions.asJava)
    ent.setUrls(urls.asJava)
    ent.build()
  }

  val tweetGen = for {
    id <- intId
    text <- arbitrary[String] suchThat (_.length <= 140)
    user <- userGen
    favoriteCount <- choose(0, 100)
    retweetCount <- choose(0, 100)
    lang <- oneOf(oneOf("de", "en", "fr"), Gen.value[String](null))
    coords <- coordGen
    entities <- entitiesGen
  } yield {
    val tweet = Tweet.newBuilder
    tweet.setId(id)
    tweet.setIdStr(id.toString)
    tweet.setText(text)
    tweet.setUser(user)
    tweet.setFavoriteCount(favoriteCount)
    tweet.setRetweetCount(retweetCount)
    tweet.setLang(lang)
    tweet.setCoordinates(coords)
    tweet.setEntities(entities)
    tweet.setCreatedAt(System.currentTimeMillis)
    tweet.build()
  }

}
