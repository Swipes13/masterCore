package org.master.graphics

import org.joml.Vector3f
import org.master.core.{Utils => CUtils}

object VEType extends Enumeration {
  type VertexElementType = Value
  val Position: VEType.Value = Value(0)
  val Color: VEType.Value = Value(1)
  val TexCoords: VEType.Value = Value(2)
  val Normal: VEType.Value = Value(3)
  val BarycenterCoords: VEType.Value = Value(4)
  val QuasarScalar: VEType.Value = Value(5)
  val QuasarVector: VEType.Value = Value(6)

  val User: VEType.Value = Value(100)
}

case class VertexElement(values: Float*) {
  val count: Int = values.length
  val bytes: Int = count * VertexElement.elementBytes
  var offset: Int = 0
  var `type`: VEType.Value = VEType.User

  def toVector3f: Vector3f = new Vector3f(if (values.nonEmpty) values.head else 0,
                                          if (values.length >= 2) values(1) else 0,
                                          if (values.length >= 3) values(2) else 0)
  def this(v: Vector3f) = this(v.x, v.y, v.z)
  def withType(t: VEType.Value): VertexElement = { `type` = t; this }

  override def equals(obj: Any): Boolean = obj match {
    case that: VertexElement =>
      if (that.count == count) {
        !values.zip(that.values).exists { case (l, r) => l != r }
      } else false
    case _ => false
  }
  override def hashCode(): Int = 42 * (42 + bytes) + count
  override def toString: String = CUtils.arrayToStrWithDelim(values.toArray, ' ')

  def mag: Float = Math.sqrt(values.map(Math.pow(_, 2)).sum).toFloat
  def len: Int = count
}

object VertexElement {
  val elementBytes: Int = 4

  def fromString(string: String): VertexElement = VertexElement(string.split(' ').map(_.toFloat):_*)
  def texCoordsFromSphericalNormal(sn: Vector3f): VertexElement = VertexElement(
//    (Math.asin(sn.x) / Math.PI + 0.5f).toFloat,
//    (Math.asin(sn.y) / Math.PI + 0.5f).toFloat
    sn.x / 2 + 0.5f,
    -sn.y / 2 + 0.5f
  ).withType(VEType.TexCoords)
}

case class Vertex(elems: VertexElement*) {
  val stride: Int = elems.foldLeft(0) { case (sum, next) => sum + next.bytes }
  elems.zipWithIndex.foreach {
    case (e, i) => e.offset = elems.zipWithIndex.foldLeft(0) { case (sum, (next, num)) => if (num >= i) sum else sum + next.bytes }
  }

  override def equals(obj: Any): Boolean = obj match {
    case that: Vertex => !elems.zip(that.elems).exists { case (l, r) => l != r }
    case _ => false
  }

  override def hashCode(): Int = 42 * (42 + stride) + elems.head.count
  override def toString: String = CUtils.arrayToStrWithDelim(elems.toArray, '\t')

  def texCoords: VertexElement = get(VEType.TexCoords, VertexElement(0, 0))
  def position: VertexElement = get(VEType.Position, VertexElement(0, 0, 0))
  def get(`type`: VEType.Value, default: VertexElement): VertexElement = elems.find(_.`type` == VEType.Position).getOrElse(default)
}

object Vertex {
  def elemCount(vertex: Vertex): Int = vertex.elems.length
  def elemCount(vertexes: Seq[Vertex]): Int = vertexes.head.elems.length
  def fromString(string: String): Vertex = Vertex(string.split('\t').map(VertexElement.fromString):_*)
}