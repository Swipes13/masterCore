package org.master.graphics

import java.awt.image.BufferedImage

import com.xuggle.mediatool.IMediaWriter

import com.xuggle.mediatool.ToolFactory
import com.xuggle.xuggler.ICodec
import java.util.concurrent.TimeUnit

class ScreenRecorder(filename: String, width: Int, height: Int) {
  val writer: IMediaWriter = ToolFactory.makeWriter(filename)
  val resultAdded = writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, width, height)
  private var finished = false

  var sec = 0.0
  def addFrame(frame: BufferedImage, dt: Double): Unit = if (!finished) {
    sec += dt
    val s = (sec * 200).toInt
    writer.encodeVideo(0, frame, s, TimeUnit.MILLISECONDS)
  }

  def finish(): Unit = { writer.close(); finished = true }
  def update(dt: Double): Unit = Graphics.saveRtToRecorder(this, dt)
}

object ScreenRecorder {
  def create(filename: String, width: Int, height: Int): ScreenRecorder = new ScreenRecorder(filename, width, height)
}