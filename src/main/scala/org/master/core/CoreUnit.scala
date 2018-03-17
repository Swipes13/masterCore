package org.master.core

abstract class CoreUnit {
  def init(): Boolean
  def destroy(): Unit = {}
  def update(dt: Double): Unit = {}
}

object CoreUnit {
  def init(unit: CoreUnit): Unit = unit.init()
  def destroy(unit: CoreUnit): Unit = unit.destroy()
}