package com.project.pawnprime.service;

import com.project.pawnprime.dto.CloseLoanDTO;
import com.project.pawnprime.model.CloseLoan;
import com.project.pawnprime.model.Loan;
import com.project.pawnprime.repo.CloseLoanRepository;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.borders.Border;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class CloseLoanService {
    private final CloseLoanRepository closeLoanRepository;
    private final LoanService loanService;

    public CloseLoanService(CloseLoanRepository closeLoanRepository, LoanService loanService) {
        this.closeLoanRepository = closeLoanRepository;
        this.loanService = loanService;
    }

    public CloseLoanDTO closeLoan(CloseLoanDTO dto) {
        // Fetch loan
        Loan loan = loanService.getLoanById(dto.getLoanId());
        if (loan == null) {
            throw new RuntimeException("Loan not found for ID: " + dto.getLoanId());
        }

        // Update loan status
        loanService.changeLoanStatus(dto.getLoanId(), "closed");

        // Map DTO to entity
        CloseLoan closeLoan = new CloseLoan();
        closeLoan.setLoan(loan);
        closeLoan.setTotalPrincipal(dto.getTotalPrincipal());
        closeLoan.setTotalInterest(dto.getTotalInterest());
        closeLoan.setTotalPaid(dto.getTotalPaid());
        closeLoan.setClosureDate(dto.getClosureDate() != null ? dto.getClosureDate() : LocalDateTime.now());
        closeLoan.setClosedBy(dto.getClosedBy());
        closeLoan.setRemarks(dto.getRemarks());

        // Save entity
        CloseLoan saved = closeLoanRepository.save(closeLoan);

        // Generate closing PDF
        byte[] pdfBytes;
        try {
            pdfBytes = generateClosingPdf(loan, dto);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }

        // Save PDF to local file (in 'receipts' folder)
        String filePath = "receipts/closing_receipt_" + dto.getLoanId() + ".pdf";
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(pdfBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save PDF", e);
        }

        // Map entity back to DTO
        CloseLoanDTO responseDTO = new CloseLoanDTO();
        responseDTO.setLoanId(saved.getLoan().getId());
        responseDTO.setTotalPrincipal(saved.getTotalPrincipal());
        responseDTO.setTotalInterest(saved.getTotalInterest());
        responseDTO.setTotalPaid(saved.getTotalPaid());
        responseDTO.setClosureDate(saved.getClosureDate());
        responseDTO.setClosedBy(saved.getClosedBy());
        responseDTO.setRemarks(saved.getRemarks());

        return responseDTO;
    }

    private byte[] generateClosingPdf(Loan loan, CloseLoanDTO dto) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(30, 30, 30, 30);

        PdfFont regularFont = PdfFontFactory.createFont("Helvetica");
        PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");

        DeviceRgb primaryColor = new DeviceRgb(0, 102, 204);
        DeviceRgb accentColor = new DeviceRgb(230, 230, 250);
        DeviceRgb textColor = new DeviceRgb(0, 0, 0); // equivalent to black

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Header Section
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
        headerTable.setWidth(UnitValue.createPercentValue(100));
        headerTable.setMarginBottom(20);

        Cell leftHeader = new Cell();
        leftHeader.setBorder(Border.NO_BORDER);
        leftHeader.setBackgroundColor(accentColor);
        leftHeader.setPadding(10);
        leftHeader.add(new Paragraph(new Text("PawnPrime").setFont(boldFont).setFontSize(24).setFontColor(primaryColor)));
        leftHeader.add(new Paragraph(new Text("Loan Closing Receipt").setFont(regularFont).setFontSize(14).setFontColor(textColor)));

        Cell rightHeader = new Cell();
        rightHeader.setBorder(Border.NO_BORDER);
        rightHeader.setPadding(10);
        rightHeader.setTextAlignment(TextAlignment.RIGHT);
        rightHeader.add(new Paragraph(new Text("Closing Date").setFont(regularFont).setFontSize(10).setFontColor(textColor)));
        rightHeader.add(new Paragraph(new Text(LocalDate.now().format(dateFormatter)).setFont(boldFont).setFontSize(12).setFontColor(textColor)));
        rightHeader.add(new Paragraph(new Text("Loan ID").setFont(regularFont).setFontSize(10).setFontColor(textColor)));
        rightHeader.add(new Paragraph(new Text(String.valueOf(loan.getId())).setFont(boldFont).setFontSize(12).setFontColor(textColor)));

        headerTable.addCell(leftHeader);
        headerTable.addCell(rightHeader);
        document.add(headerTable);

        // Divider Line
        ILineDrawer line = new SolidLine(1f);
        line.setColor(primaryColor);
        LineSeparator separator = new LineSeparator(line);
        separator.setMarginBottom(15);
        document.add(separator);

        // Loan Information Section
        document.add(new Paragraph("Loan Information")
                .setFont(boldFont)
                .setFontSize(16)
                .setFontColor(primaryColor)
                .setMarginBottom(10));
        
        Table loanInfoTable = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25}));
        loanInfoTable.setWidth(UnitValue.createPercentValue(100));
        loanInfoTable.setMarginBottom(20);

        addKeyValueCell(loanInfoTable, "Loan ID:", String.valueOf(loan.getId()), regularFont, boldFont, accentColor, textColor);
        addKeyValueCell(loanInfoTable, "Status:", "CLOSED", regularFont, boldFont, accentColor, textColor);
        addKeyValueCell(loanInfoTable, "Loan Date:", loan.getDate().format(dateFormatter), regularFont, boldFont, accentColor, textColor);
        addKeyValueCell(loanInfoTable, "Loan Value:", currencyFormat.format(loan.getLoanVal()), regularFont, boldFont, accentColor, textColor);
        addKeyValueCell(loanInfoTable, "Interest Rate:", loan.getInterestRate() + "%", regularFont, boldFont, accentColor, textColor);
        addKeyValueCell(loanInfoTable, "Duration:", loan.getDuration() + " months", regularFont, boldFont, accentColor, textColor);

        document.add(loanInfoTable);

        // Ornament Details Section
        document.add(new Paragraph("Ornament Details")
                .setFont(boldFont)
                .setFontSize(16)
                .setFontColor(primaryColor)
                .setMarginTop(20)
                .setMarginBottom(10));
        
        Table ornamentTable = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25}));
        ornamentTable.setWidth(UnitValue.createPercentValue(100));
        ornamentTable.setMarginBottom(20);

        addKeyValueCell(ornamentTable, "Type:", loan.getTypeOrnament(), regularFont, boldFont, accentColor, textColor);
        addKeyValueCell(ornamentTable, "Net Weight:", loan.getNetGram() + "g", regularFont, boldFont, accentColor, textColor);
        addKeyValueCell(ornamentTable, "Purity:", loan.getPurityPercent() + "%", regularFont, boldFont, accentColor, textColor);
        addKeyValueCell(ornamentTable, "Value:", currencyFormat.format(loan.getValue()), regularFont, boldFont, accentColor, textColor);

        document.add(ornamentTable);

        // Closing Summary Section
        document.add(new Paragraph("Closing Summary")
                .setFont(boldFont)
                .setFontSize(16)
                .setFontColor(primaryColor)
                .setMarginTop(20)
                .setMarginBottom(10));
        
        Table closingTable = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25}));
        closingTable.setWidth(UnitValue.createPercentValue(100));
        closingTable.setMarginBottom(20);

        addKeyValueCell(closingTable, "Total Principal:", currencyFormat.format(dto.getTotalPrincipal()), regularFont, boldFont, accentColor, textColor);
        addKeyValueCell(closingTable, "Total Interest:", currencyFormat.format(dto.getTotalInterest()), regularFont, boldFont, accentColor, textColor);
        addKeyValueCell(closingTable, "Total Paid:", currencyFormat.format(dto.getTotalPaid()), regularFont, boldFont, accentColor, textColor);
        addKeyValueCell(closingTable, "Closed By:", dto.getClosedBy(), regularFont, boldFont, accentColor, textColor);
        addKeyValueCell(closingTable, "Remarks:", dto.getRemarks(), regularFont, boldFont, accentColor, textColor);

        document.add(closingTable);

        // Footer
        document.add(new Paragraph("Thank you for choosing PawnPrime")
                .setFont(boldFont)
                .setFontSize(12)
                .setFontColor(primaryColor)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20));
        document.add(new Paragraph("Your loan is now closed and the ornament will be returned.")
                .setFont(regularFont)
                .setFontSize(10)
                .setFontColor(textColor)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("This is a computer-generated receipt and does not require a signature")
                .setFont(regularFont)
                .setFontSize(10)
                .setFontColor(textColor)
                .setTextAlignment(TextAlignment.CENTER));

        document.close();
        return baos.toByteArray();
    }

    private void addKeyValueCell(Table table, String key, String value, PdfFont regularFont, PdfFont boldFont, DeviceRgb keyBgColor, DeviceRgb textColor) {
        Cell keyCell = new Cell().add(new Paragraph(key).setFont(boldFont).setFontSize(10).setFontColor(textColor));
        keyCell.setBackgroundColor(keyBgColor);
        keyCell.setBorder(new SolidBorder(0.5f));
        keyCell.setPadding(8);
        table.addCell(keyCell);

        Cell valueCell = new Cell().add(new Paragraph(value != null ? value : "N/A").setFont(regularFont).setFontSize(10).setFontColor(textColor));
        valueCell.setBorder(new SolidBorder(0.5f));
        valueCell.setPadding(8);
        table.addCell(valueCell);
    }
}