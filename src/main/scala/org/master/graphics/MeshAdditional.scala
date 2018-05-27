package org.master.graphics

case class MeshFormat(version: String = "", fileType: String = "", dataSize: Long = 0L)
object MeshFormat {
  def fromString(string: String): MeshFormat = {
    val format = string.split(Array(' ', '\t'))
    MeshFormat(format(0), format(1), format(2).toInt)
  }
}
case class PhysicalName(dim: Int, name: String)
object PhysicalName {
  def fromString(string: String): (Int, PhysicalName) = {
    val pn = string.split(Array(' ', '\t'))
    (pn(1).toInt, PhysicalName(pn(0).toInt, pn(2).filter(c => c != '"')))
  }
}
case class MeshNode(index: Long, x: Float, y: Float, z: Float)
object MeshNode {
  def fromString(string: String): MeshNode = {
    val n = string.split(Array(' ', '\t'))
    MeshNode(n(0).toInt, n(1).toFloat, n(2).toFloat, n(3).toFloat)
  }
}
case class MeshElement(index: Long, `type`: Int, tags: List[Int], nodeIndexes: List[Int])
object MeshElement {
  def fromString(string: String): MeshElement = {
    val e = string.split(Array(' ', '\t'))
    val (index, t, numberOfTags) = (e(0).toLong, e(1).toInt, e(2).toInt)
    val tags = e.slice(3, 3 + numberOfTags).map(_.toInt).toList
    val verts = e.slice(3 + numberOfTags, e.length).map(_.toInt).toList
    MeshElement(index, t, tags, verts)
  }
}
object MeshElementType extends Enumeration {
  type MeshElementType = Value

  val Triangle = Value(2)
  val Tetrahedron = Value(4)

  def fromInt(t: Int): Option[MeshElementType] = t match {
    case 2 => Some(MeshElementType.Triangle)
    case 4 => Some(MeshElementType.Tetrahedron)
    case _ => None
  }
  // TODO: support other types
}

object ReservedGmshType extends Enumeration {
  type ReservedGmshType = Value
  val Format = Value(0, "$MeshFormat")
  val EndFormat = Value(1, "$EndMeshFormat")
  val Nodes = Value(2, "$Nodes")
  val EndNodes = Value(3, "$EndNodes")
  val PhysicalNames = Value(4, "$PhysicalNames")
  val EndPhysicalNames = Value(5, "$EndPhysicalNames")
  val Elements = Value(6, "$Elements")
  val EndElements = Value(7, "$EndElements")

  val all = Array(
    Format.toString,
    EndFormat.toString,
    Nodes.toString,
    EndNodes.toString,
    PhysicalNames.toString,
    EndPhysicalNames.toString,
    Elements.toString,
    EndElements.toString
  )
}
