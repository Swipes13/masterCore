package org.master.graphics

import org.master.graphics
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL11._
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

class Shader(`type`: ShaderType.Value) {
  val id: Int = glCreateShader(`type`.id)
  def detach(programId: Int): Unit = glDetachShader(programId, id)
}

object Shader {
  @throws[Exception]
  def create(filename: String, shaderType: ShaderType.ShaderType, defines: Array[String] = Array.empty[String]): Shader = {
    val shader = new Shader(shaderType)
    if (shader.id == 0) throw stackTraceError(filename, shader.id)

    val sourceFromFile = Source.fromFile(filename).mkString
    glShaderSource(shader.id, sourceFromFile)
    glCompileShader(shader.id)

    if (glGetShaderi(shader.id, GL_COMPILE_STATUS) == GL_FALSE) throw stackTraceError(filename, shader.id)
    shader
  }
  def stackTraceError(filename: String, ptr: Int) = new RuntimeException("compilation error for shader [" + filename + "]. Reason: " + glGetShaderInfoLog(ptr, 1000))
  def clear(shader: Shader): Unit = glDeleteShader(shader.id)
}