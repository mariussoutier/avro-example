package com.mariussoutier.avroexample.jobs

import scala.collection.JavaConverters._

import com.twitter.scalding._
import com.twitter.scalding.avro.PackedAvroSource
import cascading.avro.serialization.AvroSpecificRecordSerialization

import com.mariussoutier.avroexample.model._

/**
 * Counts hashtags per day and outputs them as TSV (created, tag, count)
 */
class TrendingTagsJob(args: Args) extends Job(args) {

  override def ioSerializations = super.ioSerializations :+ classOf[AvroSpecificRecordSerialization[_]]

  val tweets: TypedPipe[Tweet] = TypedPipe.from(PackedAvroSource[Tweet](args("input")))

  tweets
    .filter { tweet: Tweet => tweet.getEntities.getHashtags != null }
    .forceToDisk // -> crash
    .flatMap { tweet: Tweet =>
      tweet.getEntities.getHashtags.asScala.map { hashtag =>
        (hashtag.getText.toString, isoDateFormat.print(tweet.getCreatedAt))
      }
    }
    .groupBy ( all => all )
    .size
    // TODO Take top 20
    .toTypedPipe
    .map { case ((created, tag), count) => (created, tag, count) } // flatten
    .write(TypedTsv[(String, String, Long)](args("output")))

}

object TrendingTagsJob extends JobCompanion {
  runJob { args => new TrendingTagsJob(args) }
}
