package org.master.graphics

/**
  * Created by valentin on 27/02/2018.
  */

import java.awt.image.BufferedImage
import java.io.File

import org.joml.{Vector2f, Vector3f, Vector4f}
import org.lwjgl.opengl.{GL, GLUtil}
import org.master.core.{CoreUnit, Window}
import org.master.core
import org.master.graphics.scene.{Node, Scene}
import org.master.graphics.ui.Gui
import org.master.http_client.QuasarClient
import org.lwjgl.opengl.GL11._
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.nanovg.NanoVG._
import javax.imageio.ImageIO

import scala.collection.mutable.ArrayBuffer

class Graphics extends CoreUnit {
  private var _shaderPrograms = Map.empty[ShaderProgramType.Value, ShaderProgram]
  private var _textures = Array.empty[Texture2D]
  private var _mainFrameBuffer: FrameBuffer = _
  private var _lights = ArrayBuffer.empty[Light]
  private var _rtRenderFuncs = ArrayBuffer.empty[(RTContext, (Double, RTContext) => Unit)]

  private var _gui: Gui = _

  override def init(): Boolean = core.Utils.logging() {
    GLUtil.setupDebugMessageCallback
//    glShadeModel(GL_SMOOTH)
    glClearDepth(1.0f)
    glEnable(GL_DEPTH_TEST)
    glEnable(GL_STENCIL_TEST)
    glDepthFunc(GL_LEQUAL)
//    glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST)
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

    prepareShaderPrograms()
    addTextures()

    _gui = new Gui().init()

    _mainFrameBuffer = new FrameBuffer(0, Window.innerSize.width * 2, Window.innerSize.height * 2)
      .setFlags(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
      .setColor(new Vector4f(1, 1, 1, 1))

    _shaderPrograms.foreach(s => initGuiRts(s._2))

    //    QuasarClient.init()

    true
  }

  def prepareShaderPrograms(): Unit = {
    val cookTorranceForward = ShaderProgram.create(Array(
       Shader.create("shaders/v_cookTorrance.glsl", ShaderType.Vertex),
       Shader.create("shaders/f_cookTorrance.glsl", ShaderType.Fragment)
    ))

    val mesh = Mesh.fromFile("mesh/test2.mesh")
    mesh.vao.foreach { case (physicalIndex, vao) =>
      if (physicalIndex == 1) cookTorranceForward.addVao(vao)
    }

    _shaderPrograms += ShaderProgramType.CookTorranceForward -> cookTorranceForward
  }

  def addTextures(): Unit = {
    val b = scala.collection.mutable.ListBuffer.empty[Texture2D]
    b += Texture2D.create("textures/Star.png", TextureLocation._1)
    _textures = b.toArray
  }

  def initGuiRts(program: ShaderProgram): Unit = {
    val uniforms = Array(
      (_lights += new Light(new Vector3f(0, 0, -1))).last.withName("lightDir")
    )

    val cameras = Array(
      new Camera("viewMatrix").withPosition(new Vector3f(0, 0, -1)),
      new Camera("viewMatrix").withPosition(new Vector3f(0, 0, -2)),
      new Camera("viewMatrix").withPosition(new Vector3f(0, 0, -3)),
      new Camera("viewMatrix").withPosition(new Vector3f(0, 0, -4))
    )

    val (near, far) = (0.01f, 100)
    _gui.rtContexts.zipWithIndex.foreach { case(context, i) =>
      val perspective = i == 3
      val pm = if (perspective) Matrix4fU.perspective(context.size.x, context.size.y, 60, 0.01f, 100)
      else new OrthoProjectionMatrix(new Vector2f(context.size.x, context.size.y), near, far, 0.05f)

      pm.withName("projectionMatrix")
      context.setCamera(cameras(i))
      context.setProjectionMatrix(pm)
      uniforms.foreach(context.addUniform)
      _rtRenderFuncs += (context, (delta, context) => {
        if (perspective) { // perspective
          val p = _shaderPrograms(ShaderProgramType.CookTorranceForward).use()
          context.updateShaderProgramLocations(p)
          // my code sets to shaders
          p.render() // render all vaos ? correctly or not?
        } else {
          val p = _shaderPrograms(ShaderProgramType.CookTorranceForward).use()
          context.updateShaderProgramLocations(p)
          p.render()
        }
      })
    }
  }

  override def update(dt: Double): Unit = {
    _rtRenderFuncs.foreach { case (context, renderFunction) =>
      context.render(dt, renderFunction)
    }
    glEnable(GL_DEPTH_TEST)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
//    _shaderPrograms.foreach { p => p.use()
//      //      _testLight.update(_camera.look.negate(new Vector3f())).set()
//      //      _testView.update(_camera.look.negate(new Vector3f())).set()
//
//    }

    _mainFrameBuffer.draw(_gui.update)
  }

  def saveRtToFile(fileName: String): Boolean = {
    GL11.glReadBuffer(GL11.GL_FRONT)
    val width = Window.innerSize.width
    val height = Window.innerSize.height
    val bpp = 4
    // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
    val buffer = BufferUtils.createByteBuffer(width * height * bpp)
    GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer)

    val file = new File(fileName)
    val format = "PNG"
    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    for (x <- 0 until width) {
      for (y <- 0 until height) {
        val i = (x + (width * y)) * bpp
        val r = buffer.get(i) & 0xFF
        val g = buffer.get(i + 1) & 0xFF
        val b = buffer.get(i + 2) & 0xFF
        image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b)
      }
    }

    ImageIO.write(image, format, file)
  }

  override def destroy(): Unit = {
    ShaderProgram.clear()
    _shaderPrograms.foreach(s => ShaderProgram.clear(s._2))
    _gui.destroy()
  }

}

object Graphics extends Graphics() {
  def clearFrameBuffer(flags: Int, color: Vector4f): Unit = {
    glClearColor(color.x, color.y, color.y, color.z)
    glClear(flags)
  }
}

