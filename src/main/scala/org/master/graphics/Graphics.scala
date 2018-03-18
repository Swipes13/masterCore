package org.master.graphics

/**
  * Created by valentin on 27/02/2018.
  */

import org.lwjgl.opengl.{GL, GLUtil}
import org.lwjgl.opengl.GL11._
import org.master.core.{CoreUnit, Utils, Window}

class Graphics extends CoreUnit {
  private var _shaderPrograms = Array.empty[ShaderProgram]

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
    true
  }

  def prepareShaders(): Unit = {
    val b = scala.collection.mutable.ListBuffer.empty[ShaderProgram]
    val colorSp = ShaderProgram.create(Array(
       Shader.create("shaders/simplev.glsl", ShaderType.Vertex),
       Shader.create("shaders/simplef.glsl", ShaderType.Fragment)
    ), Array("vpos, vcolor"))
    prepareVaos(colorSp)
    b += colorSp
    _shaderPrograms = b.toArray
  }

  def prepareVaos(program: ShaderProgram): Unit = {
    val positions = (Array(-1f, -1f, 0f, 1f, -1f, 0f, 0f,  1f, 0f, 1f, 1f, 0f), 3)
    val colors = (Array(0f, 0f, 1f, 1f, 0f, 0f, 0f, 1f, 0f, 1f, 1f, 0f), 3)
    program.addVao(Vao.create(DrawType.Triangles, 6, Array(positions, colors), Array(0, 1, 2, 2, 1, 3)))

    val lineP = (Array(-1f, -1f, 0f, 0f,  1f, 0f, -1f, 1f, 0f), 3)
    val lineC = (Array(1f, 1f, 0f, 0f, 0f, 1f, 0f, 1f, 1f), 3)
    program.addVao(Vao.create(DrawType.Triangles, 3, Array(lineP, lineC)))
  }

  override def update(dt: Double): Unit = {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    _shaderPrograms.foreach(ShaderProgram.render)
  }

  override def destroy(): Unit = {
    ShaderProgram.clear()
    _shaderPrograms.foreach(ShaderProgram.clear)
  }
}

object Graphics extends Graphics() {}

