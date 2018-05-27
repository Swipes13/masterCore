package org.master.graphics

import org.joml.Vector3f

case class VertexElement(values: Float*) {
  val count: Int = values.length
  val bytes: Int = count * VertexElement.elementBytes
  var offset: Int = 0

  def toVector3f: Vector3f = new Vector3f(if (values.nonEmpty) values.head else 0,
                                          if (values.length >= 2) values(1) else 0,
                                          if (values.length >= 3) values(2) else 0)
  def this(v: Vector3f) = this(v.x, v.y, v.z)


  override def equals(obj: Any): Boolean = obj match {
    case that: VertexElement =>
      if (that.count == count) {
        !values.zip(that.values).exists { case (l, r) => l != r }
      } else false
    case _ => false
  }
  override def hashCode(): Int = 42 * (42 + bytes) + count
}

object VertexElement {
  val elementBytes: Int = 4
}

class Vertex(val elems: VertexElement*) {
  val stride: Int = elems.foldLeft(0) { case (sum, next) => sum + next.bytes }
  elems.zipWithIndex.foreach {
    case (e, i) => e.offset = elems.zipWithIndex.foldLeft(0) { case (sum, (next, num)) => if (num >= i) sum else sum + next.bytes }
  }

  override def equals(obj: Any): Boolean = obj match {
    case that: Vertex => !elems.zip(that.elems).exists { case (l, r) => l != r }
    case _ => false
  }

  override def hashCode(): Int = 42 * (42 + stride) + elems.head.count

//  def canEqual(other: Any): Boolean =
//    other.isInstanceOf[Vertex]
}

object Vertex {
  def elemCount(vertex: Vertex): Int = vertex.elems.length
  def elemCount(vertexes: Array[Vertex]): Int = vertexes.head.elems.length
}