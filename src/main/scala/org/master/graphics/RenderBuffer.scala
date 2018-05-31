package org.master.graphics

import org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT
import org.lwjgl.opengl.GL30._

class RenderBuffer {
  val id: Int = glGenRenderbuffers

  def bind(): RenderBuffer = { glBindRenderbuffer(GL_RENDERBUFFER, id); this }
}

object RenderBuffer {
  def create(width: Int, height: Int): RenderBuffer = {
    val renderBuffer = new RenderBuffer().bind()

    glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height)
    glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderBuffer.id)
    glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_STENCIL_ATTACHMENT, GL_RENDERBUFFER, renderBuffer.id)
    renderBuffer
  }
}
