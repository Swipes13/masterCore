package org.master.graphics

case class VertexElement(values: Float*) {
  val count: Int = values.length
  val bytes: Int = count * VertexElement.elementBytes
  var offset: Int = 0
}

object VertexElement {
  val elementBytes: Int = 4
}

class Vertex(val elems: VertexElement*) {
  val stride: Int = elems.foldLeft(0) { case (sum, next) => sum + next.bytes }
  elems.zipWithIndex.foreach {
    case (e, i) => e.offset = elems.zipWithIndex.foldLeft(0) { case (sum, (next, num)) => if (num >= i) sum else sum + next.bytes }
  }
}

object Vertex {
  def elemCount(vertex: Vertex): Int = vertex.elems.length
  def elemCount(vertexes: Array[Vertex]): Int = vertexes.head.elems.length
}