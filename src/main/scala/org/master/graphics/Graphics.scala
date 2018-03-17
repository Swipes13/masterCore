package org.master.graphics

/**
  * Created by valentin on 27/02/2018.
  */

import org.lwjgl.opengl.{GL, GLUtil}
import org.lwjgl.opengl.GL11._
import org.master.core.{CoreUnit, Utils, Window}

class Graphics extends CoreUnit {
  private var _shaderPrograms = Array.empty[ShaderProgram]
  private var _vaos = Array.empty[Vao]

  override def init(): Boolean = Utils.logging() {
    GL.createCapabilities
    GLUtil.setupDebugMessageCallback
    glClearColor(0.05f, 0.45f, 0.45f, 0.0f)
    glViewport(0, 0, Window.size.width, Window.size.height)
//    glMatrixMode(GL_PROJECTION)
//    glLoadIdentity()
//    GLU.gluPerspective(45.0f, ((float)w/(float)h),0.1f,100.0f)
//    glMatrixMode(GL_MODELVIEW)
//    glLoadIdentity()
//    glShadeModel(GL_SMOOTH)
//    glClearDepth(1.0f)
    glDisable(GL_DEPTH_TEST)
//    glDepthFunc(GL_LEQUAL)
//    glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST)
    prepareShaders()
    prepareVaos()
    true
  }

  def prepareShaders(): Unit = {
    val b = scala.collection.mutable.ListBuffer.empty[ShaderProgram]
     b += ShaderProgram.create(Array(
       Shader.create("shaders/simplev.glsl", ShaderType.Vertex),
       Shader.create("shaders/simplef.glsl", ShaderType.Fragment)
     ), Array("vpos, vcolor"))
    _shaderPrograms = b.toArray
  }

  def prepareVaos(): Unit = {
    val b = scala.collection.mutable.ListBuffer.empty[Vao]
    val positions = (Array(-1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f,  1.0f, 0.0f), 3)
    val colors = (Array(0f, 0f, 1f, 1f, 0f, 0f, 0f, 1f, 0f), 3)
    b += Vao.create(DrawType.Triangles, 3, positions, colors)
    _vaos = b.toArray
  }

  override def update(dt: Double): Unit = {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    _shaderPrograms.foreach(p => {
      p.use()
      _vaos.foreach(Vao.render)
    })
  }

  override def destroy(): Unit = {
    ShaderProgram.clear()
    _vaos.foreach(Vao.clear)
    _shaderPrograms.foreach(ShaderProgram.clear)
  }
}

object Graphics extends Graphics() {}

