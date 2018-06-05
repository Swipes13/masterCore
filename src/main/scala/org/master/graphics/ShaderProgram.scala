package org.master.graphics

import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL11._

object ShaderProgramType extends Enumeration {
  type ShaderProgramType = Value
  val CookTorranceForward: ShaderProgramType.Value = Value(0)
  val Simple: ShaderProgramType.Value = Value(1)
  val QuasarResult: ShaderProgramType.Value = Value(2)
}

class ShaderProgram(val shaders: Array[Shader]) {
  val id: Int = glCreateProgram
  var uniformLocations: Map[String, Int] = _
  private var _vaos = Array.empty[Vao]

  def render(): Unit = _vaos.foreach(Vao.render)
  def use(): ShaderProgram = { glUseProgram(id); this }
  def validate(): Unit = ShaderProgram.check(glValidateProgram(id), id, GL_VALIDATE_STATUS, "validate shader error. ")
  def link(): Unit = ShaderProgram.check(glLinkProgram(id), id, GL_LINK_STATUS, "link shader error. ")
  def addVao(vao: Vao): Unit = _vaos = _vaos :+ vao
  def removeVao(vao: Vao): Unit = _vaos = _vaos.filter(_ != vao)
  def prepareUniforms(uniforms: Array[String]): Unit = uniformLocations = uniforms.map(u => (u, glGetUniformLocation(id, u))).toMap[String, Int]
  def updateLocationForUniform(u: Uniform): Uniform = u.withLocation(this.uniformLocations.getOrElse(u.name, 0))
}

object ShaderProgram {
  @throws[Exception]
  def create(shaders: Array[Shader]): ShaderProgram = {
    val program = new ShaderProgram(shaders)
    shaders.foreach(s => glAttachShader(program.id, s.id))
    val vShader = shaders.find(_.`type`.id == ShaderType.Vertex.id).map(v => v.asInstanceOf[VertexShader])

    vShader.foreach(v => v.attribs.zipWithIndex.foreach { case(a, i) => glBindAttribLocation(program.id, i, a) })
    program.link()
    vShader.foreach(v => program.prepareUniforms(v.uniforms))

    program
  }
  def check[R](block: => R, pid: Int, state: Int, message: String): Unit = {
    block
    if (glGetProgrami(pid, state) == GL_FALSE) throw new RuntimeException(message + glGetProgramInfoLog(pid, 1000))
  }
  def clear(): Unit = glUseProgram(0)
  def clear(p: ShaderProgram): Unit = {
    p._vaos.foreach(Vao.clear)
    p.shaders.foreach(s => s.detach(p.id))
    glDeleteProgram(p.id)
    p.shaders.foreach(Shader.clear)
  }
  def render(sp: ShaderProgram): Unit = sp.render()
}
