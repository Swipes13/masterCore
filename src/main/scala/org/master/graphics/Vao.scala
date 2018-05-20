package org.master.graphics

import java.nio.{ByteBuffer, IntBuffer}

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._

object DrawType extends Enumeration {
  type DrawType = Value
  val None = 0
  val LineLoop: DrawType.Value = Value(GL_LINE_LOOP)
  val Triangles: DrawType.Value = Value(GL_TRIANGLE_STRIP)
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

class IVao(drawType: DrawType.Value, vertexCount: Int, length: Int) extends Vao(drawType, vertexCount, length) {
  override def draw(): Vao = { glDrawElements(drawType.id, vertexCount, GL_UNSIGNED_INT, 0); this }
}

object Vao {
  def create(drawType: DrawType.Value, vertexCount: Int, fbs: Array[(Array[Float], Int)], indexes: Array[Int] = null): Vao = {
    val vao = (if (indexes == null) new Vao(drawType, vertexCount, fbs.length) else new IVao(drawType, vertexCount, fbs.length)).bind()
    vao.vbos = fbs.zipWithIndex.map { case ((floats, count), index) =>
      val vbo = Vbo.create(BufferType.Vertex, floats)
      glVertexAttribPointer(index, count, GL_FLOAT, false, 0, 0)
      glEnableVertexAttribArray(index)
      vbo
    } ++ createIVbo(indexes).toList
    vao
  }

  def createInterleaved(drawType: DrawType.Value, vertexes: Array[Vertex], indexes: Array[Int] = null): Vao = {
    val vao = (if (indexes == null) new Vao(drawType, vertexes.length, Vertex.elemCount(vertexes)) else new IVao(drawType, indexes.length, Vertex.elemCount(vertexes))).bind()
    val floats = vertexes.flatMap(vs => vs.elems.flatMap(v => v.values))
    val vbo = Vbo.create(BufferType.Vertex, floats)

    val v = vertexes.head
    v.elems.zipWithIndex.foreach { case (elem, i) => glVertexAttribPointer(i, elem.count, GL_FLOAT, false, v.stride, elem.offset) }
    vao.vbos = Array(vbo) ++ createIVbo(indexes).toList
    vao
  }

  private def createIVbo(indexes: Array[Int]) = Option(indexes).map(i => Vbo.create(Vbo.prepareBuffer(i)))
  def render(vao: Vao): Unit = vao.render()
  def clear(vao: Vao): Unit = {
    vao.vbos.foreach(Vbo.clear)
    glDeleteVertexArrays(vao.id)
  }
}
