package org.master.graphics

import scala.io.Source

class Mesh {
  var format = MeshFormat()
  var physicalNames = List.empty[PhysicalName]
  var nodes = List.empty[MeshNode]
  var elements = List.empty[MeshElement]

  var vao: Option[Vao] = None

  def initVao(): Unit = {
    // TODO: think about not dense nodes input
    val vertexes = nodes.map(node =>
      new Vertex(
        VertexElement(node.x, node.y, node.z),
        VertexElement(0, 0, 0), // normal
        VertexElement(0, 0)     // tutv
      )
    ).toArray
    val indexes = elements.flatMap { element =>
      MeshElementType(element.`type`) match {
        case MeshElementType.Triangle =>
          element.verts.map(_ - 1)
        case MeshElementType.Tetrahedron =>
          Array(0, 1, 2, 0, 1, 3, 0, 2, 3, 1, 2, 3).map(i => element.verts(i)).map(_ - 1)
        case unsupported =>
          println(s"unsupported mesh element type: $unsupported")
          Array.empty[Int]
      }
    }.toArray
    vao = Some(Vao.createInterleaved(DrawType.LineLoop, vertexes, indexes))
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
          case ReservedGmshType.PhysicalNames => mesh.physicalNames = block.tail.map(PhysicalName.fromString)
          case ReservedGmshType.Nodes => mesh.nodes = block.tail.map(MeshNode.fromString)
          case ReservedGmshType.Elements => mesh.elements = block.tail.map(MeshElement.fromString)
          case _ => println(s"unsupported mesh block type: $t")
        }
        case _ =>
      }
    }
    mesh.initVao()
    mesh
  }
}