package org.master.graphics

import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL11._

class ShaderProgram(val shaders: Array[Shader]) {
  val id: Int = glCreateProgram
  var uniformLocs: Map[String, Int] = _
  private var _vaos = Array.empty[Vao]

  def render(): Unit = use()._vaos.foreach(Vao.render)
  def use(): ShaderProgram = { glUseProgram(id); this }
  def validate(): Unit = ShaderProgram.check(glValidateProgram(id), id, GL_VALIDATE_STATUS, "validate shader error. ")
  def link(): Unit = ShaderProgram.check(glLinkProgram(id), id, GL_LINK_STATUS, "link shader error. ")
  def addVao(vao: Vao): Unit = _vaos = _vaos :+ vao
  def prepareUniforms(uniforms: Array[String]): Unit = uniformLocs = uniforms.map(u => (u, glGetUniformLocation(id, u))).toMap[String, Int]
  def updateLocForU(u: Uniform): Uniform = u.withLoc(this.uniformLocs.getOrElse(u.name, 0))
}

object ShaderProgram {
  @throws[Exception]
  def create(shaders: Array[Shader], attributes: Array[String], uniforms: Array[String] = Array.empty): ShaderProgram = {
    val program = new ShaderProgram(shaders)
    shaders.foreach(s => glAttachShader(program.id, s.id))
    attributes.zipWithIndex.foreach { case(a, i) => glBindAttribLocation(program.id, i, a) }
    program.link()
    program.prepareUniforms(uniforms)
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
