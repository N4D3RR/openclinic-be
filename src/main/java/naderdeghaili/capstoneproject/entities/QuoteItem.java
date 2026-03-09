package naderdeghaili.capstoneproject.entities;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "quote_items")
public class QuoteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Integer toothNumber;

    @Column(nullable = false)
    private BigDecimal quotedPrice;

    @ManyToOne
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    @ManyToOne
    @JoinColumn(name = "procedure_id", nullable = false)
    private Procedure procedure;

    public QuoteItem() {
    }

    public QuoteItem(Integer toothNumber, BigDecimal quotedPrice, Quote quote, Procedure procedure) {
        this.toothNumber = toothNumber;
        this.quotedPrice = quotedPrice;
        this.quote = quote;
        this.procedure = procedure;
    }

    public UUID getId() {
        return id;
    }

    public Integer getToothNumber() {
        return toothNumber;
    }

    public void setToothNumber(Integer toothNumber) {
        this.toothNumber = toothNumber;
    }

    public BigDecimal getQuotedPrice() {
        return quotedPrice;
    }

    public void setQuotedPrice(BigDecimal quotedPrice) {
        this.quotedPrice = quotedPrice;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    @Override
    public String toString() {
        return "QuoteItem: " +
                "id: " + id +
                " | toothNumber: " + toothNumber +
                " | quotedPrice: " + quotedPrice +
                " | quote: " + (quote != null ? quote.getId() : null) +
                " | procedure: " + (procedure != null ? procedure.getId() : null);
    }
}
