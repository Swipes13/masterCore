package org.master.graphics

import org.joml.Vector3f

import scala.io.Source
import scala.collection.immutable.Map
import scala.collection.mutable.ArrayBuffer

class Mesh(val fileName: String) {
  var format = MeshFormat()
  var physicalNames = Map.empty[Int, PhysicalName]
  var nodes = List.empty[MeshNode]
  var elements = List.empty[MeshElement]

  var vaos = Map.empty[Int, Vao]

  def initVaos(withSaveToVaoFile: Boolean): Unit = {
    val vertexesWithIndex = nodes.map(node => (node.index, new VertexElement(node.x, node.y, node.z).withType(VEType.Position))).toMap
    physicalNames.foreach { case (physicalIndex, _) =>
      if (physicalIndex == 1) vaos += (physicalIndex -> createVaoForPhysicalName(physicalIndex, vertexesWithIndex, withSaveToVaoFile))
    }
  }

  def createVaoForPhysicalName(physicalIndex: Int, vertexesWithIndex: Map[Long, VertexElement], withSaveToVaoFile: Boolean): Vao = {
    val indexesWithCheck = elements.flatMap { element =>
      MeshElementType.fromInt(element.`type`) match {
        case Some(t) if element.tags.head != physicalIndex => Array.empty[Int]
        case Some(MeshElementType.Triangle) => element.nodeIndexes
        case Some(MeshElementType.Tetrahedron) => Array(0, 2, 1, 0, 1, 3, 0, 3, 2, 1, 2, 3).map(i => element.nodeIndexes(i))
        case _ => println(s"unsupported mesh element type: ${element.`type`}"); Array.empty[Int]
      }
    }

    val center = BBox.createFromVertexElement(vertexesWithIndex.values.toSeq).center
    var vertexes = indexesWithCheck.sliding(3, 3).flatMap { tri => val triPoints = tri.map(t => vertexesWithIndex(t))
      val (a, b, c) = (triPoints.head, triPoints(1), triPoints(2))

      val ab = b.toVector3f.sub(a.toVector3f, new Vector3f())
      val ac = c.toVector3f.sub(a.toVector3f, new Vector3f())
      val n = ac.cross(ab, new Vector3f()).normalize()

      val texCoords = Seq(
        a.toVector3f.sub(center, new Vector3f()).normalize(),
        b.toVector3f.sub(center, new Vector3f()).normalize(),
        c.toVector3f.sub(center, new Vector3f()).normalize()
      ).map(new VertexElement(_).withType(VEType.TexCoords))

      List(
        new Vertex(a, new VertexElement(n).withType(VEType.Normal), texCoords(0)),
        new Vertex(b, new VertexElement(n).withType(VEType.Normal), texCoords(1)),
        new Vertex(c, new VertexElement(n).withType(VEType.Normal), texCoords(2))
      )
    }.zipWithIndex.toArray
    var indexes = vertexes.indices.map((_, false)).toArray

    for (i <- vertexes.indices) {
      for (j <- Range(vertexes.length - 1, i, -1)) {
        if (vertexes(i)._1 == vertexes(j)._1) {
          val toDrop = vertexes(j)._2
          vertexes = vertexes.take(j) ++ vertexes.drop(j + 1)
          indexes = indexes.map(index => if (index._1 == toDrop) (i, true) else index)
        }

      }
    }
    for (i <- vertexes.indices) indexes = indexes.map(ind => if (!ind._2 && ind._1 == vertexes(i)._2) (i, true) else ind)

    val v = vertexes.map(_._1)
    val i = indexes.map(_._1)

    val filteredIndexes = removeTriReps(v, i)

    if (withSaveToVaoFile) Vao.saveToFile(s"${fileName}_${physicalNames(physicalIndex).name}", v, filteredIndexes)
    Vao.createInterleaved(DrawType.Triangles, v, filteredIndexes)
  }

  def removeTriReps(vertexes: Seq[Vertex], indexes: Seq[Int]): Array[Int] = org.master.core.Utils.logging() {
    var filtered: Array[Int] = null
    org.master.core.Utils.deltaTime {
      val triMap = indexes.sliding(3, 3).map((_, 0)).toArray
      filtered = triMap.filter { case (tri, count) =>
        triMap.count { case (t, c) =>
          val (v1, v2, v3) = (vertexes(t(0)).position, vertexes(t(1)).position, vertexes(t(2)).position)
          val (p1, p2, p3) = (vertexes(tri(0)).position, vertexes(tri(1)).position, vertexes(tri(2)).position)

          (v1 == p1 && v2 == p2 && v3 == p3) ||
          (v1 == p1 && v2 == p3 && v3 == p2) ||
          (v1 == p2 && v2 == p1 && v3 == p3) ||
          (v1 == p2 && v2 == p3 && v3 == p1) ||
          (v1 == p3 && v2 == p1 && v3 == p2) ||
          (v1 == p3 && v2 == p2 && v3 == p1)

        } == 1
      }.flatMap(_._1)

    }
    filtered
  }
}

object Mesh {
  def fromFile(fileName: String, withSaveToVaoFile: Boolean = false): Mesh = {
    val linesFromFile = Source.fromFile(fileName).getLines().toList

    var indexes = scala.collection.mutable.ArrayBuffer.empty[Int]
    linesFromFile.zipWithIndex.foreach {
      case (line, id) if ReservedGmshType.all.exists(t => line.contains(t)) => indexes += id
      case _ =>
    }
    val mesh = new Mesh(fileName.split('/').last.split('.').head)
    indexes.toList.sliding(2, 2).toList.foreach { blockIndexes =>
      val (start, end) = blockIndexes.head -> blockIndexes(1)
      val fullBlock = linesFromFile.slice(start, end)
      val (head, block) = fullBlock.head -> fullBlock.tail

      ReservedGmshType.all.find(t => head.contains(t)) match {
        case Some(t) => ReservedGmshType.withName(t) match {
          case ReservedGmshType.Format => mesh.format = MeshFormat.fromString(block.head)
          case ReservedGmshType.PhysicalNames => mesh.physicalNames = block.tail.map(PhysicalName.fromString).toMap
          case ReservedGmshType.Nodes => mesh.nodes = block.tail.map(MeshNode.fromString)
          case ReservedGmshType.Elements => mesh.elements = block.tail.map(MeshElement.fromString)
          case _ => println(s"unsupported mesh block type: $t")
        }
        case _ =>
      }
    }
    mesh.initVaos(withSaveToVaoFile)
    mesh
  }
}