package org.master.graphics

import org.lwjgl.opengl.{GL11, GL20}
import org.lwjgl.opengl.GL20._

class ShaderProgram(val id: Int, shaders: Array[Shader]) {
  shaders.foreach(s => glAttachShader(id, s.id))
  GL20.glBindAttribLocation(id, 0, "VertexPosition")
  link()
//  shaders.foreach(s => s.destroy())

  def use(): Unit = glUseProgram(id)
  def validate(): Unit = ShaderProgram.check(glValidateProgram(id), id, GL_VALIDATE_STATUS, "validate shader error. ")
  def link(): Unit = ShaderProgram.check(glLinkProgram(id), id, GL_LINK_STATUS, "link shader error. ")
}

object ShaderProgram {
  def apply(id: Int, shaders: Array[Shader]) = new ShaderProgram(id, shaders)

  @throws[Exception]
  def create(shaders: Array[Shader]): ShaderProgram = ShaderProgram(glCreateProgram, shaders)
  def check[R](block: => R, pid: Int, state: Int, message: String): Unit = {
    block
    if (glGetProgrami(pid, state) == GL11.GL_FALSE) throw new RuntimeException(message + glGetProgramInfoLog(pid, 1000))
  }
}
