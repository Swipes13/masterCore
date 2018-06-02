package org.master.graphics

import org.joml.Vector4f
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL32._

class FrameBuffer(val id: Int, val width: Int, val height: Int) {
  var texture: Texture = _
  var depthBuffer: RenderBuffer = _
  var renderFlags = 0
  var clearColor = new Vector4f()

  def bind(): FrameBuffer = { glBindFramebuffer(GL_FRAMEBUFFER, id); this }
  def draw(drawFunction: () => Unit): Unit = {
    FrameBuffer.preRender(this)
    drawFunction()
    unbind()
  }

  def unbind(): Unit = glBindFramebuffer(GL_FRAMEBUFFER, 0)
  def setColor(color: Vector4f): FrameBuffer = { clearColor = color; this }
  def setFlags(flags: Int): FrameBuffer = { renderFlags = flags; this }
}

object FrameBuffer {
  def create(width: Int, height: Int): FrameBuffer = {
    val frameBuffer = new FrameBuffer(glGenFramebuffers, width, height).bind()
      .setFlags(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT)
      .setColor(new Vector4f(0.05f, 0.45f, 0.45f, 1.0f))
    frameBuffer.texture = Texture.create(width, height)
    frameBuffer.depthBuffer = RenderBuffer.create(width, height)

    glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, frameBuffer.texture.id, 0)
    glDrawBuffer(GL_COLOR_ATTACHMENT0)
    frameBuffer.unbind()
    frameBuffer
  }
  def preRender(fb: FrameBuffer): Unit = {
    fb.bind()
    glViewport(0, 0, fb.width, fb.height)
    Graphics.clearFrameBuffer(fb.renderFlags, fb.clearColor)
  }
  def unbind(fb: FrameBuffer): Unit = glBindFramebuffer(GL_FRAMEBUFFER, 0)
  def destroy(fb: FrameBuffer): Unit = {
    glDeleteRenderbuffers(fb.depthBuffer.id)
    glDeleteTextures(fb.texture.id)
    glDeleteFramebuffers(fb.id)
  }
}
