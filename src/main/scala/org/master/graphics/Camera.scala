package org.master.graphics

import org.joml.{Matrix4f, Vector3f}

class Camera extends Matrix4fUniform {
  val position = new Vector3f(0, 0, 20)
  def update(): Matrix4f = this.lookAt(new Vector3f(0, 0, -1), position, new Vector3f(-1, 0, 0))
}
