package naderdeghaili.capstoneproject.services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.Payment;
import naderdeghaili.capstoneproject.entities.PaymentStatus;
import naderdeghaili.capstoneproject.exceptions.ValidationException;
import naderdeghaili.capstoneproject.repositories.PaymentRepository;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
public class InvoiceService {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;


    public InvoiceService(PaymentService paymentService, PaymentRepository paymentRepository) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }

    public byte[] generateInvoice(UUID paymentId) {
        Payment payment = paymentService.findById(paymentId);

        if (payment.getStatus() != PaymentStatus.PAID)
            throw new ValidationException(java.util.List.of("La fattura può essere generata solo per pagamenti con stato PAID"));

        int year = payment.getPaymentDate().getYear();
        long count = paymentRepository.countPaidByYear(year);
        String invoiceNumber = year + "/" + String.format("%03d", count);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 50, 50, 60, 60);
            PdfWriter.getInstance(doc, out);
            doc.open();

            // Font
            Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD, new Color(0x1B, 0x2A, 0x3D));
            Font sectionFont = new Font(Font.HELVETICA, 11, Font.BOLD, new Color(0x2A, 0x9D, 0x8F));
            Font labelFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.DARK_GRAY);
            Font valueFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.DARK_GRAY);
            Font smallMuted = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.GRAY);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Header
            Paragraph title = new Paragraph("OpenClinic", titleFont);
            title.setSpacingAfter(2);
            doc.add(title);

            Paragraph subtitle = new Paragraph("Studio Odontoiatrico", smallMuted);
            subtitle.setSpacingAfter(20);
            doc.add(subtitle);


            // Numero fattura
            Paragraph invoiceTitle = new Paragraph("FATTURA N. " + invoiceNumber, sectionFont);
            invoiceTitle.setSpacingAfter(4);
            doc.add(invoiceTitle);

            Paragraph invoiceDate = new Paragraph(
                    "Data: " + payment.getPaymentDate().format(fmt), valueFont);
            invoiceDate.setSpacingAfter(20);
            doc.add(invoiceDate);

            // Dati paziente
            doc.add(new Paragraph("PAZIENTE", sectionFont));
            doc.add(new Paragraph(
                    payment.getPatient().getFirstName() + " " + payment.getPatient().getLastName(), labelFont));
            if (payment.getPatient().getFiscalCode() != null)
                doc.add(new Paragraph("CF: " + payment.getPatient().getFiscalCode(), valueFont));
            if (payment.getPatient().getEmail() != null)
                doc.add(new Paragraph("Email: " + payment.getPatient().getEmail(), valueFont));
            if (payment.getPatient().getPhone() != null)
                doc.add(new Paragraph("Tel: " + payment.getPatient().getPhone(), valueFont));

            doc.add(Chunk.NEWLINE);

            // Dettaglio pagamento
            doc.add(new Paragraph("DETTAGLIO PAGAMENTO", sectionFont));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(8);
            table.setSpacingAfter(8);

            addTableRow(table, "Importo", "€ " + String.format("%.2f", payment.getAmount()), labelFont, valueFont);
            addTableRow(table, "Metodo di pagamento", formatMethod(payment.getMethod().name()), labelFont, valueFont);
            addTableRow(table, "Stato", "PAGATO", labelFont, valueFont);
            if (payment.getNotes() != null && !payment.getNotes().isBlank())
                addTableRow(table, "Note", payment.getNotes(), labelFont, valueFont);

            doc.add(table);

            // Totale
            doc.add(Chunk.NEWLINE);
            Font totalFont = new Font(Font.HELVETICA, 13, Font.BOLD, new Color(0x1B, 0x2A, 0x3D));
            Paragraph total = new Paragraph("TOTALE: € " + String.format("%.2f", payment.getAmount()), totalFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingBefore(10);
            doc.add(total);

            // Footer
            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph("Documento generato da OpenClinic — " +
                    java.time.LocalDate.now().format(fmt), smallMuted);
            footer.setAlignment(Element.ALIGN_CENTER);
            doc.add(footer);

            doc.close();
            log.info("Invoice {} generated for payment {}", invoiceNumber, paymentId);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Errore nella generazione della fattura", e);
        }
    }

    private void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.BOTTOM);
        labelCell.setPadding(6);
        labelCell.setBorderColor(new Color(0xE5, 0xE7, 0xEB));

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setPadding(6);
        valueCell.setBorderColor(new Color(0xE5, 0xE7, 0xEB));

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private String formatMethod(String method) {
        return switch (method) {
            case "CASH" -> "Contanti";
            case "CARD" -> "Carta";
            case "BANK_TRANSFER" -> "Bonifico";
            case "CHECK" -> "Assegno";
            default -> method;
        };
    }

}

