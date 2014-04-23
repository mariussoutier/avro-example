package com.mariussoutier.avroexample.test
import generators._

import java.io.File

import org.apache.avro.specific.SpecificDatumWriter
import org.apache.avro.file.DataFileWriter

import com.twitter.scalding.Args

import com.mariussoutier.avroexample.model._


object MakeTweets extends App {

  val parsedArgs = Args(args)

  val base = new File(parsedArgs.getOrElse("output", System.getProperty("java.io.tmpdir") + "/tweets"))
  base.mkdirs()

  val tweetCount = parsedArgs.optional("tweets").map(_.toInt).getOrElse(100)

  for (_ <- 1 to tweetCount) {
    tweetGen.sample.foreach { tweet =>
      val writer = new SpecificDatumWriter[Tweet](Tweet.SCHEMA$)
      val dataFileWriter = new DataFileWriter(writer)

      dataFileWriter.create(Tweet.SCHEMA$, new File(base, s"Tweet-${tweet.getId}.avro"))
      dataFileWriter.append(tweet)
      dataFileWriter.close()
    }
  }


}
