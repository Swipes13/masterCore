package org.master.graphics

import java.io.{File, PrintWriter}
import java.nio.{ByteBuffer, IntBuffer}

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.master.core.{Utils => CUtils}

import scala.io.Source

object DrawType extends Enumeration {
  type DrawType = Value
  val None = 0
  val Points: DrawType.Value = Value(GL_POINTS)
  val Lines: DrawType.Value = Value(GL_LINES)
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

class IVao(drawType: DrawType.Value, vertexCount: Int, length: Int) extends Vao(drawType, vertexCount, length) {
  override def draw(): Vao = { glDrawElements(drawType.id, vertexCount, GL_UNSIGNED_INT, 0); this }
}

object Vao {
  def fromFile(fileName: String): Vao = {
    val linesFromFile = Source.fromFile(fileName).getLines().toList
    val vertexCount = linesFromFile.head.toInt
    val (vertexes, indexesWithCount) = linesFromFile.tail.splitAt(vertexCount)
    val (indexCount, indexes) = (indexesWithCount.head, indexesWithCount.tail)

    val v = vertexes.map(Vertex.fromString)
    val i = indexes.flatten(_.split(' ').map(_.toInt))

    Vao.createInterleaved(DrawType.Triangles, v, i)
  }
  def create(drawType: DrawType.Value, vertexCount: Int, fbs: Array[(Array[Float], Int)], indexes: Array[Int] = null): Vao = {
    val vao = (if (indexes == null) new Vao(drawType, vertexCount, fbs.length) else new IVao(drawType, vertexCount, fbs.length)).bind()
    vao.vbos = fbs.zipWithIndex.map { case ((floats, count), index) =>
      val vbo = Vbo.create(BufferType.Vertex, BufferDrawType.Static, floats)
      glVertexAttribPointer(index, count, GL_FLOAT, false, 0, 0)
      glEnableVertexAttribArray(index)
      vbo
    } ++ createIVbo(indexes).toList
    vao
  }
  def createForQuasar(vertexCount: Int, fbs: Array[(Array[Float], Int)], indexes: Array[Int] = null): Vao = {
    val vao = new Vao(DrawType.Points, vertexCount, fbs.length).bind()
    vao.vbos = fbs.zipWithIndex.map { case ((floats, count), index) =>
      val vbo = Vbo.create(BufferType.Vertex, BufferDrawType.Static, floats)
      glVertexAttribPointer(index, count, GL_FLOAT, false, 0, 0)
      glEnableVertexAttribArray(index)
      vbo
    } ++ createIVbo(indexes).toList
    vao
  }

  def createInterleaved(drawType: DrawType.Value, vertexes: Seq[Vertex], indexes: Seq[Int] = null): Vao = {
    val vao = (if (indexes == null) new Vao(drawType, vertexes.length, Vertex.elemCount(vertexes)) else new IVao(drawType, indexes.length, Vertex.elemCount(vertexes))).bind()
    val floats = vertexes.flatMap(vs => vs.elems.flatMap(v => v.values))
    val vbo = Vbo.create(BufferType.Vertex, BufferDrawType.Static, floats)

    val v = vertexes.head
    v.elems.zipWithIndex.foreach { case (elem, i) => glVertexAttribPointer(i, elem.count, GL_FLOAT, false, v.stride, elem.offset) }
    vao.vbos = Array(vbo) ++ createIVbo(indexes).toList
    vao
  }

  def createInterleavedWithBuffer(drawType: DrawType.Value, bufferDrawType: BufferDrawType.Value, vertexes: Seq[Vertex]): Vao = {
    val vao = new Vao(drawType, vertexes.length, Vertex.elemCount(vertexes)).bind()
    val floats = vertexes.flatMap(vs => vs.elems.flatMap(v => v.values))
    val vbo = Vbo.create(BufferType.Vertex, BufferDrawType.Static, floats)

    val v = vertexes.head
    v.elems.zipWithIndex.foreach { case (elem, i) => glVertexAttribPointer(i, elem.count, GL_FLOAT, false, v.stride, elem.offset) }
    vao.vbos = Array(vbo)
    vao
  }

  private def createIVbo(indexes: Seq[Int]) = Option(indexes).map(i => Vbo.create(Vbo.prepareBuffer(i.toArray)))
  def render(vao: Vao): Unit = vao.render()
  def clear(vao: Vao): Unit = {
    vao.vbos.foreach(Vbo.clear)
    glDeleteVertexArrays(vao.id)
  }
  def saveToFile(fileName: String, vertexes: Array[Vertex], indexes: Array[Int] = null): Unit = {
    val writer = new PrintWriter(new File(s"vaos/$fileName.vao"))
    writer.write(s"${vertexes.length}\n")
    writer.write(CUtils.arrayToStrWithDelim(vertexes, '\n'))
    writer.write(s"\n${indexes.length}\n")
    writer.write(CUtils.arrayToStrWithDelim(indexes.sliding(3, 3).toArray, '\n', (innerI: Array[Int]) => CUtils.arrayToStrWithDelim(innerI, ' ')))
    writer.close()
  }
}
