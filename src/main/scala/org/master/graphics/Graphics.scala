package org.master.graphics

/**
  * Created by valentin on 27/02/2018.
  */

import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11._
import org.master.core.CoreUnit
import org.master.window.Window

class Graphics extends CoreUnit {
  override def update(dt: Double): Unit = {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    println(s"delta time $dt")
  }

  override def init(): Boolean = {
    GL.createCapabilities
    glClearColor(0.05f, 0.45f, 0.45f, 0.0f)

    glViewport(0, 0, Window.size.width, Window.size.height)
    //    glMatrixMode(GL_PROJECTION)
    //    glLoadIdentity()
    //    GLU.gluPerspective(45.0f, ((float)w/(float)h),0.1f,100.0f)
    //    glMatrixMode(GL_MODELVIEW)
    //    glLoadIdentity()
    //    glShadeModel(GL_SMOOTH)
    //    glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
    //    glClearDepth(1.0f)
    //    glEnable(GL_DEPTH_TEST)
    //    glDepthFunc(GL_LEQUAL)
    //    glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST)
    true
  }
}

object Graphics extends Graphics() {}