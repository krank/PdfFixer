package com.urverkspel.app;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDStream;

public class PDFTokens {

  public static final List<String> gfxContextOperators = Collections.unmodifiableList(
      new ArrayList<String>(
          List.of(
              OperatorName.STROKING_COLORSPACE, // CS
              OperatorName.NON_STROKING_COLORSPACE, // cs
              OperatorName.SET_LINE_DASHPATTERN, // d
              OperatorName.TYPE3_D0, // d0
              OperatorName.TYPE3_D1, // d1
              OperatorName.STROKING_COLOR_GRAY, // G
              OperatorName.NON_STROKING_GRAY, // g
              OperatorName.SET_GRAPHICS_STATE_PARAMS, // gs
              OperatorName.SET_LINE_JOINSTYLE, // j
              OperatorName.SET_LINE_CAPSTYLE, // J
              OperatorName.STROKING_COLOR_CMYK, // K
              OperatorName.NON_STROKING_CMYK, // k
              OperatorName.SET_LINE_MITERLIMIT, // M
              OperatorName.SAVE, // q
              OperatorName.RESTORE, // Q
              OperatorName.STROKING_COLOR_RGB, // RG
              OperatorName.NON_STROKING_RGB, // rg
              OperatorName.SET_RENDERINGINTENT, // ri
              OperatorName.STROKING_COLOR, // SC
              OperatorName.NON_STROKING_COLOR, // sc
              // OperatorName.STROKING_COLOR_N, // SCN
              // OperatorName.NON_STROKING_COLOR_N, // scn
              OperatorName.SET_LINE_WIDTH, // w
              OperatorName.CLIP_NON_ZERO, // W
              OperatorName.CLIP_EVEN_ODD, // W*

              OperatorName.SET_FONT_AND_SIZE, // Tf
              OperatorName.SET_CHAR_SPACING, // Tc
              OperatorName.SET_WORD_SPACING // Tw
          )));

  static String makeTokenString(Object token) {
    if (token instanceof COSName)
      return ((COSName) token).getName();

    if (token instanceof COSInteger)
      return Integer.toString(((COSInteger) token).intValue());

    if (token instanceof Operator)
      return ((Operator) token).getName();

    if (token instanceof COSFloat)
      return Float.toString(((COSFloat) token).floatValue());

    if (token instanceof COSString)
      return ((COSString) token).getString();

    if (token instanceof COSArray) {
      List<? extends COSBase> list = ((COSArray) token).toList();
      return "[" + PDFTokens.makeLineString(list) + "]";
    }

    return token.toString();
  }

  static String makeLineString(List<? extends COSBase> tokens) {
    StringBuilder builder = new StringBuilder();
    for (COSBase token : tokens) {
      builder.append(makeTokenString(token));
      builder.append(" ");
    }

    return builder.toString().trim();
  }

  static PDStream makeStreamFromTokenList(List<Object> tokens, PDDocument document) throws IOException {
    PDStream newStream = new PDStream(document);
    OutputStream outputStream = newStream.createOutputStream(COSName.FLATE_DECODE);
    ContentStreamWriter writer = new ContentStreamWriter(outputStream);
    writer.writeTokens(tokens);
    outputStream.close();

    return newStream;
  }
}
