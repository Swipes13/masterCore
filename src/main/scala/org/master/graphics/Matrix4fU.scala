package org.master.graphics

import java.nio.{Buffer, FloatBuffer}

import org.joml.{Matrix4f, Vector3f}
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20._
import org.master.core.Window

trait Uniform {
  var location: Int = 0
  var name = ""
  def set(location: Int = this.location)
  def withLoc(location: Int): Uniform = { this.location = location; this }
  def withName(name: String): Uniform = { this.name = name; this }
}

class Matrix4fU extends Matrix4f with Uniform {
  private val buffer: FloatBuffer = BufferUtils.createFloatBuffer(16)
  private def updateBuffer(): FloatBuffer = { this.get(buffer); buffer }

  override def set(location: Int = this.location): Unit = glUniformMatrix4fv(location, false, updateBuffer())
}

object Matrix4fU {
  def perspective(fov: Float, near: Float, far: Float): Matrix4fU = {
    val proj = new Matrix4fU()
    proj.perspective(Math.toRadians(fov).toFloat, Window.size.width / Window.size.height.toFloat, near, far)
    proj
  }
  def lookAt(eye: Vector3f, pos: Vector3f): Matrix4fU = {
    val view = new Matrix4fU()
    view.lookAt(eye, pos, new Vector3f(0, 1, 0))
    view
  }
}

class Int1U(var v1: Int = 0) extends Uniform { override def set(location: Int = this.location): Unit = glUniform1i(location, v1) }
class Int2U(var v1: Int = 0, var v2: Int = 0) extends Uniform { override def set(location: Int = this.location): Unit = glUniform2i(location, v1, v2) }
class Int3U(var v1: Int = 0, var v2: Int = 0, var v3: Int = 0) extends Uniform { override def set(location: Int = this.location): Unit = glUniform3i(location, v1, v2, v3) }
class Int4U(var v1: Int = 0, var v2: Int = 0, var v3: Int = 0, var v4: Int = 0) extends Uniform { override def set(location: Int = this.location): Unit = glUniform4i(location, v1, v2, v3, v4) }

class Float1U(var v1: Float = 0) extends Uniform { override def set(location: Int = this.location): Unit = glUniform1f(location, v1) }
class Float2U(var v1: Float = 0, var v2: Float = 0) extends Uniform { override def set(location: Int = this.location): Unit = glUniform2f(location, v1, v2) }
class Float3U(var v1: Float = 0, var v2: Float = 0, var v3: Float = 0) extends Uniform { override def set(location: Int = this.location): Unit = glUniform3f(location, v1, v2, v3) }
class Float4U(var v1: Float = 0, var v2: Float = 0, var v3: Float = 0, var v4: Float = 0) extends Uniform { override def set(location: Int = this.location): Unit = glUniform4f(location, v1, v2, v3, v4) }