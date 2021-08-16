package org.dedkot

import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector

class GrainsCounters extends ProcessFunction[Grain, Grain] {
  @transient private lazy val goodGrainCounter =
    getRuntimeContext.getMetricGroup.addGroup("counter").counter("goodGrains")

  @transient private lazy val badGrainCounter =
    getRuntimeContext.getMetricGroup.addGroup("counter").counter("badGrains")

  override def processElement(value: Grain, ctx: ProcessFunction[Grain, Grain]#Context, out: Collector[Grain]): Unit = {
    if (value.isRotten) badGrainCounter.inc() else goodGrainCounter.inc()

    out.collect(value)
  }
}
