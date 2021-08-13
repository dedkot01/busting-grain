package org.dedkot

import cloudflow.flink.{ FlinkStreamlet, FlinkStreamletLogic }
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.{ AvroInlet, AvroOutlet }
import org.apache.flink.streaming.api.scala.createTypeInformation

class GrainBuster extends FlinkStreamlet {
  val grainIn: AvroInlet[Grain]       = AvroInlet("grain-in")
  val badGrainOut: AvroOutlet[Grain]  = AvroOutlet("bad-grain-out")
  val goodGrainOut: AvroOutlet[Grain] = AvroOutlet("good-grain-out")

  override def shape(): StreamletShape =
    StreamletShape
      .withInlets(grainIn)
      .withOutlets(badGrainOut, goodGrainOut)

  override protected def createLogic(): FlinkStreamletLogic = new FlinkStreamletLogic() {
    override def buildExecutionGraph(): Unit = {
      val grainStream = readStream(grainIn)

      val goodGrainStream = grainStream.filter(!_.isRotten)
      val badGrainStream  = grainStream.filter(_.isRotten)

      writeStream(badGrainOut, badGrainStream)
      writeStream(goodGrainOut, goodGrainStream)
    }
  }
}
