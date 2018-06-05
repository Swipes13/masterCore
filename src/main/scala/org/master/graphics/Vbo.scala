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

object BufferDrawType extends Enumeration {
  type BufferDrawType = Value
  val None = 0
  val Static: BufferDrawType.Value = Value(GL_STATIC_DRAW)
  val Dynamic: BufferDrawType.Value = Value(GL_DYNAMIC_DRAW)
}

class Vbo(val bufferType: BufferType.Value, drawType: BufferDrawType.Value) {
  val id: Int = glGenBuffers
  def bind(): Vbo = { glBindBuffer(bufferType.id, id); this }
  def prepareData(buffer: IntBuffer): Vbo = { glBufferData(bufferType.id, buffer, drawType.id); this }
  def prepareData(buffer: FloatBuffer): Vbo = { glBufferData(bufferType.id, buffer, drawType.id); this }
  def unbind(): Unit = glBindBuffer(bufferType.id, 0)
  def setBuffer(buffer: FloatBuffer): Vbo = { glBufferSubData(GL_ARRAY_BUFFER, 0, buffer); this }

  def updateData(buffer: FloatBuffer): Unit = bind().setBuffer(buffer).unbind()
}

object Vbo {
  def create(bufferType: BufferType.Value, drawType: BufferDrawType.Value, buffer: FloatBuffer): Vbo =
    new Vbo(bufferType, drawType).bind().prepareData(buffer)

  def create(bufferType: BufferType.Value, drawType: BufferDrawType.Value, values: Seq[Float]): Vbo =
    create(bufferType, drawType, prepareBuffer(values.toArray))

  def create(ibuffer: IntBuffer): Vbo = new Vbo(BufferType.Index, BufferDrawType.Static).bind().prepareData(ibuffer)

  def prepareBuffer(v: Array[Int]): IntBuffer = {
    val buffer = BufferUtils.createIntBuffer(v.length)
    buffer.put(v).flip()
    buffer
  }
  def prepareBuffer(v: Array[Float]): FloatBuffer = {
    val buffer = BufferUtils.createFloatBuffer(v.length)
    buffer.put(v).flip()
    buffer
  }
  def prepareBuffer(a: Array[Vertex]): FloatBuffer = {
    val v = a.flatMap(vs => vs.elems.flatMap(v => v.values))
    val buffer = BufferUtils.createFloatBuffer(v.length)
    buffer.put(v).flip()
    buffer
  }

  def clear(vbo: Vbo): Unit = glDeleteBuffers(vbo.id)
}
