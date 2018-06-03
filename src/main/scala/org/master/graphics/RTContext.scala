package org.master.graphics

import org.joml.Vector2i
import org.master.input.{Input, KeyType, MousePos}

import scala.collection.mutable.ArrayBuffer

class RTContext(val size: Vector2i) {
  var hasFocus = false

  Input.addKeyPressCb(KeyType.W, () => zoom(-1))
  Input.addKeyPressCb(KeyType.S, () => zoom(1))
  Input.addKeyPressCb(KeyType.A, () => if (hasFocus) _camera.foreach(_.strafe(-1)))
  Input.addKeyPressCb(KeyType.D, () => if (hasFocus) _camera.foreach(_.strafe(1)))
  Input.addKeyPressCb(KeyType.LShift,   () => speedZoom(5))
  Input.addKeyReleaseCb(KeyType.LShift, () => speedZoom(1))
  Input.mouse.addPositionCb((pos: MousePos) => if (hasFocus) {
    _camera.foreach(_.pitch(pos.y / 100))
    _camera.foreach(_.rotY(pos.x / 100))
  })

  private val _frameBuffer: FrameBuffer = FrameBuffer.create(size.x, size.y)
  private var _camera: Option[Camera] = None
  private var _projectionMatrix: Option[ProjectionMatrix] = None
  private var _uniforms: ArrayBuffer[Uniform] = ArrayBuffer.empty[Uniform]
  private var _drawFunction: Option[(Double) => Unit] = None
  private var _zoomCf = 1.0f

  def frameBuffer: FrameBuffer = _frameBuffer

  def render(dt: Double, renderFunction: (Double, RTContext) => Unit): Unit = {
    preRender()
    renderFunction(dt, this)
    _drawFunction.foreach(_(dt))
    postRender()
  }

  def addUniform(uniform: Uniform): Unit = _uniforms += uniform
  def setProjectionMatrix(projectionMatrix: ProjectionMatrix): Unit = {
    _projectionMatrix = Some(projectionMatrix)
    addUniform(projectionMatrix)
  }
  def setCamera(camera: Camera): Unit = {
    _camera = Some(camera)
    addUniform(camera)
    addUniform(camera.view)
  }
  def updateShaderProgramLocations(program: ShaderProgram): Unit = {
    _uniforms.foreach(program.updateLocationForUniform)
    _uniforms.foreach(Uniform.set)
  }

  private def preRender(): Unit = {
    FrameBuffer.preRender(_frameBuffer)
    _camera.foreach(_.update())
  }
  private def postRender(): Unit = FrameBuffer.unbind(_frameBuffer)

  private def zoom(sign: Int): Unit = if (hasFocus) _projectionMatrix match {
    case Some(pm) if pm.`type` == ProjectionType.Perspective => _camera.foreach(_.walk(sign))
    case Some(pm) if pm.`type` == ProjectionType.Orthographical =>
      pm.asInstanceOf[OrthoProjectionMatrix].zoom(Math.pow(if (sign > 0) 1.01f else 0.99f, _zoomCf).toFloat)
    case _ =>
  }
  private def speedZoom(cf: Float): Unit = if (hasFocus) _projectionMatrix match {
    case Some(pm) if pm.`type` == ProjectionType.Perspective => _camera.foreach(_.speed = cf)
    case Some(pm) if pm.`type` == ProjectionType.Orthographical => _zoomCf = cf
    case _ =>
  }
}

object RTContext {
  def destroy(context: RTContext): Unit = {
    FrameBuffer.destroy(context._frameBuffer)
  }
}