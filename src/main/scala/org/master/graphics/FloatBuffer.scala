package org.master.graphics

import java.nio

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL15._

class FloatBuffer(val id: Int, val length: Int) {
  def bind(): Unit = GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id)
}

object FloatBuffer {
  def create(floats: Array[Float]): FloatBuffer = {
    val floatBuffer = BufferUtils.createFloatBuffer(floats.length)
    floatBuffer.put(floats)
    floatBuffer.rewind()

    val fb = FloatBuffer(glGenBuffers, floats.length)
    fb.bind()
    glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW)
    fb
  }

  def apply(id: Int, length: Int): FloatBuffer = new FloatBuffer(id, length)
}
