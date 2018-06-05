package org.master.http_client

import org.joml.Vector3f

import scalaj.http.{Http, HttpResponse}
import org.json4s.{DefaultFormats, Extraction, JObject}
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._
import org.liquidengine.legui.component.{CheckBox, Slider}
import org.master.graphics.quasar.QuasarResultView
import org.master.graphics.ui.QuasarColorScale
import org.master.graphics.{VEType, VertexElement}

case class Point(x: Float, y: Float, z: Float)

object QuasarResultType extends Enumeration {
  type QuasarResultType = Value
  val None: QuasarResultType.Value = Value(-1, "None")
  val Scalar: QuasarResultType.Value = Value(0, "Scalar")
  val Vector: QuasarResultType.Value = Value(1, "Vector")
}

class QuasarClient() {
  implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats

  var pointDensity: Int = 7
  var timeDensity: Int = 20
  var resultNameSelected = "None"
  var resultTypeSelected: QuasarResultType.Value = QuasarResultType.None

  val url = "http://localhost:5000/api/quasar"
  val loadUrl = s"$url/load"
  val scalarUrl = s"$url/scalar"
  val vectorUrl = s"$url/vector"

  var resultView: QuasarResultView = new QuasarResultView
  var colorScale = QuasarColorScale.classic()
  var timeValueSlider: Option[Slider] = None
  var autoPlayCheckbox: Option[CheckBox] = None

  def load(projectPath: String, solutionPath: String): Int = {
    val json = compact(render(("ProjectPath" -> projectPath) ~ ("SolPath" -> solutionPath)))
    postData(s"${QuasarClient.loadUrl}", json).code
  }

  def getScalar() = Http(s"${QuasarClient.scalarUrl}").timeout(10000, 50000).asString.body
  def getVector() = Http(s"${QuasarClient.vectorUrl}").timeout(10000, 50000).asString.body

  def postPoints(points: Array[Vector3f], time: Double): (Seq[VertexElement], Boolean) = {
    if (resultTypeSelected == QuasarResultType.Scalar) {
      return postScalar(points, time) match {
        case (body, true) => (parse(body).extract[List[Double]].map(v => VertexElement(v.toFloat).withType(VEType.QuasarScalar)), true)
        case _ => (Seq.empty[VertexElement], false)
      }
    } else if (resultTypeSelected == QuasarResultType.Vector) {
      return postVector(points, time) match {
        case (body, true) => (parse(body).extract[List[Point]].map(v => VertexElement(v.x, v.y, v.z).withType(VEType.QuasarVector)), true)
        case _ => (Seq.empty[VertexElement], false)
      }
    }
    (Seq.empty[VertexElement], false)
  }

  def postScalar(points: Array[Vector3f], time: Double): (String, Boolean) = postVectorScalar(points, time, resultNameSelected, scalarUrl)
  def postVector(points: Array[Vector3f], time: Double): (String, Boolean) = postVectorScalar(points, time, resultNameSelected, vectorUrl)

  private def postVectorScalar(points: Array[Vector3f], time: Double, resultName: String, url: String): (String, Boolean) = {
    val json = compact(render(
      ("Points" -> Extraction.decompose(points.map(p => Point(p.x, p.y, p.z)))) ~
        ("t" -> time) ~
        ("ResultName" -> resultName)
    ))
    val answer = postData(s"$url", json)
    if (answer.code == 200) {
      println(s"ok for request with body: ${answer.body}")
      (answer.body, true)
    } else {
      println(s"error for request with code: ${answer.code}")
      ("", false)
    }
  }

  def postData(url: String, json: String): HttpResponse[String] = {
    val t = Http(url)
    t.postData(json).header("content-type", "application/json").timeout(10000, 50000).asString
  }

  var scalars = List.empty[String]
  var vectors = List.empty[String]

  def init(): Unit = {
    println(load("panda", "sol"))
    scalars = parse(getScalar()).extract[List[String]]
    vectors = parse(getVector()).extract[List[String]]
  }

  def updateResultView(points: Array[Vector3f], timeCounts: Int): Unit = {
    val (startT, endT) = (0, 100)
    val step = (endT - startT) / timeCounts.toDouble
    resultView.positions = points.map(new VertexElement(_).withType(VEType.Position))
    resultView.data = scala.collection.mutable.Map.empty[Double, Array[VertexElement]]
    resultView.times = Array.empty
    for (i <- 0 to timeCounts) {
      val t = startT + step * i
      val answer = postPoints(points, t)
      if (!answer._2) {
        resultView.clear()
        return
      }
      resultView.times :+= t
      resultView.data(t) = answer._1.toArray
    }
    resultView.updateMinMax()
    resultView.updateVertexes()
  }
}

object QuasarClient extends QuasarClient() {

}
