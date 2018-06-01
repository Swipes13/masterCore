package org.master.graphics.ui

import org.lwjgl.nanovg.{NanoVGGL2, NanoVGGL3}
import org.lwjgl.opengl.GL11.glGetInteger
import org.lwjgl.opengl.GL30.{GL_MAJOR_VERSION, GL_MINOR_VERSION}

abstract class NanoVgContext {
  val id: Long
  def destroy(): Unit
}

object NanoVgContext {
  val flags: Int = NanoVGGL3.NVG_STENCIL_STROKES | NanoVGGL3.NVG_ANTIALIAS

  def create(): NanoVgContext = {
    val isNewVersion = (glGetInteger(GL_MAJOR_VERSION) > 3) || ((glGetInteger(GL_MAJOR_VERSION) == 3) && glGetInteger(GL_MINOR_VERSION) >= 2)
    if (isNewVersion) new NanoVgContext3()
    else new NanoVgContext2()
  }
}

class NanoVgContext2 extends NanoVgContext {
  override val id: Long = NanoVGGL2.nvgCreate(NanoVgContext.flags)
  override def destroy(): Unit = NanoVGGL2.nnvgDelete(id)
}

class NanoVgContext3 extends NanoVgContext {
  override val id: Long = NanoVGGL3.nvgCreate(NanoVgContext.flags)
  override def destroy(): Unit = NanoVGGL3.nnvgDelete(id)
}