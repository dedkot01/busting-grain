package org.dedkot

import org.apache.flink.streaming.api.functions.source.SourceFunction

import scala.util.Random

class GrainRandomGenerationFunction extends SourceFunction[Grain] {
  private val random = new Random()

  private def createRandomGrain: Grain = new Grain(isRotten = random.nextBoolean())

  override def run(ctx: SourceFunction.SourceContext[Grain]): Unit = {
    while(true) {
      Thread.sleep(5000)
      val grain = createRandomGrain

      ctx.collect(grain)
    }
  }

  override def cancel(): Unit = ()
}
