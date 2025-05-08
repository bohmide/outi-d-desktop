package org.example.utils.user;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.example.entities.User;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class PDFGenerator {

    public static void generateUserListPDF(List<User> users, File file) {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

            Paragraph title = new Paragraph("📋 User List", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.5f, 3f, 1.5f, 2f});

            addTableHeader(table, headerFont);
            for (User user : users) {
                addUserRow(table, user, bodyFont);
            }

            document.add(table);
            document.close();

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTableHeader(PdfPTable table, Font font) {
        String[] headers = {"Name", "Email", "Role", "Phone"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }

    private static void addUserRow(PdfPTable table, User user, Font font) {
        table.addCell(new Phrase(user.getFirstName() + " " + user.getLastName(), font));
        table.addCell(new Phrase(user.getEmail(), font));
        table.addCell(new Phrase(user.getType(), font));
        table.addCell(new Phrase(user.getNumtel(), font));
    }
}
