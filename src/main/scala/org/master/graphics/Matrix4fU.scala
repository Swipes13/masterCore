package org.master.graphics

import java.nio.{Buffer, FloatBuffer}

import org.joml.{Matrix4f, Vector2f, Vector3f}
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20._
import org.master.core.Window

trait Uniform {
  var location: Int = 0
  var name = ""
  def set(location: Int = this.location)
  def withLocation(location: Int): Uniform = { this.location = location; this }
  def withName(name: String): Uniform = { this.name = name; this }
}

object Uniform {
  def set(uniform: Uniform): Unit = uniform.set()
}

class Matrix4fU extends Matrix4f with Uniform {
  private val buffer: FloatBuffer = BufferUtils.createFloatBuffer(16)
  private def updateBuffer(): FloatBuffer = { this.get(buffer); buffer }

  override def set(location: Int = this.location): Unit = glUniformMatrix4fv(location, false, updateBuffer())
}

object ProjectionType extends Enumeration {
  type ProjectionType = Value
  val Perspective: ProjectionType.Value = Value(0)
  val Orthographical: ProjectionType.Value = Value(1)
}

class ProjectionMatrix(val `type`: ProjectionType.Value) extends Matrix4fU {}
class OrthoProjectionMatrix(val width: Float, val height: Float, val zNear: Float, val zFar: Float, var zoomCf: Float = 1.0f)
  extends ProjectionMatrix(ProjectionType.Orthographical) {
  Matrix4fU.orthographical(this)

  def zoom(deltaCf: Float): OrthoProjectionMatrix = {
    zoomCf *= deltaCf
    Matrix4fU.orthographical(this)
  }
}

object Matrix4fU {
  def perspective(width: Int, height: Int, fov: Float, zNear: Float, zFar: Float): ProjectionMatrix = {
    val ret = new ProjectionMatrix(ProjectionType.Perspective)
    ret.perspective(Math.toRadians(fov).toFloat, width / height.toFloat, zNear, zFar)
    ret
  }
  def orthographical(pm: OrthoProjectionMatrix): OrthoProjectionMatrix = {
    val left = -pm.width / 2.0f
    val right = pm.width / 2.0f
    val top = -pm.height / 2.0f
    val bottom = pm.height / 2.0f
    pm.setOrtho(left * pm.zoomCf, right * pm.zoomCf, bottom * pm.zoomCf, top * pm.zoomCf, pm.zNear, pm.zFar)
    pm
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
class Float3U(var v1: Float = 0, var v2: Float = 0, var v3: Float = 0) extends Uniform {
  override def set(location: Int = this.location): Unit = glUniform3f(location, v1, v2, v3)
  def update(v: Vector3f): Float3U = { v1 = v.x; v2 = v.y; v3 = v.z; this }
  def this(v: Vector3f) = this(v.x, v.y, v.z)
}
class Float4U(var v1: Float = 0, var v2: Float = 0, var v3: Float = 0, var v4: Float = 0) extends Uniform { override def set(location: Int = this.location): Unit = glUniform4f(location, v1, v2, v3, v4) }