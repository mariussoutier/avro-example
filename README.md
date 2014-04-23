# Scalding with Avro

This project shows how to use Avro to model your data and later process it using Scalding.
It also illustrates an error that is occurring as soon as nested Avro structures hit the disk.

## Tweets

Of course this example, too, has to model Twitter tweets. In contrast to most tutorials, we are
modeling Tweets close to the original, as
[documented by Twitter](https://dev.twitter.com/docs/platform-objects/tweets).
This means not only a simple flat model, but nested and nullable properties of various types.

## Jobs

CountTweetsJob - count all tweets per day.

TrendingTagsJob - takes all hashtags, counts them per day, and keeps the top 20 ones.

## Running the Project

Note: This project currently uses Maven to reproduce my client's original environment as close as possible.

Run `mvn clean compile scala:cc` for interactive development (or just import into IntelliJ).
Run `mvn clean compile scala:cctest` to run tests while developing.

To run the job on Hadoop, package everything via `mvn clean package`. The fat JAR will be placed into the
target folder and can be submitted to Hadoop.

~~~bash
hadoop jar target/avro-example-1.0.0-SNAPSHOT.jar com.mariussoutier.avroexample.jobs.<JobName> --input ... --output ... --hdfs
~~~

In time, the project will be moved to sbt.

## Test Data

The project also contains [ScalaCheck](scalacheck.org) generators. Easily create 100 tweets in
`/tmp/tweets` by running `mvn scala:run -DmainClass="com.mariussoutier.avroexample.test.MakeTweets"`.
