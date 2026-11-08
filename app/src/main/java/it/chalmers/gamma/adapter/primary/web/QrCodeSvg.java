package it.chalmers.gamma.adapter.primary.web;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.util.Map;

/**
 * Draws a QR code, made by ZXing, as inline SVG. Inline, since the content security policy doesn't
 * allow images from data URLs.
 */
public final class QrCodeSvg {

  private QrCodeSvg() {}

  public static String of(String text, String label) {
    BitMatrix matrix;
    try {
      matrix =
          new QRCodeWriter()
              .encode(
                  text,
                  BarcodeFormat.QR_CODE,
                  0,
                  0,
                  Map.of(
                      EncodeHintType.MARGIN,
                      4,
                      EncodeHintType.ERROR_CORRECTION,
                      ErrorCorrectionLevel.M));
    } catch (WriterException e) {
      throw new IllegalStateException("Could not create the QR code", e);
    }

    StringBuilder path = new StringBuilder();
    for (int y = 0; y < matrix.getHeight(); y++) {
      for (int x = 0; x < matrix.getWidth(); x++) {
        if (matrix.get(x, y)) {
          path.append('M').append(x).append(' ').append(y).append("h1v1h-1z");
        }
      }
    }

    return "<svg xmlns=\"http://www.w3.org/2000/svg\" class=\"qr-code\" viewBox=\"0 0 "
        + matrix.getWidth()
        + " "
        + matrix.getHeight()
        + "\" role=\"img\" aria-label=\""
        + label.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;")
        + "\" shape-rendering=\"crispEdges\"><rect width=\"100%\" height=\"100%\" fill=\"#fff\"/>"
        + "<path fill=\"#000\" d=\""
        + path
        + "\"/></svg>";
  }
}
