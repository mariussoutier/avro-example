package com.mariussoutier.avroexample.jobs

import com.twitter.scalding._
import com.twitter.scalding.avro.PackedAvroSource
import cascading.avro.serialization.AvroSpecificRecordSerialization

import com.mariussoutier.avroexample.model._

class CountTweetsJob(args: Args) extends Job(args) {

  override def ioSerializations = super.ioSerializations :+ classOf[AvroSpecificRecordSerialization[_]]

  val tweets: TypedPipe[Tweet] = TypedPipe.from(PackedAvroSource[Tweet](args("input")))

  tweets
    .map { tweet: Tweet => isoDateFormat.print(tweet.getCreatedAt) }
    .groupBy ( all => all )
    .size
    .toTypedPipe
    .write(TypedTsv[(String, Long)](args("output")))

}

object CountTweetsJob extends JobCompanion {
  runJob { args => new CountTweetsJob(args) }
}
