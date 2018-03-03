package org.master.graphics

import org.lwjgl.opengl.{ARBFragmentShader, ARBShaderObjects, ARBVertexShader, GL11}
import org.master.graphics
import org.lwjgl.opengl.GL20._
import scala.io.Source

/**
  * Created by valentin on 27/02/2018.
  */

object ShaderType extends Enumeration {
  type ShaderType = Value
  val None = 0
  val Vertex: graphics.ShaderType.Value = Value(GL_VERTEX_SHADER)
  val Fragment: graphics.ShaderType.Value = Value(GL_FRAGMENT_SHADER)
}

class Shader(val ptr: Int, `type`: ShaderType.Value) {
  def destroy(): Unit = Shader.destroy(ptr)
}

object Shader {
  def apply(ptr: Int, `type`: ShaderType.Value) = new Shader(ptr, `type`)
  def destroy(ptr: Int): Unit = ARBShaderObjects.glDeleteObjectARB(ptr)

  @throws[Exception]
  def create(filename: String, shaderType: ShaderType.ShaderType): Shader = {
    var ptr = 0
    try {
      ptr = glCreateShader(shaderType.id)
      if (ptr == 0) throw stackTraceError(filename, ptr)

      glShaderSource(ptr, Source.fromFile(filename).getLines.mkString)
      glCompileShader(ptr)

      if (glGetShaderi(ptr, GL_COMPILE_STATUS) == GL11.GL_FALSE) throw stackTraceError(filename, ptr)

      Shader(ptr, shaderType)
    } catch {
      case e: Exception =>
        destroy(ptr)
        throw e
    }
  }
  def stackTraceError(filename: String, ptr: Int) = new RuntimeException("compilation error for shader [" + filename + "]. Reason: " + glGetShaderInfoLog(ptr, 1000))
}