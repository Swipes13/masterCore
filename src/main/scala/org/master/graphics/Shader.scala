package org.master.graphics

import org.master.graphics
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL32._
import scala.io.Source

/**
  * Created by valentin on 27/02/2018.
  */

object ShaderType extends Enumeration {
  type ShaderType = Value
  val None = 0
  val Vertex: graphics.ShaderType.Value = Value(GL_VERTEX_SHADER)
  val Fragment: graphics.ShaderType.Value = Value(GL_FRAGMENT_SHADER)
  val Geometry: graphics.ShaderType.Value = Value(GL_GEOMETRY_SHADER)
}

class Shader(val `type`: ShaderType.Value) {
  val id: Int = glCreateShader(`type`.id)
  def detach(programId: Int): Unit = glDetachShader(programId, id)
}

object Shader {
  @throws[Exception]
  def create(filename: String, shaderType: ShaderType.ShaderType, defines: Array[String] = Array.empty[String]): Shader = {
    val sourceFromFile = Source.fromFile(filename).mkString

    val shader = Shader(shaderType, sourceFromFile)
    if (shader.id == 0) throw stackTraceError(filename, shader.id)

    glShaderSource(shader.id, sourceFromFile)
    glCompileShader(shader.id)

    if (glGetShaderi(shader.id, GL_COMPILE_STATUS) == GL_FALSE) throw stackTraceError(filename, shader.id)
    shader
  }
  def stackTraceError(filename: String, ptr: Int) = new RuntimeException("compilation error for shader [" + filename + "]. Reason: " + glGetShaderInfoLog(ptr, 1000))
  def clear(shader: Shader): Unit = glDeleteShader(shader.id)

  def apply(`type`: ShaderType.Value, source: String): Shader = {
    if (`type`.id == ShaderType.Vertex.id) new VertexShader(source)
    else new Shader(`type`)
  }
}

class VertexShader(source: String) extends Shader(ShaderType.Vertex) {
  private val sourceLines = source.split('\n')
  var uniforms: Array[String] = getVar("uniform")
  var attribs: Array[String] = getVar("layout")

  private def getVar(find: String) = sourceLines.filter(l => l.contains(find)).map(_.split(' ')).map(line => line.last.substring(0, line.last.length - 1))
}