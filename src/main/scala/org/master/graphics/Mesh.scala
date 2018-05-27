package org.master.graphics

import org.joml.Vector3f

import scala.io.Source
import scala.collection.immutable.Map
import scala.collection.mutable.ArrayBuffer

class Mesh {
  var format = MeshFormat()
  var physicalNames = Map.empty[Int, PhysicalName]
  var nodes = List.empty[MeshNode]
  var elements = List.empty[MeshElement]

  var vao = Map.empty[Int, Vao]

  def initVaos(): Unit = {
    val vertexesWithIndex = nodes.map(node => (node.index, new VertexElement(node.x, node.y, node.z))).toMap
    physicalNames.foreach { case (physicalIndex, _) =>
      if (physicalIndex == 1) vao += (physicalIndex -> createVaoForPhysicalName(physicalIndex, vertexesWithIndex))
    }
  }

  def createVaoForPhysicalName(physicalIndex: Int, vertexesWithIndex: Map[Long, VertexElement]): Vao = {
    val indexesWithCheck = elements.flatMap { element =>
      MeshElementType.fromInt(element.`type`) match {
        case Some(t) if element.tags.head != physicalIndex => Array.empty[Int]
        case Some(MeshElementType.Triangle) => element.nodeIndexes
        case Some(MeshElementType.Tetrahedron) => Array(0, 2, 1, 0, 1, 3, 0, 3, 2, 1, 2, 3).map(i => element.nodeIndexes(i))
        case _ => println(s"unsupported mesh element type: ${element.`type`}"); Array.empty[Int]
      }
    }
    var vertexes = indexesWithCheck.sliding(3, 3).flatMap { tri => val triPoints = tri.map(t => vertexesWithIndex(t))
      val (a, b, c) = (triPoints.head, triPoints(1), triPoints(2))

      val ab = b.toVector3f.sub(a.toVector3f, new Vector3f())
      val ac = c.toVector3f.sub(a.toVector3f, new Vector3f())
      val n = ac.cross(ab, new Vector3f()).normalize()
      List(
        new Vertex(a, new VertexElement(n), new VertexElement(0, 0)),
        new Vertex(b, new VertexElement(n), new VertexElement(0, 0)),
        new Vertex(c, new VertexElement(n), new VertexElement(0, 0))
      )
    }.zipWithIndex.toArray
    var indexes = vertexes.indices.map((_, false)).toArray

    for (i <- vertexes.indices) {
      for (j <- Range(vertexes.length - 1, i, -1)) {
//        println(i, j)
        if (vertexes(i)._1 == vertexes(j)._1) {
          val toDrop = vertexes(j)._2
          vertexes = vertexes.take(j) ++ vertexes.drop(j + 1)
          indexes = indexes.map(index => if (index._1 == toDrop) (i, true) else index)
        }

      }
    }
    for (i <- vertexes.indices) indexes = indexes.map(ind => if (!ind._2 && ind._1 == vertexes(i)._2) (i, true) else ind)

    Vao.createInterleaved(DrawType.Triangles, vertexes.map(_._1), indexes.map(_._1))
  }
}

object Mesh {
  def fromFile(fileName: String): Mesh = {
    val linesFromFile = Source.fromFile(fileName).getLines().toList

    var indexes = scala.collection.mutable.ArrayBuffer.empty[Int]
    linesFromFile.zipWithIndex.foreach {
      case (line, id) if ReservedGmshType.all.exists(t => line.contains(t)) => indexes += id
      case _ =>
    }

    val mesh = new Mesh()
    indexes.toList.sliding(2, 2).toList.foreach { blockIndexes =>
      val (start, end) = blockIndexes.head -> blockIndexes(1)
      val FullBlock = linesFromFile.slice(start, end)
      val (head, block) = FullBlock.head -> FullBlock.tail

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
    mesh.initVaos()
    mesh
  }
}