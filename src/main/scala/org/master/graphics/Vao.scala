package org.master.graphics

import org.lwjgl.opengl.{GL11, GL15, GL20, GL30}

class Vao(val vertexCount: Int, val length: Int) {
  val id: Int = GL30.glGenVertexArrays

  def draw(): Unit = {
    bind()
    enableAttribs()
    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount)
  }

  def enableAttribs(): Unit = (0 until length).foreach(i => GL20.glEnableVertexAttribArray(i))
  def bind(): Unit = GL30.glBindVertexArray(id)
}

object Vao {
  val FLOAT_BYTES = java.lang.Float.SIZE   / java.lang.Byte.SIZE
  val INT_BYTES   = java.lang.Integer.SIZE / java.lang.Byte.SIZE
  val VEC4_BYTES  = 4 * FLOAT_BYTES

  def create(fbs: Array[FloatBuffer], vertexCount: Int): Vao = {
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)

    val vao = new Vao(vertexCount, fbs.length)
    vao.bind()
    vao.enableAttribs()
    fbs.zipWithIndex.foreach { case (fb, index) =>
      fb.bind()
      GL20.glVertexAttribPointer(index, fb.length, GL11.GL_FLOAT, false, VEC4_BYTES, 0)
    }
    GL30.glBindVertexArray(0)
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    vao
  }
}
