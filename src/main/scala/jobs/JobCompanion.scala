package com.mariussoutier.avroexample.jobs

import com.twitter.scalding.{Args, Job, Tool}
import org.apache.hadoop.conf.Configuration

trait JobCompanion extends App {
  def runJob(f: Args => Job): Unit = {
    val tool = new Tool
    tool.setJobConstructor(f)
    tool.setConf(new Configuration)

    val status = tool.run(args)
    System.exit(status)
  }
}
