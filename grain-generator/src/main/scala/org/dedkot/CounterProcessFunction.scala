package org.dedkot

import com.codahale.metrics
import com.codahale.metrics.{Histogram, SlidingWindowReservoir}
import org.apache.flink.api.scala.metrics.ScalaGauge
import org.apache.flink.configuration.Configuration
import org.apache.flink.dropwizard.metrics.{DropwizardHistogramWrapper, DropwizardMeterWrapper}
import org.apache.flink.metrics.{Counter, Meter}
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector

class CounterProcessFunction extends ProcessFunction[Grain, Grain] {
  @transient private lazy val counter: Counter = getRuntimeContext.getMetricGroup
    .addGroup("counter")
    .counter("grain")

  @transient private var totalWeightMetric: Double = 0.0

  @transient private lazy val dropwizardMeterWrapper = new metrics.Meter()
  @transient private lazy val meter: Meter =
    getRuntimeContext.getMetricGroup
      .addGroup("meter")
      .meter("grainPerSecond", new DropwizardMeterWrapper(dropwizardMeterWrapper))

  @transient private lazy val dropwizardHistogramWrapper = new Histogram(new SlidingWindowReservoir(500))
  @transient private lazy val histogram = getRuntimeContext.getMetricGroup.addGroup("histogram")
    .histogram("wtf", new DropwizardHistogramWrapper(dropwizardHistogramWrapper))

  override def open(parameters: Configuration): Unit = {
    getRuntimeContext.getMetricGroup
      .addGroup("gauge")
      .gauge[Double, ScalaGauge[Double]]("totalWeight", ScalaGauge[Double](() => totalWeightMetric))
  }

  override def processElement(value: Grain, ctx: ProcessFunction[Grain, Grain]#Context, out: Collector[Grain]): Unit = {
    counter.inc()
    totalWeightMetric += value.weight
    histogram.update((value.weight * 10).toLong)
    meter.markEvent(1)

    println(s"total weight = $totalWeightMetric, meter = ${meter.getRate}")
    out.collect(value)
  }
}
