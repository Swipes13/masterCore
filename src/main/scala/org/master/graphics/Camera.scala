package org.master.graphics

import org.joml.{Matrix4f, Vector3f}
import org.master.input.{Input, KeyType, MouseButtonType, MousePos}

class Camera extends Matrix4fU {
  def this(name: String) = {
    this()
    withName(name)
  }

  val deltaCf = 0.01f
  val view = new Float3U(0, 0, -1); view.withName("viewDir")

  var position = new Vector3f(0, 0, -1)
  var look = new Vector3f(0, 0, -1)
  var right = new Vector3f(1, 0, 0)
  var up = new Vector3f(0, 1, 0)
  var speed = 1.0f

  def update(): Unit = { // TODO: optimize with dirty flag
    look.normalize()
    look.cross(right, up).normalize()
    up.cross(look, right).normalize() // TODO: remove normalize and check

    m00(right.x); m10(right.y); m20(right.z); m30(-position.dot(right))
    m01(up.x);    m11(up.y);    m21(up.z);    m31(-position.dot(up))
    m02(look.x);  m12(look.y);  m22(look.z);  m32(-position.dot(look))

    view.update(look.negate(new Vector3f()))
  }

  def hasMouseFocus: Boolean = Input.mouse.buttons(MouseButtonType.Right).pushed

  def walk(sign: Float): Unit = position.add(look.mul(sign * deltaCf* speed, new Vector3f()))
  def strafe(sign: Float): Unit = position.add(right.mul(sign * deltaCf * speed, new Vector3f()))

  def pitch(delta: Double): Unit = if (hasMouseFocus) {
    val r = new Matrix4f().rotation(delta.toFloat, right)
    r.transformDirection(up)
    r.transformDirection(look)
  }
  def rotY(delta: Double): Unit = if (hasMouseFocus) {
    val r = new Matrix4f().rotationY(delta.toFloat)
    r.transformDirection(right)
    r.transformDirection(up)
    r.transformDirection(look)
  }
  def withPosition(pos: Vector3f): Camera = { position = pos; this }
}
