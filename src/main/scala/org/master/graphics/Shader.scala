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

class Shader(val id: Int, `type`: ShaderType.Value) {
  def destroy(): Unit = glDeleteShader(id) // TODO: need IMPLEMENT ?
}

object Shader {
  def apply(ptr: Int, `type`: ShaderType.Value) = new Shader(ptr, `type`)

  @throws[Exception]
  def create(filename: String, shaderType: ShaderType.ShaderType): Shader = {
    val shader = Shader(glCreateShader(shaderType.id), shaderType)
    if (shader.id == 0) throw stackTraceError(filename, shader.id)

    glShaderSource(shader.id, Source.fromFile(filename).mkString)
    glCompileShader(shader.id)

    if (glGetShaderi(shader.id, GL_COMPILE_STATUS) == GL11.GL_FALSE) throw stackTraceError(filename, shader.id)
    shader
  }
  def stackTraceError(filename: String, ptr: Int) = new RuntimeException("compilation error for shader [" + filename + "]. Reason: " + glGetShaderInfoLog(ptr, 1000))
}