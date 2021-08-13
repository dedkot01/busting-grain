package org.dedkot

import cloudflow.flink.{ FlinkStreamlet, FlinkStreamletLogic }
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.AvroInlet
import org.apache.flink.streaming.api.scala.createTypeInformation

class GoodGrainEgress extends FlinkStreamlet {
  val goodGrainIn: AvroInlet[Grain] = AvroInlet("good-grain-in")

  override def shape(): StreamletShape = StreamletShape.withInlets(goodGrainIn)

  override protected def createLogic(): FlinkStreamletLogic = new FlinkStreamletLogic() {
    override def buildExecutionGraph(): Unit = {
      val goodGrainStream = readStream(goodGrainIn)

      goodGrainStream.addSink { grain =>
        log.info(s"Grain: $grain")
      }
    }
  }
}
