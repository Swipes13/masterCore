package org.master.http_client
import org.joml.Vector3f

import scalaj.http.{Http, HttpResponse}
import org.json4s.{DefaultFormats, Extraction, JObject}
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._

case class Point(x: Float, y: Float, z: Float)

class QuasarClient() {
  implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats

  val url = "http://localhost:5000/api/quasar"
  val loadUrl = s"$url/load"
  val scalarUrl = s"$url/scalar"
  val vectorUrl = s"$url/vector"

  def load(projectPath: String, solutionPath: String): Int = {
    val json = compact(render(("ProjectPath" -> projectPath) ~ ("SolPath" -> solutionPath)))
    postData(s"${QuasarClient.loadUrl}", json).code
  }

  def getScalar() = Http(s"${QuasarClient.scalarUrl}").timeout(10000, 50000).asString.body
  def getVector() = Http(s"${QuasarClient.vectorUrl}").timeout(10000, 50000).asString.body

  def postScalar(points: Array[Vector3f], time: Float, resultName: String) = postVectorScalar(points, time, resultName, scalarUrl)
  def postVector(points: Array[Vector3f], time: Float, resultName: String) = postVectorScalar(points, time, resultName, vectorUrl)

  private def postVectorScalar(points: Array[Vector3f], time: Float, resultName: String, url: String) = {
    val json = compact(render(
      ("Points" -> Extraction.decompose(points.map(p => Point(p.x, p.y, p.z)))) ~
        ("t" -> time) ~
        ("ResultName" -> resultName)
    ))
    postData(s"$url", json)
  }

  def postData(url: String, json: String): HttpResponse[String] = {
    val t = Http(url)
    t.postData(json).header("content-type", "application/json").timeout(10000, 50000).asString
  }

  var scalars = List.empty[String]
  var vectors = List.empty[String]

  def init() = {
    //  println(load("panda", "sol"))
    scalars = parse(getScalar()).extract[List[String]]
    vectors = parse(getVector()).extract[List[String]]
  }
}

object QuasarClient extends QuasarClient() {

}
