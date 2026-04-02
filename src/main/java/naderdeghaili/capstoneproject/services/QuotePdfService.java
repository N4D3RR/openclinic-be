package naderdeghaili.capstoneproject.services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.Quote;
import naderdeghaili.capstoneproject.entities.QuoteItem;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
public class QuotePdfService {

    private final QuoteService quoteService;

    public QuotePdfService(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    public byte[] generatePdf(UUID quoteId) {
        Quote quote = quoteService.findById(quoteId);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 50, 50, 60, 60);
            PdfWriter.getInstance(doc, out);
            doc.open();

            //font
            Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD, new Color(0x1B, 0x2A, 0x3D));
            Font sectionFont = new Font(Font.HELVETICA, 11, Font.BOLD, new Color(0x2A, 0x9D, 0x8F));
            Font labelFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.DARK_GRAY);
            Font valueFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.DARK_GRAY);
            Font smallMuted = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.GRAY);
            Font totalFont = new Font(Font.HELVETICA, 13, Font.BOLD, new Color(0x1B, 0x2A, 0x3D));

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            //header studio
            Paragraph title = new Paragraph("OpenClinic", titleFont);
            title.setSpacingAfter(2);
            doc.add(title);

            Paragraph subtitle = new Paragraph("Studio Odontoiatrico", smallMuted);
            subtitle.setSpacingAfter(20);
            doc.add(subtitle);

            //document title
            Paragraph docTitle = new Paragraph("PREVENTIVO", sectionFont);
            docTitle.setSpacingAfter(4);
            doc.add(docTitle);

            Paragraph docDate = new Paragraph(
                    "Data: " + quote.getCreatedAt().format(fmt), valueFont);
            docDate.setSpacingAfter(4);
            doc.add(docDate);

            Paragraph docStatus = new Paragraph(
                    "Stato: " + formatStatus(quote.getStatus().name()), valueFont);
            docStatus.setSpacingAfter(20);
            doc.add(docStatus);

            //patient data
            doc.add(new Paragraph("PAZIENTE", sectionFont));
            doc.add(new Paragraph(
                    quote.getPatient().getFirstName() + " " + quote.getPatient().getLastName(), labelFont));
            if (quote.getPatient().getFiscalCode() != null)
                doc.add(new Paragraph("CF: " + quote.getPatient().getFiscalCode(), valueFont));
            if (quote.getPatient().getEmail() != null)
                doc.add(new Paragraph("Email: " + quote.getPatient().getEmail(), valueFont));
            if (quote.getPatient().getPhone() != null)
                doc.add(new Paragraph("Tel: " + quote.getPatient().getPhone(), valueFont));

            doc.add(Chunk.NEWLINE);

            //dentist
            if (quote.getDentist() != null) {
                doc.add(new Paragraph("DENTISTA", sectionFont));
                doc.add(new Paragraph(
                        quote.getDentist().getFirstName() + " " + quote.getDentist().getLastName(), valueFont));
                doc.add(Chunk.NEWLINE);
            }

            //quote items
            doc.add(new Paragraph("VOCI DEL PREVENTIVO", sectionFont));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3f, 1.5f, 1f, 1.5f});
            table.setSpacingBefore(8);
            table.setSpacingAfter(8);

            //table header
            addHeaderCell(table, "Prestazione", labelFont);
            addHeaderCell(table, "Codice", labelFont);
            addHeaderCell(table, "Dente", labelFont);
            addHeaderCell(table, "Prezzo €", labelFont);

            //rows
            BigDecimal total = BigDecimal.ZERO;
            for (QuoteItem item : quote.getItems()) {
                addCell(table, item.getProcedure().getName(), valueFont);
                addCell(table, item.getProcedure().getCode(), valueFont);
                addCell(table, String.valueOf(item.getToothNumber()), valueFont);
                addCell(table, "€ " + item.getQuotedPrice().toPlainString(), valueFont);
                total = total.add(item.getQuotedPrice());
            }

            doc.add(table);

            //total
            Paragraph totalPar = new Paragraph(
                    "TOTALE: € " + total.toPlainString(), totalFont);
            totalPar.setAlignment(Element.ALIGN_RIGHT);
            totalPar.setSpacingBefore(10);
            doc.add(totalPar);

            //note
            if (quote.getNotes() != null && !quote.getNotes().isBlank()) {
                doc.add(Chunk.NEWLINE);
                doc.add(new Paragraph("NOTE", sectionFont));
                doc.add(new Paragraph(quote.getNotes(), valueFont));
            }

            //footer
            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph(
                    "Documento generato da OpenClinic — " + java.time.LocalDate.now().format(fmt), smallMuted);
            footer.setAlignment(Element.ALIGN_CENTER);
            doc.add(footer);

            doc.close();
            log.info("Quote PDF generated for quote {}", quoteId);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Errore nella generazione del PDF preventivo", e);
        }
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(0xF4, 0xF6, 0xF9));
        cell.setPadding(7);
        cell.setBorderColor(new Color(0xE5, 0xE7, 0xEB));
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(new Color(0xE5, 0xE7, 0xEB));
        table.addCell(cell);
    }

    private String formatStatus(String status) {
        return switch (status) {
            case "DRAFT" -> "Bozza";
            case "SENT" -> "Inviato";
            case "ACCEPTED" -> "Accettato";
            case "REJECTED" -> "Rifiutato";
            default -> status;
        };
    }
}