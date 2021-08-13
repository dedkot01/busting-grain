package org.dedkot

import cloudflow.flink.{ FlinkStreamlet, FlinkStreamletLogic }
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.AvroOutlet
import org.apache.flink.streaming.api.scala.createTypeInformation

class GrainGenerator extends FlinkStreamlet {
  val grainOut: AvroOutlet[Grain] = AvroOutlet("grain-out")

  override def shape(): StreamletShape = StreamletShape.withOutlets(grainOut)

  override protected def createLogic(): FlinkStreamletLogic = new FlinkStreamletLogic() {
    override def buildExecutionGraph(): Unit = {
      val randomGrains = context.env.addSource(new GrainRandomGenerationFunction)

      writeStream(grainOut, randomGrains)
    }
  }
}
