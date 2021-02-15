package com.university.project;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileInputStream;
import java.io.IOException;

public class WorkWithDoc {

    public static class WriteInDocFile {
        ResettableOutputStream out;
        XWPFDocument doc;
        XWPFParagraph paragraph;
        XWPFRun run;

        public WriteInDocFile(ResettableOutputStream out) {
            this.out = out;
            doc = new XWPFDocument();
            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setFontFamily("Times New Roman");
            run.setFontSize(12);
        }

        public void writeImage(FileInputStream imageFile) {
            try {
                run.addPicture(imageFile, XWPFDocument.PICTURE_TYPE_JPEG, "image.png", Units.toEMU(300), Units.toEMU(300));
                run.addBreak();
                out.reset();
                imageFile.close();
                doc.write(out);
                out.close();
            } catch (InvalidFormatException | IOException e) {
                e.printStackTrace();
            }
        }

        public void writeText(String text) {
            run.setText(text);
            run.addBreak();
            try {
                out.reset();
                doc.write(out);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
