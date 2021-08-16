package org.dedkot

import cloudflow.flink.{ FlinkStreamlet, FlinkStreamletLogic }
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.AvroInlet
import org.apache.flink.streaming.api.scala.createTypeInformation

class GrainEgress extends FlinkStreamlet {
  val in: AvroInlet[Grain] = AvroInlet("grain-in")

  override def shape(): StreamletShape = StreamletShape.withInlets(in)

  override protected def createLogic(): FlinkStreamletLogic = new FlinkStreamletLogic() {
    override def buildExecutionGraph(): Unit = {
      val grain = readStream(in)
        .process(new GrainsCounters)
        .setParallelism(1)
        .setMaxParallelism(1)
        .name("GrainEgress")

      grain.addSink { grain =>
        log.info(s"Get $grain")
      }
    }
  }
}
