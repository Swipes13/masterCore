package org.master.graphics

import org.joml.{Matrix4f, Vector3f}
import org.master.input.{Input, KeyType, MouseButtonType, MousePos}

class Camera extends Matrix4fU {
  def this(name: String) = { this(); withName(name) }

  val view = new Float3U(0, 0, -1); view.withName("viewDir")
  var speed = 1.0f

  private var _look = new Vector3f(0, 0, -1)
  private var _pos = new Vector3f(0, 0, -1)
  private var _right = new Vector3f(1, 0, 0)
  private var _up = new Vector3f(0, 1, 0)

  private var _dirty = true

  def update(): Unit = dirtyCheck {
    _look.normalize()
    _look.cross(_right, _up).normalize()
    _up.cross(_look, _right).normalize()

    m00(_right.x); m10(_right.y); m20(_right.z); m30(-_pos.dot(_right))
    m01(_up.x);    m11(_up.y);    m21(_up.z);    m31(-_pos.dot(_up))
    m02(_look.x);  m12(_look.y);  m22(_look.z);  m32(-_pos.dot(_look))

    view.update(_look.negate(new Vector3f()).normalize(new Vector3f()))
  }

  def hasMouseFocus: Boolean = Input.mouse.buttons(MouseButtonType.Right).pushed

  def walk(sign: Float): Unit = dirty { _pos.add(_look.mul(sign * Camera.DeltaCf * speed, new Vector3f())) }
  def strafe(sign: Float): Unit = dirty { _pos.add(_right.mul(sign * Camera.DeltaCf * speed, new Vector3f())) }

  def pitch(delta: Double): Unit = if (hasMouseFocus) dirty {
    val r = new Matrix4f().rotation(delta.toFloat, _right)
    r.transformDirection(_up)
    r.transformDirection(_look)
  }
  def rotY(delta: Double): Unit = if (hasMouseFocus) dirty {
    val r = new Matrix4f().rotationY(delta.toFloat)
    r.transformDirection(_right)
    r.transformDirection(_up)
    r.transformDirection(_look)
  }
  def withPosition(pos: Vector3f): Camera = { _pos = pos; this }
  def topView(): Camera = {
    dirty { withSettings(p = new Vector3f(0, 2, 0),  l = new Vector3f(0, 1, 0), r = new Vector3f(-1, 0, 0), u = new Vector3f(0, 0, -1)) }
    this
  }
  def frontView(): Camera = {
    dirty { withSettings(p = new Vector3f(0, 0, -2), l = new Vector3f(0, 0, -1), r = new Vector3f(-1, 0, 0), u = new Vector3f(0, 1, 0)) }
    this
  }
  def rightView(): Camera = {
    dirty { withSettings(p = new Vector3f(-2, 0, 0), l = new Vector3f(-1, 0, 0), r = new Vector3f(0, 0, 1), u = new Vector3f(0, 1, 0)) }
    this
  }
  def withSettings(p: Vector3f, l: Vector3f, r: Vector3f, u: Vector3f = new Vector3f()): Camera = {
    dirty {
      _pos = p
      _look = l
      _right = r
      _up = u
    }
    this
  }
  def dirtyCheck(block: => Unit): Unit = if (_dirty) { block; _dirty = false }
  def dirty(block: => Unit): Unit = { block; _dirty = true }
}

object Camera {
  val DeltaCf = 0.01f
}
