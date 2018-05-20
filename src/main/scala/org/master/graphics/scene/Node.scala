package org.master.graphics.scene

import org.joml.Vector3f

class Node {
  var position: Vector3f = new Vector3f()
  var scale: Vector3f = new Vector3f()
  var rotation: Float = 0f

  var components = List.empty[Component]
  var childs = List.empty[Node]

  def addChild(node: Node): Unit = {
    childs +:= node
  }

  def addComponent(cmp: Component): Unit = {
    components +:= cmp
  }
}

object Node {
  def create(pos: Vector3f = new Vector3f(), scale: Vector3f = new Vector3f(), rotation: Float = 0f): Node = {
    val node = new Node()
    node.position = pos
    node.scale = scale
    node.rotation = rotation
    node
  }

  def draw(node: Node, dt: Double): Unit = {
    node.components.foreach(_.update(dt))
    node.childs.foreach(n => draw(n, dt))
  }
}

