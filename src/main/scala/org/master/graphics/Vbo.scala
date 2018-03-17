package org.master.graphics

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL15._

class Vbo() {
  val id: Int = glGenBuffers
  def bind(): Vbo = { glBindBuffer(GL_ARRAY_BUFFER, id); this }
}

object Vbo {
  def create(floats: Array[Float]): Vbo = {
    val vbo = new Vbo().bind()
    glBufferData(GL_ARRAY_BUFFER, prepareBuffer(floats), GL_STATIC_DRAW)
    vbo
  }

  private def prepareBuffer(floats: Array[Float]) = {
    val floatBuffer = BufferUtils.createFloatBuffer(floats.length) // * float size ?
    floatBuffer.put(floats)
    floatBuffer.flip()
    floatBuffer
  }

  def clear(vbo: Vbo): Unit = glDeleteBuffers(vbo.id)
}
