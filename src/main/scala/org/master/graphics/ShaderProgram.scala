package org.master.graphics

import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL11._

class ShaderProgram(val shaders: Array[Shader]) {
  val id: Int = glCreateProgram

  def use(): Unit = glUseProgram(id)
  def validate(): Unit = ShaderProgram.check(glValidateProgram(id), id, GL_VALIDATE_STATUS, "validate shader error. ")
  def link(): Unit = ShaderProgram.check(glLinkProgram(id), id, GL_LINK_STATUS, "link shader error. ")
}

object ShaderProgram {
  @throws[Exception]
  def create(shaders: Array[Shader], attributes: Array[String]): ShaderProgram = {
    val program = new ShaderProgram(shaders)
    shaders.foreach(s => glAttachShader(program.id, s.id))
    attributes.zipWithIndex.foreach { case(a, i) => glBindAttribLocation(program.id, i, a) }
    program.link()
    program
  }
  def check[R](block: => R, pid: Int, state: Int, message: String): Unit = {
    block
    if (glGetProgrami(pid, state) == GL_FALSE) throw new RuntimeException(message + glGetProgramInfoLog(pid, 1000))
  }
  def clear(): Unit = glUseProgram(0)
  def clear(p: ShaderProgram): Unit = {
    p.shaders.foreach(s => s.detach(p.id))
    glDeleteProgram(p.id)
    p.shaders.foreach(Shader.clear)
  }
}
