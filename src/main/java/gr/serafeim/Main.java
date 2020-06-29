package gr.serafeim;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.Certificate;

public class Main {
    // From Itext in Action
    public static void signer(String original, String dest) throws NoSuchProviderException, KeyStoreException, IOException {
        KeyStore ks = KeyStore.getInstance("pkcs12", "BC");
        ks.load(new FileInputStream(path), keystore_password.toCharArray());
        String alias = ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, key_password.toCharArray());
        Certificate[] chain = ks.getCertificateChain(alias);
        PdfReader reader = new PdfReader(original);
        PdfStamper stamper = PdfStamper.createSignature(reader, new FileOutputStream(dest), '\0');
        PdfSignatureAppearance appearance
                = stamper.getSignatureAppearance();
        appearance.setVisibleSignature("mySig");
        appearance.setReason("It's personal.");
        appearance.setLocation("Foobar");
        appearance.setCrypto(
                pk, chain, null, PdfSignatureAppearance.WINCER_SIGNED);

        boolean certified = true;
        if (certified) appearance.setCertificationLevel(PdfSignatureAppearance.CERTIFIED_NO_CHANGES_ALLOWED);
        /*
        if (graphic) {
            appearance.setAcro6Layers(true);
            appearance.setSignatureGraphic(Image.getInstance(RESOURCE));
            appearance.setRenderingMode(
                    PdfSignatureAppearance.RenderingMode.GRAPHIC);
        }
        */

        stamper.close();
    }

    public static void createPdf(String filename)
            throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter writer
                = PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        document.add(new Paragraph("Hello World!"));
        PdfFormField field
                = PdfFormField.createSignature(writer);
        field.setWidget(new Rectangle(72, 732, 144, 780),
                PdfAnnotation.HIGHLIGHT_INVERT);
        field.setFieldName("mySig");
        field.setFlags(PdfAnnotation.FLAGS_PRINT);
        field.setPage();
        field.setMKBorderColor(Color.black);
        field.setMKBackgroundColor(Color.white);
        PdfAppearance tp
                = PdfAppearance.createAppearance(writer, 72, 48);
        tp.rectangle(0.5f, 0.5f, 71.5f, 47.5f);
        tp.stroke();
        field.setAppearance(
                PdfAnnotation.APPEARANCE_NORMAL, tp);
        writer.addAnnotation(field);
        document.close();
    }


    public static void main(String[] args) {
        System.out.println("X");
        try {
            createPdf("z.pdf");
            signer("z.pdf", "zz.pdf");
        } catch (IOException | NoSuchProviderException | KeyStoreException e) {
            e.printStackTrace();
        }
    }
}
