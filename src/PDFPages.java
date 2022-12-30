import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
// import org.apache.pdfbox.pdmodel.PDResources;

public class PDFPages {

  public static void analyzePage(PDDocument document, int pageNum) throws Exception {

    PDPage page = document.getPage(pageNum);

    StringBuilder lineBuilder = new StringBuilder();
    StringBuilder pageBuilder = new StringBuilder();

    // PDResources resources = page.getResources();

    PDFStreamParser parser = new PDFStreamParser(page);
    List<Object> tokens = parser.parse();

    int operatorCount = 0;

    for (int i = 0; i < tokens.size(); i++) {
      Object currentToken = tokens.get(i);

      if (currentToken instanceof COSName) {
        lineBuilder.append("n" + ((COSName) currentToken).getName());

      } else if (currentToken instanceof Operator) {
        lineBuilder.insert(0, operatorCount + ": ");
        operatorCount++;
        lineBuilder.append(((Operator) currentToken).getName());

        // End of the line
        pageBuilder.append(lineBuilder + System.lineSeparator());
        lineBuilder = new StringBuilder();
      } else {
        lineBuilder.append(currentToken);
      }

      lineBuilder.append(" || ");

    }

    System.out.println(pageBuilder);

  }

  public static void deletePage(PDDocument document, int pageNum) {
    // STUB
  }

  public static void insertBlankPage(PDDocument document, int pageNum) {
    // STUB
  }

  public static void insertPageFrom(PDDocument targetDocument, int targetPageNum, PDDocument sourceDocument,
      int sourcePageNum) {
    // STUB
  }

}
