package org.dedkot

import cloudflow.flink.{ FlinkStreamlet, FlinkStreamletLogic }
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.AvroInlet
import org.apache.flink.streaming.api.scala.createTypeInformation

class BadGrainEgress extends FlinkStreamlet {
  val badGrainIn: AvroInlet[Grain] = AvroInlet("bad-grain-in")

  override def shape(): StreamletShape = StreamletShape.withInlets(badGrainIn)

  override protected def createLogic(): FlinkStreamletLogic = new FlinkStreamletLogic() {
    override def buildExecutionGraph(): Unit = {
      val badGrainStream = readStream(badGrainIn)

      badGrainStream.addSink { grain =>
        log.info(s"Grain: $grain")
      }
    }
  }
}
