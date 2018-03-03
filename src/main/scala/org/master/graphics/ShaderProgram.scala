package org.master.graphics

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20._

class ShaderProgram(val ptr: Int) {
  def set(): Unit = glUseProgram(ptr)
}

object ShaderProgram {
  def apply(ptr: Int) = new ShaderProgram(ptr)
  def create(shaders: Array[Shader]): ShaderProgram = {
    val ptr = glCreateProgram

    shaders.foreach(s => glAttachShader(ptr, s.ptr))

    glLinkProgram(ptr)
    if (glGetProgrami(ptr, GL_LINK_STATUS) == GL11.GL_FALSE) throw new RuntimeException("could not link shader. Reason: " + glGetProgramInfoLog(ptr, 1000))

    glValidateProgram(ptr)
    if (glGetProgrami(ptr, GL_VALIDATE_STATUS) == GL11.GL_FALSE) throw new RuntimeException("could not validate shader. Reason: " + glGetProgramInfoLog(ptr, 1000))

    ShaderProgram(ptr)
  }
}
