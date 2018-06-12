package org.master.graphics.quasar

import java.nio.FloatBuffer

import org.joml.Vector3f
import org.master.graphics._
import org.master.graphics.ui.QuasarColorScale
import org.master.http_client.{QuasarClient, QuasarResultType}

class QuasarResultView {
  var animationTime: Double = 60
  var vao: Option[Vao] = None
  var vbo: Vbo = _
  var positions = Array.empty[VertexElement]
  var times = Array(0.0)
  var data = scala.collection.mutable.Map.empty[Double, Array[VertexElement]]
  var autoPlay = true

  var min = 0.0f
  var max = 0.0f
  var delta = 0.0f

  def updateMinMax(): Unit = {
    min = data.map(_._2.minBy(_.mag)).map(_.mag).min
    max = data.map(_._2.maxBy(_.mag)).map(_.mag).max
    delta = max - min
  }

  def getVertexes(time: Double): Array[Vertex] = {
    val newTime = if (time > times.last) { dtCounter = 0; 0 } else time
    val t = times.sliding(2, 1).find(two => newTime <= two(1)).getOrElse(Array(0.0, 0))
    val (left, right) = (t(0), t(1))
    var a = (newTime - left) / (right - left)

    val (leftV, rightV) = (data(left), data(right))

    leftV.zip(rightV).zip(positions).map { case((l, r), point) =>
      val resultVec = new VertexElement(QuasarColorScale.lerp(l.toVector3f, r.toVector3f, a.toFloat))
      val resultA = (resultVec.mag - min) / delta
      var v = Array(point, new VertexElement(QuasarClient.colorScale.getColor(resultA)).withType(VEType.Color))
      if (QuasarClient.resultTypeSelected == QuasarResultType.Vector) v = v :+ new VertexElement(resultVec.toVector3f.normalize()).withType(VEType.QuasarVector)
      Vertex(v:_*)
    }
  }

  def updateVertexes(): Unit = {
    dtCounter = 0
    if (vao.isEmpty) {
      vao = Some(Vao.createInterleavedWithBuffer(DrawType.Points, BufferDrawType.Dynamic, getVertexes(times.head)))
      vao.foreach { v =>
        vbo = v.vbos.head
        Graphics.shaderPrograms(ShaderProgramType.QuasarResult).addVao(v)
      }
    } else update(0)
  }

  def clear(): Unit = {
    vao match {
      case Some(v) => Graphics.shaderPrograms(ShaderProgramType.QuasarResult).removeVao(v)
      case _ =>
    }
    vao = None
    vbo = null
    positions = Array.empty[VertexElement]
    times = Array(0.0)
    data = scala.collection.mutable.Map.empty[Double, Array[VertexElement]]
    min = 0
    max = 0
    delta = 0
    dtCounter = 0
  }

  var dtCounterLocker = 0.0
  var dtCounter = 0.0
  def update(dt: Double, forced: Boolean = false): Unit = {
    if (QuasarClient.requestStarted && QuasarClient.requestFinished) {
      QuasarClient.requestStarted = false
      QuasarClient.requestFinished = false
      QuasarClient.afterUpdateResultView()
    }
    if (!QuasarClient.requestStarted && vao.nonEmpty) {
      if (forced) { // TODO: remove auto-play
        autoPlay = false
        dtCounter = dt
        QuasarClient.autoPlayCheckbox.foreach(_.setChecked(autoPlay))
      } else if (autoPlay) {
        dtCounter += dt
        QuasarClient.timeValueSlider.foreach(_.setValue(dtCounter.toFloat))
      } else return
      val timeConverted = dtCounter //(dtCounter / animationTime) * 100
      vbo.updateData(Vbo.prepareBuffer(getVertexes(timeConverted)))
    }
  }

}
