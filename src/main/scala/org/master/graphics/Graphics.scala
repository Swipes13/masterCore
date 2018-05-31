package org.master.graphics

/**
  * Created by valentin on 27/02/2018.
  */

import java.awt.image.BufferedImage
import java.io.{File, IOException}

import org.joml.{Vector3f, Vector4f}
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

class Graphics extends CoreUnit {
  private var _shaderPrograms = Array.empty[ShaderProgram]
  private var _projMatrix = new Matrix4fU().withName("projectionMatrix")
  private val _camera = new Camera(); _camera.withName("viewMatrix")
  private val _testRed = new Float1U(0.5f); _testRed.withName("red")
  private val _testLight = new Float3U(0, 0, -1); _testLight.withName("lightDir")
  private val _testView = new Float3U(0, 0, -1); _testView.withName("viewDir")
  private var _mainFrameBuffer: FrameBuffer = _

  private var _testGui: Gui = _

  private val _scene = new Scene(); _scene.node = Node.create()

  private var _textures = Array.empty[Texture2D]
  override def init(): Boolean = core.Utils.logging() {
    GL.createCapabilities
    GLUtil.setupDebugMessageCallback
//    glViewport(0, 0, Window.size.width * 2, Window.size.height * 2)
//    glShadeModel(GL_SMOOTH)
    glClearDepth(1.0f)
    glEnable(GL_DEPTH_TEST)
    glEnable(GL_STENCIL_TEST)
    glDepthFunc(GL_LEQUAL)
//    glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST)
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

    prepareShaders()
    addTextures()
    initTestScene()

    _testGui = new Gui()
    _testGui.init()

    Window.context.updateGlfwWindow()

    _mainFrameBuffer = FrameBuffer.create(0, GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT | GL_STENCIL_BUFFER_BIT, new Vector4f(1, 1, 1, 1),Window.context.getWindowSize.x * 2, Window.context.getWindowSize.y * 2)

    true
  }

  def initTestScene(): Unit = {
//    l.node = Node.create()
//    l.node.addComponent(l)
//    l.string = "test"
    _scene.node.addComponent(_scene)
//    _scene.node.addChild(l.node)

  }

  def prepareShaders(): Unit = {
    val b = scala.collection.mutable.ListBuffer.empty[ShaderProgram]
    val colorSp = ShaderProgram.create(Array(
       Shader.create("shaders/v_cookTorrance.glsl", ShaderType.Vertex),
       Shader.create("shaders/f_cookTorrance.glsl", ShaderType.Fragment)
    ))
    addVaosToProgram(colorSp)
    b += colorSp
    _shaderPrograms = b.toArray
  }

  def addTextures(): Unit = {
    val b = scala.collection.mutable.ListBuffer.empty[Texture2D]
    b += Texture2D.create("textures/Star.png", TextureLocation._1)
    _textures = b.toArray
  }

  def addVaosToProgram(program: ShaderProgram): Unit = {
    val lineP = (Array(-1f, -1, 0,  1, -1, 1), 2)
    val lineC = (Array(1f, 1, 0, 0, 0, 1, 0, 1, 1), 3)
//    program.addVao(Vao.create(DrawType.LineLoop, 3, Array(lineP, lineC)))
    _projMatrix = Matrix4fU.perspective(60, 0.01f, 100).withName("projectionMatrix")

    program.updateLocForU(this._projMatrix)
    program.updateLocForU(this._camera)
    program.updateLocForU(this._testRed)
    program.updateLocForU(this._testView)
    program.updateLocForU(this._testLight)

    program.uniformLocs.foreach(println)

    val vertexes = Array(
      new Vertex(VertexElement(-1, -1), VertexElement(0, 0, 1), VertexElement(0, 0)),
      new Vertex(VertexElement(1, -1),  VertexElement(0, 0, 1), VertexElement(1, 0)),
      new Vertex(VertexElement(0, 1),   VertexElement(0, 0, 1), VertexElement(0, 1)),
      new Vertex(VertexElement(1, 1),   VertexElement(0, 0, 1), VertexElement(1, 1))
    )
//    program.addVao(Vao.createInterleaved(DrawType.Triangles, vertexes, Array[Int](0, 1, 2, 2, 1, 3)))
//    QuasarClient.init()
    val mesh = Mesh.fromFile("mesh/test2.mesh")
    mesh.vao.foreach { case (physicalIndex, vao) =>
      if (physicalIndex == 1) program.addVao(vao)
    }

    mesh.nodes
  }

  override def update(dt: Double): Unit = {
    _testGui.frameBuffer().draw(myRender)
    _mainFrameBuffer.draw(() => {
      _testGui.update(dt)
    })
  }

  private def myRender(): Unit = {
    glEnable(GL_DEPTH_TEST)
    glEnable(GL_STENCIL_TEST)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    glEnable(GL_CULL_FACE)
    glCullFace(GL_BACK)

    _shaderPrograms.foreach { p =>
      _projMatrix.set()
      //      _testLight.update(_camera.look.negate(new Vector3f())).set()
      _testView.update(_camera.look.negate(new Vector3f())).set()
      _camera.updateWithRender()
      p.render()
    }
  }
  def nvgRender(): Unit = {
    import org.lwjgl.nanovg.NVGColor
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    nvgBeginFrame(_testGui.nvgContext, _testGui.frameBuffer().width, _testGui.frameBuffer().height, 1)

    val nvgColorOne: NVGColor = NVGColor.calloc
    val nvgColorTwo: NVGColor = NVGColor.calloc

    nvgColorOne.r(0)
    nvgColorOne.g(1)
    nvgColorOne.b(0)
    nvgColorOne.a(1)

    nvgColorTwo.r(0)
    nvgColorTwo.g(0)
    nvgColorTwo.b(0)
    nvgColorTwo.a(1)

    nvgTranslate(_testGui.nvgContext, _testGui.frameBuffer().width / 2f, _testGui.frameBuffer().height / 2f)
    nvgRotate(_testGui.nvgContext, 5)

    nvgBeginPath(_testGui.nvgContext)
    nvgRect(_testGui.nvgContext, -_testGui.frameBuffer().width / 4f, -_testGui.frameBuffer().height / 4f, _testGui.frameBuffer().width / 2f, _testGui.frameBuffer().height / 2f)
    nvgStrokeColor(_testGui.nvgContext, nvgColorTwo)
    nvgStroke(_testGui.nvgContext)

    nvgBeginPath(_testGui.nvgContext)
    nvgRect(_testGui.nvgContext, -_testGui.frameBuffer().width / 4f, -_testGui.frameBuffer().height / 4f, _testGui.frameBuffer().width / 2f, _testGui.frameBuffer().height / 2f)
    nvgFillColor(_testGui.nvgContext, nvgColorOne)
    nvgFill(_testGui.nvgContext)

    nvgColorOne.free()
    nvgColorTwo.free()

    nvgEndFrame(_testGui.nvgContext)
  }

  def saveRtToFile(fileName: String): Boolean = {
    GL11.glReadBuffer(GL11.GL_FRONT)
    val width = Window.size.width
    val height = Window.size.height
    val bpp = 4
    // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
    val buffer = BufferUtils.createByteBuffer(width * height * bpp)
    GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer)

    val file = new File(fileName)
    val format = "PNG"
    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    for(x <- 0 until width) {
      for(y <- 0 until height) {
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
    _shaderPrograms.foreach(ShaderProgram.clear)
    _testGui.destroy()
  }

}

object Graphics extends Graphics() {
  def clearFrameBuffer(flags: Int, color: Vector4f): Unit = {
    glClearColor(color.x, color.y, color.y, color.z)
    glClear(flags)
  }
}

