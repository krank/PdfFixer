import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.json.JSONObject;

public class ActionMetadata extends Configuration.Action {

  String author;
  String creator;
  String producer;

  String title;
  String subject;
  String keywords;

  @Override
  public void Load(JSONObject configFragment) {

    author = Configuration.GetStringIfKeyExists("author", configFragment);
    creator = Configuration.GetStringIfKeyExists("creator", configFragment);
    producer = Configuration.GetStringIfKeyExists("producer", configFragment);
    title = Configuration.GetStringIfKeyExists("title", configFragment);
    subject = Configuration.GetStringIfKeyExists("subject", configFragment);
    keywords = Configuration.GetStringIfKeyExists("keywords", configFragment);



  }

  @Override
  public void ApplyTo(PDDocument document) {
    System.out.println("Editing metadata...");

    // http://useof.org/java-open-source/org.apache.pdfbox.pdmodel.common.PDMetadata

    // PDMetadata metadata = document.getDocumentCatalog().getMetadata();

    // metadata.

    PDDocumentInformation information = document.getDocumentInformation();

    System.out.println(author);
    System.out.println(creator);
    System.out.println(producer);
    System.out.println(title);
    System.out.println(subject);
    System.out.println(keywords);

    if (author != "")
      information.setAuthor(author);
    if (creator != "")
      information.setCreator(creator);
    if (producer != "")
      information.setProducer(producer);
    if (title != "")
      information.setTitle(title);
    if (subject != "")
      information.setSubject(subject);
    if (keywords != "")
      information.setKeywords(keywords);

      

    information.setAuthor("hehe");
    System.out.println(information.getAuthor());

    document.setDocumentInformation(information);
  }
}