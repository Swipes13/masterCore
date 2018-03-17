package org.master.graphics

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._

object DrawType extends Enumeration {
  type DrawType = Value
  val None = 0
  val LineLoop: DrawType.Value = Value(GL_LINE_LOOP)
  val Triangles: DrawType.Value = Value(GL_TRIANGLES)
}

class Vao(val drawType: DrawType.Value, val vertexCount: Int, val length: Int) {
  val id: Int = glGenVertexArrays
  var vbos = Array.empty[Vbo]

  def render(): Unit = {
    bind().enableAttributes()
    vbos.foreach(v => v.bind())
    draw()
  }
  def enableAttributes(): Vao = { (0 until length).foreach(i => glEnableVertexAttribArray(i)); this }
  def bind(): Vao = { glBindVertexArray(id); this }
  def draw(): Vao = { glDrawArrays(drawType.id, 0, vertexCount); this }
}

object Vao {
  def create(drawType: DrawType.Value, vertexCount: Int, fbs: (Array[Float], Int)*): Vao = {
    val vao = new Vao(drawType, vertexCount, fbs.length).bind()
    vao.vbos = fbs.zipWithIndex.map { case ((floats, count), index) =>
      val vbo = Vbo.create(floats)
      glVertexAttribPointer(index, count, GL_FLOAT, false, 0, 0)
      glEnableVertexAttribArray(index)
      vbo
    }.toArray
    vao
  }
  def render(vao: Vao): Unit = vao.render()
  def clear(vao: Vao): Unit = {
    vao.vbos.foreach(Vbo.clear)
    glDeleteVertexArrays(vao.id)
  }
}
