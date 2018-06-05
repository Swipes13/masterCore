package org.master.graphics

import org.joml.Vector3f

case class BBox(min: Vector3f, max: Vector3f) {
  val center: Vector3f = min.add(max, new Vector3f()).div(2, new Vector3f())
}

object BBox {
  def createFromVertex(vertexes: Seq[Vertex]): BBox = {
    createFromVertexElement(vertexes.foldLeft(Array.empty[VertexElement]) { case (prev, next) =>
      next.elems.find(_.`type` == VEType.Position) match {
        case Some(vertexElement) => prev :+ vertexElement
        case _ => prev
      }
    })
  }
  def createFromVertexElement(vertexeElements: Seq[VertexElement]): BBox = {
    val pos3f = vertexeElements.map(_.toVector3f)
    BBox(
      new Vector3f(pos3f.minBy(_.x).x,pos3f.minBy(_.y).y, pos3f.minBy(_.z).z),
      new Vector3f(pos3f.maxBy(_.x).x,pos3f.maxBy(_.y).y, pos3f.maxBy(_.z).z)
    )
  }
}
