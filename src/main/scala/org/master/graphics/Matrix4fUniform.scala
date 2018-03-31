package org.master.graphics

import java.nio.{Buffer, FloatBuffer}

import org.joml.{Matrix4f, Vector3f}
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20._
import org.master.core.Window

trait Uniform {

}

class Matrix4fUniform extends Matrix4f with Uniform {
  var location: Int = 0
  var name = ""
  private val buffer: FloatBuffer = BufferUtils.createFloatBuffer(16)
  private def updateBuffer(): FloatBuffer = { this.get(buffer); buffer }

  def render(location: Int = this.location): Unit = glUniformMatrix4fv(location, false, updateBuffer())
  def withName(name: String): Matrix4fUniform = { this.name = name; this }
  def withLocation(location: Int): Matrix4fUniform = { this.location = location; this }
}

object Matrix4fUniform {
  def perspective(fov: Float, near: Float, far: Float): Matrix4fUniform = {
    val proj = new Matrix4fUniform()
    proj.perspective(Math.toRadians(fov).toFloat, Window.size.width / Window.size.height.toFloat, near, far)
    proj
  }
  def lookAt(eye: Vector3f, pos: Vector3f): Matrix4fUniform = {
    val view = new Matrix4fUniform()
    view.lookAt(eye, pos, new Vector3f(0, 1, 0))
    view
  }
}
