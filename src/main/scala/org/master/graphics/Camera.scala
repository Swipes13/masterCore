package org.master.graphics

import org.joml.{Matrix4f, Vector3f}

class Camera {
  val position = new Vector3f(0, 0, 0)
  def update(): Matrix4f = Matrix4fUniform.lookAt(new Vector3f(), position)
}
