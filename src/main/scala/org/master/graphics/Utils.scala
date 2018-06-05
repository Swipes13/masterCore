package org.master.graphics

import de.matthiasmann.twl.utils.PNGDecoder
import de.matthiasmann.twl.utils.PNGDecoder.Format
import java.io.FileInputStream
import java.nio.ByteBuffer
import org.master.core.Size

case class BufferWithSize(buffer: ByteBuffer, size: Size)

object Utils {

  @throws[Exception]
  def loadPNGTexture(filename: String): BufferWithSize = {
    val in = new FileInputStream(filename)
    val decoder = new PNGDecoder(in)
    val tWidth = decoder.getWidth
    val tHeight = decoder.getHeight
    var buf = ByteBuffer.allocateDirect(4 * tWidth * tHeight)
    decoder.decode(buf, decoder.getWidth * 4, Format.RGBA)
    buf.flip
    in.close()
    BufferWithSize(buf, Size(tWidth, tHeight))
  }
}
