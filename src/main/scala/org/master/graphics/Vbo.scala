package org.master.graphics

import java.nio.{Buffer, ByteBuffer, FloatBuffer, IntBuffer}

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL15._

object BufferType extends Enumeration {
  type BufferType = Value
  val None = 0
  val Vertex: BufferType.Value = Value(GL_ARRAY_BUFFER)
  val Index: BufferType.Value = Value(GL_ELEMENT_ARRAY_BUFFER)
}

class Vbo(val bufferType: BufferType.Value) {
  val id: Int = glGenBuffers
  def bind(): Vbo = { glBindBuffer(bufferType.id, id); this }
  def prepareData(buffer: ByteBuffer): Vbo = { glBufferData(bufferType.id, buffer, GL_STATIC_DRAW); this }
  def prepareData(buffer: FloatBuffer): Vbo = { glBufferData(bufferType.id, buffer, GL_STATIC_DRAW); this }
}

object Vbo {
  def create(bufferType: BufferType.Value, values: Array[Float]): Vbo = new Vbo(bufferType).bind().prepareData(prepareBuffer(values))
  def create(ibuffer: ByteBuffer): Vbo = new Vbo(BufferType.Index).bind().prepareData(ibuffer)

  def prepareBuffer(v: Array[Byte]): ByteBuffer = {
    val buffer = BufferUtils.createByteBuffer(v.length)
    buffer.put(v).flip()
    buffer
  }
  private def prepareBuffer(v: Array[Float]) = {
    val buffer = BufferUtils.createFloatBuffer(v.length)
    buffer.put(v).flip()
    buffer
  }

  def clear(vbo: Vbo): Unit = glDeleteBuffers(vbo.id)
}
