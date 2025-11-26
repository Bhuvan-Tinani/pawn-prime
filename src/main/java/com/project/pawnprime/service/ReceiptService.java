package com.project.pawnprime.service;

import com.project.pawnprime.model.CloseLoan;
import com.project.pawnprime.model.Customer;
import com.project.pawnprime.model.Loan;
import com.project.pawnprime.model.RepaymentTransaction;
import com.project.pawnprime.repo.CloseLoanRepository;
import com.project.pawnprime.repo.LoanRepository;
import com.project.pawnprime.repo.RepaymentTransactionRepository;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ReceiptService {

	private CloseLoanRepository closeLoanRepository;

	private final RepaymentTransactionRepository repaymentRepo;
	private final LoanRepository loanRepo;
	private final ResourceLoader resourceLoader;

	private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(25, 118, 210);
	private static final DeviceRgb SECONDARY_COLOR = new DeviceRgb(66, 165, 245);
	private static final DeviceRgb ACCENT_COLOR = new DeviceRgb(255, 193, 7);
	private static final DeviceRgb HEADER_BG = new DeviceRgb(240, 248, 255);
	private static final DeviceRgb SUCCESS_COLOR = new DeviceRgb(76, 175, 80);

	private PdfFont notoSansFont;

	public ReceiptService(RepaymentTransactionRepository repaymentRepo, LoanRepository loanRepo,
			ResourceLoader resourceLoader, CloseLoanRepository closeLoanRepository) {
		this.repaymentRepo = repaymentRepo;
		this.loanRepo = loanRepo;
		this.resourceLoader = resourceLoader;
		this.closeLoanRepository = closeLoanRepository;
		initializeFont();
	}

	private void initializeFont() {
		try {
			Resource resource = resourceLoader.getResource("classpath:fonts/NotoSans-Regular.ttf");
			try (InputStream fontStream = resource.getInputStream()) {
				Path tempFile = Files.createTempFile("NotoSans-Regular", ".ttf");
				Files.copy(fontStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

				FontProgram fontProgram = FontProgramFactory.createFont(tempFile.toString());
				notoSansFont = PdfFontFactory.createFont(fontProgram, PdfEncodings.IDENTITY_H,
						EmbeddingStrategy.PREFER_EMBEDDED);

				Files.deleteIfExists(tempFile);
			}
		} catch (IOException e) {
			System.err.println("Font loading error: " + e.getMessage());
			notoSansFont = null; // Fallback to default font
		}
	}

	public void exportLoanReceipt(Long loanId, OutputStream outputStream) throws IOException {
		try {
			JasperReport jasperReport = (JasperReport) JRLoader
					.loadObject(resourceLoader.getResource("classpath:reports/receipt.jasper").getInputStream());
			net.sf.jasperreports.pdf.JRPdfExporter exporter = new net.sf.jasperreports.pdf.JRPdfExporter();
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
			exporter.exportReport();

		} catch (JRException e) {
			System.err.println("JASPER REPORTS ERROR: " + e.getMessage());
			throw new RuntimeException("Failed to generate PDF receipt due to reporting error.", e);
		}
	}

	public void exportNoc(Long loanId, OutputStream outputStream) throws IOException {
		try {
			Loan loan = loanRepo.findById(loanId)
					.orElseThrow(() -> new RuntimeException("Loan not found with ID: " + loanId));
			Customer customer = loan.getCustomer();

			Map<String, Object> parameters = new HashMap<>();
			parameters.put("loanId", loan.getId());
			parameters.put("customerName",
					customer.getName() + (customer.getMiddleName() != null ? " " + customer.getMiddleName() : "")
							+ (customer.getLastName() != null ? " " + customer.getLastName() : ""));
			parameters.put("mobile", customer.getMobile());
			parameters.put("aadharNo", customer.getAadharNo());
			parameters.put("dob", customer.getDob() != null ? customer.getDob().toString() : "");
			parameters.put("photoUrl", customer.getPhotoUrl() != null ? customer.getPhotoUrl() : "");
			parameters.put("aadharUrl", customer.getAadharUrl() != null ? customer.getAadharUrl() : "");
			parameters.put("loanDate", loan.getDate() != null ? loan.getDate().toString() : "");
			parameters.put("typeOrnament", loan.getTypeOrnament());
			parameters.put("netGram", loan.getNetGram());
			parameters.put("purityPercent", loan.getPurityPercent());
			parameters.put("purityGram", loan.getPurityGram());
			parameters.put("value", loan.getValue());
			parameters.put("loanVal", loan.getLoanVal());
			parameters.put("interestRate", loan.getInterestRate());
			parameters.put("duration", loan.getDuration());
			parameters.put("loanStatus", loan.getLoanStatus());

			JasperReport jasperReport = (JasperReport) JRLoader
					.loadObject(resourceLoader.getResource("classpath:reports/noc.jasper").getInputStream());

			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
					java.util.Collections.singletonList(loan));

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			net.sf.jasperreports.pdf.JRPdfExporter exporter = new net.sf.jasperreports.pdf.JRPdfExporter();
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
			exporter.exportReport();

		} catch (JRException e) {
			System.err.println("JASPER REPORTS ERROR: " + e.getMessage());
			throw new RuntimeException("Failed to generate NOC PDF due to reporting error.", e);
		}
	}

	public void exportNocWithIText(Long loanId, OutputStream outputStream) throws IOException {
	    try {
	        Loan loan = loanRepo.findById(loanId)
	                .orElseThrow(() -> new RuntimeException("Loan not found with ID: " + loanId));
	        Customer customer = loan.getCustomer();
	        CloseLoan closeLoan = closeLoanRepository.findByLoanId(loanId)
	                .orElseThrow(() -> new RuntimeException("Closing details not found for loan: " + loanId));

	        String closingDateFormatted = closeLoan.getClosureDate().toLocalDate()
	                .format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));

	        PdfWriter writer = new PdfWriter(outputStream);
	        PdfDocument pdf = new PdfDocument(writer);
	        Document document = new Document(pdf);
	        document.setMargins(20, 20, 20, 20);

	        PdfFont fontToUse = (notoSansFont != null) ? notoSansFont : PdfFontFactory.createFont();

	        Paragraph companyName = new Paragraph("PAWNPRIME").setFontSize(16).setBold().setFontColor(PRIMARY_COLOR)
	                .setTextAlignment(TextAlignment.CENTER).setFont(fontToUse);
	        document.add(companyName);

	        Table decorativeLine = new Table(1).useAllAvailableWidth();
	        decorativeLine.addCell(
	                new Cell().add(new Paragraph("")).setHeight(2).setBackgroundColor(ACCENT_COLOR).setBorder(null));
	        document.add(decorativeLine);

	        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
	        Paragraph dateText = new Paragraph("Date: " + currentDate).setFontSize(8)
	                .setTextAlignment(TextAlignment.RIGHT).setFont(fontToUse);
	        document.add(dateText);

	        Paragraph nocTitle = new Paragraph("NO OBJECTION CERTIFICATE").setFontSize(14).setBold()
	                .setFontColor(ColorConstants.WHITE).setBackgroundColor(PRIMARY_COLOR)
	                .setTextAlignment(TextAlignment.CENTER).setPadding(5).setFont(fontToUse);
	        document.add(nocTitle);

	        Paragraph refNumber = new Paragraph(
	                "NOC Reference No: NOC/" + loan.getId() + "/" + LocalDate.now().getYear()).setFontSize(9).setBold()
	                .setFontColor(PRIMARY_COLOR).setTextAlignment(TextAlignment.CENTER).setMarginBottom(10)
	                .setFont(fontToUse);
	        document.add(refNumber);

	        Paragraph customerInfo = new Paragraph("Client: " + customer.getName()
	                + (customer.getMiddleName() != null ? " " + customer.getMiddleName() : "")
	                + (customer.getLastName() != null ? " " + customer.getLastName() : "") + " | Contact: "
	                + customer.getMobile()).setFontSize(12).setFontColor(PRIMARY_COLOR).setMarginBottom(15)
	                .setFont(fontToUse);
	        document.add(customerInfo);

	        Paragraph closingDateParagraph = new Paragraph("Closing Date: " + closingDateFormatted).setFontSize(10)
	                .setBold() // make text bold
	                .setFontColor(ColorConstants.BLACK) // set black color
	                .setTextAlignment(TextAlignment.RIGHT).setFont(fontToUse).setMarginBottom(5);
	        document.add(closingDateParagraph);

	        Table loanTable = new Table(new float[] { 2, 3 }).useAllAvailableWidth();
	        loanTable.setMarginBottom(15);

	        addTableRow(loanTable, "Loan ID", String.valueOf(loan.getId()), true, fontToUse);
	        addTableRow(loanTable, "Loan Date", loan.getDate() != null ? loan.getDate().toString() : "N/A", false,
	                fontToUse);
	        addTableRow(loanTable, "Ornament Type", loan.getTypeOrnament(), true, fontToUse);
	        addTableRow(loanTable, "Net Weight", loan.getNetGram() + " grams", false, fontToUse);
	        addTableRow(loanTable, "Purity", loan.getPurityPercent() + "%", true, fontToUse);
	        addTableRow(loanTable, "Pure Weight", loan.getPurityGram() + " grams", false, fontToUse);
	        addTableRow(loanTable, "Loan Amount", "₹ " + String.format("%,.2f", loan.getLoanVal()), true, fontToUse);
	        addTableRow(loanTable, "Interest Rate", loan.getInterestRate() + "% per annum", false, fontToUse);
	        addTableRow(loanTable, "Loan Duration", loan.getDuration() + " months", true, fontToUse);

	        Cell statusLabelCell = new Cell().add(new Paragraph("Loan Status").setBold()).setBackgroundColor(HEADER_BG)
	                .setPadding(5).setBorder(new SolidBorder(SECONDARY_COLOR, 0.5f)).setFont(fontToUse);
	        Cell statusValueCell = new Cell()
	                .add(new Paragraph(loan.getLoanStatus()).setBold().setFontColor(SUCCESS_COLOR)).setPadding(5)
	                .setBorder(new SolidBorder(SECONDARY_COLOR, 0.5f)).setFont(fontToUse);
	        loanTable.addCell(statusLabelCell);
	        loanTable.addCell(statusValueCell);

	        document.add(loanTable);

	        Paragraph declaration = new Paragraph(
	                "This is to certify that the above-mentioned customer has successfully repaid the entire loan amount along with all applicable interest and charges. The pledged ornaments mentioned in this certificate are hereby released and the company has no objection to the customer claiming their pledged items. All ornaments must be collected within 7 days from the date of NOC issuance upon presenting this original NOC document and valid photo ID proof.")
	                .setFontSize(8).setTextAlignment(TextAlignment.JUSTIFIED).setMarginBottom(5).setFont(fontToUse);
	        document.add(declaration);

	        // -------- Added professional NOC details similar to closure letter --------

	        Paragraph confirmationParagraph = new Paragraph(
	                "We further confirm that, as of " + closingDateFormatted
	                        + ", there are no outstanding dues, charges, or liabilities payable by the customer in respect of the above loan account. "
	                        + "This No Objection Certificate is being issued upon full and final settlement of the loan as per the terms and conditions agreed at the time of sanction.")
	                .setFontSize(8)
	                .setTextAlignment(TextAlignment.JUSTIFIED)
	                .setMarginTop(5)
	                .setMarginBottom(5)
	                .setFont(fontToUse);
	        document.add(confirmationParagraph);

	        Paragraph postClosureNotes = new Paragraph(
	                "Post-Closure Notes:\n"
	                        + "• Any security documents or collateral held against this loan, other than the pledged ornaments, will be released in due course as per internal verification and compliance norms.\n"
	                        + "• If the loan has been closed through a pre-payment or foreclosure process, any residual or excess amount received, if applicable, will be refunded in accordance with company policy within the standard processing timeline.\n"
	                        + "• This certificate may be used by the customer as proof of closure and clearance of the above loan account, wherever required.")
	                .setFontSize(8)
	                .setTextAlignment(TextAlignment.JUSTIFIED)
	                .setMarginBottom(8)
	                .setFont(fontToUse);
	        document.add(postClosureNotes);

	        Paragraph closingNote = new Paragraph(
	                "We thank you for availing services from PawnPrime and look forward to serving you again in the future.")
	                .setFontSize(8)
	                .setTextAlignment(TextAlignment.JUSTIFIED)
	                .setMarginBottom(15)
	                .setFont(fontToUse);
	        document.add(closingNote);

	        // ---------------- Signature & disclaimer section -------------------------

	        Paragraph signForCompany = new Paragraph("For PawnPrime")
	                .setFontSize(9)
	                .setBold()
	                .setTextAlignment(TextAlignment.LEFT)
	                .setMarginTop(15)
	                .setFont(fontToUse);
	        document.add(signForCompany);

	        Paragraph signDesignation = new Paragraph("Authorised Signatory")
	                .setFontSize(8)
	                .setTextAlignment(TextAlignment.LEFT)
	                .setMarginTop(25)
	                .setFont(fontToUse);
	        document.add(signDesignation);

	        Paragraph disclaimer = new Paragraph(
	                "Disclaimer: This No Objection Certificate is system-generated based on our internal records and does not require a physical signature or company seal. "
	                        + "In case of any discrepancy, kindly contact our branch within 7 days from the date of issue.")
	                .setFontSize(7)
	                .setTextAlignment(TextAlignment.CENTER)
	                .setMarginTop(20)
	                .setFont(fontToUse);
	        document.add(disclaimer);

	        // -------------------------------------------------------------------------

	        document.close();

	    } catch (Exception e) {
	        System.err.println("ITEXT PDF ERROR: " + e.getMessage());
	        throw new RuntimeException("Failed to generate NOC PDF with iText.", e);
	    }
	}


	public void exportCloseLoanReceipt(Long loanId, OutputStream outputStream) throws IOException {
	    try {
	        Loan loan = loanRepo.findById(loanId)
	                .orElseThrow(() -> new RuntimeException("Loan not found with ID: " + loanId));
	        Customer customer = loan.getCustomer();
	        List<RepaymentTransaction> repayments = repaymentRepo.findByLoanId(loanId);
	        CloseLoan closeLoan = closeLoanRepository.findByLoanId(loanId)
	                .orElseThrow(() -> new RuntimeException("Closing details not found for loan: " + loanId));

	        String closingDateFormatted = closeLoan.getClosureDate().toLocalDate()
	                .format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));

	        PdfWriter writer = new PdfWriter(outputStream);
	        PdfDocument pdf = new PdfDocument(writer);
	        Document document = new Document(pdf);
	        document.setMargins(20, 20, 20, 20);

	        PdfFont fontToUse = (notoSansFont != null) ? notoSansFont : PdfFontFactory.createFont();

	        Paragraph companyName = new Paragraph("PAWNPRIME LOAN SERVICES").setFontSize(18).setBold()
	                .setFontColor(PRIMARY_COLOR).setTextAlignment(TextAlignment.CENTER).setFont(fontToUse);
	        document.add(companyName);

	        Table headerLine = new Table(1).useAllAvailableWidth();
	        headerLine.addCell(
	                new Cell().add(new Paragraph("")).setHeight(1).setBackgroundColor(ACCENT_COLOR).setBorder(null));
	        document.add(headerLine);

	        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
	        Paragraph dateText = new Paragraph("Issued: " + currentDate).setFontSize(9).setFontColor(SECONDARY_COLOR)
	                .setTextAlignment(TextAlignment.RIGHT).setFont(fontToUse);
	        document.add(dateText);

	        Paragraph title = new Paragraph("LOAN CLOSURE STATEMENT").setFontSize(16).setBold()
	                .setFontColor(ColorConstants.WHITE).setBackgroundColor(PRIMARY_COLOR)
	                .setTextAlignment(TextAlignment.CENTER).setPadding(8).setFont(fontToUse);
	        document.add(title);

	        Paragraph refNumber = new Paragraph("Ref No: CLS-" + loan.getId() + "-" + LocalDate.now().getYear())
	                .setFontSize(10).setBold().setFontColor(PRIMARY_COLOR).setTextAlignment(TextAlignment.CENTER)
	                .setMarginBottom(15).setFont(fontToUse);
	        document.add(refNumber);

	        Paragraph customerInfo = new Paragraph("Client: " + customer.getName()
	                + (customer.getMiddleName() != null ? " " + customer.getMiddleName() : "")
	                + (customer.getLastName() != null ? " " + customer.getLastName() : "") + " | Contact: "
	                + customer.getMobile()).setFontSize(12).setFontColor(PRIMARY_COLOR).setMarginBottom(15)
	                .setFont(fontToUse);
	        document.add(customerInfo);

	        Paragraph closingDateParagraph = new Paragraph("Closing Date: " + closingDateFormatted).setFontSize(10)
	                .setBold().setFontColor(ColorConstants.BLACK)
	                .setTextAlignment(TextAlignment.RIGHT).setFont(fontToUse).setMarginBottom(5);
	        document.add(closingDateParagraph);

	        Table loanTable = new Table(new float[] { 2, 3 }).useAllAvailableWidth();
	        loanTable.setMarginBottom(15);

	        addTableRow(loanTable, "Loan ID", String.valueOf(loan.getId()), true, fontToUse);
	        addTableRow(loanTable, "Issue Date", loan.getDate() != null ? loan.getDate().toString() : "N/A", false, fontToUse);
	        addTableRow(loanTable, "Ornament", loan.getTypeOrnament(), true, fontToUse);
	        addTableRow(loanTable, "Net Weight", loan.getNetGram() + "g", false, fontToUse);
	        addTableRow(loanTable, "Purity", loan.getPurityPercent() + "%", true, fontToUse);
	        addTableRow(loanTable, "Pure Weight", loan.getPurityGram() + "g", false, fontToUse);
	        addTableRow(loanTable, "Principal", "₹ " + String.format("%,.2f", loan.getLoanVal()), true, fontToUse);
	        addTableRow(loanTable, "Interest", loan.getInterestRate() + "% p.a.", false, fontToUse);
	        addTableRow(loanTable, "Term", loan.getDuration() + " months", true, fontToUse);
	        addTableRow(loanTable, "Total Paid", "₹ " + String.format("%,.2f",
	                repayments.stream().mapToDouble(RepaymentTransaction::getTotalAmt).sum()), false, fontToUse);

	        Cell statusLabelCell = new Cell().add(new Paragraph("Status").setBold()).setBackgroundColor(HEADER_BG)
	                .setPadding(5).setBorder(new SolidBorder(SECONDARY_COLOR, 0.5f)).setFont(fontToUse);
	        Cell statusValueCell = new Cell().add(new Paragraph("Closed").setBold().setFontColor(SUCCESS_COLOR))
	                .setPadding(5).setBorder(new SolidBorder(SECONDARY_COLOR, 0.5f)).setFont(fontToUse);

	        loanTable.addCell(statusLabelCell);
	        loanTable.addCell(statusValueCell);
	        document.add(loanTable);

	        Paragraph confirmationParagraph = new Paragraph(
	                "We hereby confirm that the above loan account has been closed in full and that, as of "
	                        + closingDateFormatted
	                        + ", there are no outstanding dues payable by you against this loan. This closure has been processed in accordance with the terms and conditions agreed at the time of sanction.")
	                .setFontSize(11).setTextAlignment(TextAlignment.JUSTIFIED).setMarginBottom(10).setFont(fontToUse);
	        document.add(confirmationParagraph);

	        Paragraph appreciationParagraph = new Paragraph(
	                "We appreciate your timely repayments and thank you for choosing PawnPrime Loan Services. Please retain this Loan Closure Statement for your records and future reference.")
	                .setFontSize(10).setTextAlignment(TextAlignment.JUSTIFIED).setMarginBottom(12).setFont(fontToUse);
	        document.add(appreciationParagraph);

	        Table closureSummaryTable = new Table(new float[] { 3, 3 }).useAllAvailableWidth();
	        closureSummaryTable.setMarginBottom(15);

	        addTableRow(closureSummaryTable, "Outstanding Balance", "₹ 0.00", true, fontToUse);
	        addTableRow(closureSummaryTable, "Account Status", "Closed - No Dues", false, fontToUse);

	        document.add(closureSummaryTable);

	        Paragraph repaymentHeader = new Paragraph("Payment History").setFontSize(12).setBold()
	                .setFontColor(PRIMARY_COLOR).setMarginTop(10).setMarginBottom(10).setFont(fontToUse);
	        document.add(repaymentHeader);

	        if (!repayments.isEmpty()) {
	            Table repaymentTable = new Table(new float[] { 2, 2, 2, 2, 2 }).useAllAvailableWidth();
	            repaymentTable.setMarginBottom(15);

	            addTableRow(repaymentTable, "Scheduled", "Paid", "Principal", "Interest", "Total", "Method", true, fontToUse);
	            for (RepaymentTransaction transaction : repayments) {
	                addTableRow(repaymentTable,
	                        transaction.getDate() != null ? transaction.getDate().toString() : "N/A",
	                        transaction.getPaidDate() != null ? transaction.getPaidDate().toString() : "N/A",
	                        "₹ " + String.format("%,.2f", transaction.getPrincipalAmt()),
	                        "₹ " + String.format("%,.2f", transaction.getInterestAmt()),
	                        "₹ " + String.format("%,.2f", transaction.getTotalAmt()),
	                        transaction.getType() != null ? transaction.getType().toString() : "N/A",
	                        false, fontToUse);
	            }
	            document.add(repaymentTable);
	        } else {
	            Paragraph noRepayments = new Paragraph("No payments recorded.").setFontSize(10)
	                    .setTextAlignment(TextAlignment.CENTER).setFont(fontToUse);
	            document.add(noRepayments);
	        }

	        // -------- Added New Closure Letter Section (as requested) --------
	        Paragraph closureLetter = new Paragraph(
	                "Dear Customer,\n\n"
	                + "We sincerely appreciate the opportunity to serve you. Thank you for placing your trust in PawnPrime Loan Services. "
	                + "We hope your experience with us has been smooth, transparent, and satisfactory.\n\n"
	                + "We are pleased to inform you that your loan has been officially closed as of " + closingDateFormatted + ". "
	                + "There are no outstanding dues remaining on your loan account.\n\n"
	                + "However, certain post-closure formalities may require your attention:\n\n"
	                + "Unclaimed Documents & Security Items:\n"
	                + "If any of your pledged items, post-dated cheques, or security documents were held with us, they will be safely "
	                + "stored at our branch for a period of 45 days from the date of closure. After this period, the documents will "
	                + "be securely destroyed in accordance with compliance norms, unless collected by you.\n\n"
	                + "If this loan was closed through the pre-payment process and any EMI is presented based on the existing payment "
	                + "schedule, the credited amount will be refunded within 7–10 working days through direct bank transfer or Demand Draft.\n\n"
	                + "If you require any additional support or assistance regarding this loan or future services, our team will be pleased to help you.\n\n"
	                + "Warm Regards,\n"
	                + "PawnPrime Loan Services")
	        .setFontSize(10)
	        .setTextAlignment(TextAlignment.JUSTIFIED)
	        .setMarginTop(20)
	        .setMarginBottom(15)
	        .setFont(fontToUse);

	        document.add(closureLetter);
	        // -----------------------------------------------------------------

	        Paragraph signForCompany = new Paragraph("For PawnPrime Loan Services")
	                .setFontSize(10)
	                .setBold()
	                .setTextAlignment(TextAlignment.LEFT)
	                .setMarginTop(20)
	                .setFont(fontToUse);
	        document.add(signForCompany);

	        Paragraph signDesignation = new Paragraph("Authorised Signatory")
	                .setFontSize(9)
	                .setTextAlignment(TextAlignment.LEFT)
	                .setMarginTop(25)
	                .setFont(fontToUse);
	        document.add(signDesignation);

	        Paragraph footerNote = new Paragraph(
	                "Pledged items can be collected within 7 days with valid ID at any branch.")
	                .setFontSize(9)
	                .setFontColor(SECONDARY_COLOR)
	                .setTextAlignment(TextAlignment.CENTER)
	                .setMarginTop(15)
	                .setFont(fontToUse);
	        document.add(footerNote);

	        Paragraph disclaimer = new Paragraph(
	                "Disclaimer: This is a computer-generated statement based on our records and does not require a physical signature or seal. "
	                + "In case of any discrepancy, please contact our branch within 7 days from the date of issue.")
	                .setFontSize(8)
	                .setFontColor(SECONDARY_COLOR)
	                .setTextAlignment(TextAlignment.CENTER)
	                .setMarginTop(5)
	                .setFont(fontToUse);
	        document.add(disclaimer);

	        document.close();

	    } catch (Exception e) {
	        System.err.println("ITEXT PDF ERROR: " + e.getMessage());
	        throw new RuntimeException("Failed to generate close loan receipt PDF with iText.", e);
	    }
	}


	public void exportRepaymentReceipts(Long loanId, OutputStream outputStream) throws IOException {
		try {
			Loan loan = loanRepo.findById(loanId)
					.orElseThrow(() -> new RuntimeException("Loan not found with ID: " + loanId));
			Customer customer = loan.getCustomer();
			List<RepaymentTransaction> repayments = repaymentRepo.findByLoanId(loanId);

			PdfWriter writer = new PdfWriter(outputStream);
			PdfDocument pdf = new PdfDocument(writer);
			Document document = new Document(pdf);
			document.setMargins(20, 20, 20, 20);

			PdfFont fontToUse = (notoSansFont != null) ? notoSansFont : PdfFontFactory.createFont();

			for (RepaymentTransaction transaction : repayments) {
				document.add(new Paragraph("PAWNPRIME PAYMENT RECEIPT").setFontSize(16).setBold()
						.setFontColor(PRIMARY_COLOR).setTextAlignment(TextAlignment.CENTER).setFont(fontToUse));

				document.add(new Table(1).useAllAvailableWidth().addCell(new Cell().add(new Paragraph("")).setHeight(1)
						.setBackgroundColor(ACCENT_COLOR).setBorder(null)));
				String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a"))
						+ " IST";
				document.add(new Paragraph("Receipt Date: " + currentDate).setFontSize(9).setFontColor(SECONDARY_COLOR)
						.setTextAlignment(TextAlignment.RIGHT).setFont(fontToUse));

				document.add(new Paragraph("Receipt No: RCPT-" + transaction.getId() + "-" + LocalDate.now().getYear())
						.setFontSize(10).setBold().setFontColor(PRIMARY_COLOR).setTextAlignment(TextAlignment.CENTER)
						.setMarginBottom(15).setFont(fontToUse));

				document.add(new Paragraph("Client: " + customer.getName()
						+ (customer.getMiddleName() != null ? " " + customer.getMiddleName() : "")
						+ (customer.getLastName() != null ? " " + customer.getLastName() : "") + " | Contact: "
						+ customer.getMobile()).setFontSize(12).setFontColor(PRIMARY_COLOR).setMarginBottom(15)
						.setFont(fontToUse));

				Table transactionTable = new Table(new float[] { 2, 3 }).useAllAvailableWidth();
				transactionTable.setMarginBottom(15);

				addTableRow(transactionTable, "Transaction ID", String.valueOf(transaction.getId()), true, fontToUse);
				addTableRow(transactionTable, "Loan ID", String.valueOf(loan.getId()), false, fontToUse);
				addTableRow(transactionTable, "Scheduled Date",
						transaction.getDate() != null ? transaction.getDate().toString() : "N/A", true, fontToUse);
				addTableRow(transactionTable, "Paid Date",
						transaction.getPaidDate() != null ? transaction.getPaidDate().toString() : "N/A", false,
						fontToUse);
				addTableRow(transactionTable, "Principal", "₹ " + String.format("%,.2f", transaction.getPrincipalAmt()),
						true, fontToUse);
				addTableRow(transactionTable, "Interest", "₹ " + String.format("%,.2f", transaction.getInterestAmt()),
						false, fontToUse);
				addTableRow(transactionTable, "Total Amount", "₹ " + String.format("%,.2f", transaction.getTotalAmt()),
						true, fontToUse);
				addTableRow(transactionTable, "Payment Method",
						transaction.getType() != null ? transaction.getType().toString() : "N/A", false, fontToUse);

				document.add(transactionTable);

				document.add(new Paragraph(
						"This receipt confirms the payment transaction for the associated loan. Please retain this document for your records.")
						.setFontSize(9).setFontColor(SECONDARY_COLOR).setTextAlignment(TextAlignment.CENTER)
						.setMarginTop(15).setFont(fontToUse));

				if (repayments.indexOf(transaction) < repayments.size() - 1) {
					document.add(new Paragraph("---").setFontSize(12).setBold().setTextAlignment(TextAlignment.CENTER)
							.setMarginTop(20).setMarginBottom(20).setFont(fontToUse));
				}
			}

			document.close();

		} catch (Exception e) {
			System.err.println("ITEXT PDF ERROR: " + e.getMessage());
			throw new RuntimeException("Failed to generate repayment receipts PDF with iText.", e);
		}
	}

	private void addTableRow(Table table, String label, String value, boolean alternateColor, PdfFont font) {
		DeviceRgb bgColor = alternateColor ? HEADER_BG : new DeviceRgb(255, 255, 255);

		Cell labelCell = new Cell().add(new Paragraph(label).setBold().setFontSize(9).setFont(font))
				.setBackgroundColor(bgColor).setPadding(5).setBorder(new SolidBorder(SECONDARY_COLOR, 0.5f));

		Cell valueCell = new Cell().add(new Paragraph(value).setFontSize(9).setFont(font)).setBackgroundColor(bgColor)
				.setPadding(5).setBorder(new SolidBorder(SECONDARY_COLOR, 0.5f));

		table.addCell(labelCell);
		table.addCell(valueCell);
	}

	private void addTableRow(Table table, String scheduledDate, String paidDate, String principal, String interest,
			String total, String type, boolean alternateColor, PdfFont font) {
		DeviceRgb bgColor = alternateColor ? HEADER_BG : new DeviceRgb(255, 255, 255);

		Cell scheduledDateCell = new Cell().add(new Paragraph(scheduledDate).setFontSize(9).setFont(font))
				.setBackgroundColor(bgColor).setPadding(5).setBorder(new SolidBorder(SECONDARY_COLOR, 0.5f));

		Cell paidDateCell = new Cell().add(new Paragraph(paidDate).setFontSize(9).setFont(font))
				.setBackgroundColor(bgColor).setPadding(5).setBorder(new SolidBorder(SECONDARY_COLOR, 0.5f));

		Cell principalCell = new Cell().add(new Paragraph(principal).setFontSize(9).setFont(font))
				.setBackgroundColor(bgColor).setPadding(5).setBorder(new SolidBorder(SECONDARY_COLOR, 0.5f));

		Cell interestCell = new Cell().add(new Paragraph(interest).setFontSize(9).setFont(font))
				.setBackgroundColor(bgColor).setPadding(5).setBorder(new SolidBorder(SECONDARY_COLOR, 0.5f));

		Cell totalCell = new Cell().add(new Paragraph(total).setFontSize(9).setFont(font)).setBackgroundColor(bgColor)
				.setPadding(5).setBorder(new SolidBorder(SECONDARY_COLOR, 0.5f));

		Cell typeCell = new Cell().add(new Paragraph(type).setFontSize(9).setFont(font)).setBackgroundColor(bgColor)
				.setPadding(5).setBorder(new SolidBorder(SECONDARY_COLOR, 0.5f));

		table.addCell(scheduledDateCell);
		table.addCell(paidDateCell);
		table.addCell(principalCell);
		table.addCell(interestCell);
		// table.addCell(totalCell);
		table.addCell(typeCell);
	}

	public void exportRepaymentReceiptForInstallment(Long loanId, int installmentNo, OutputStream outputStream)
			throws IOException {
		try {
			Loan loan = loanRepo.findById(loanId)
					.orElseThrow(() -> new RuntimeException("Loan not found with ID: " + loanId));

			Customer customer = loan.getCustomer();
			List<RepaymentTransaction> repayments = repaymentRepo.findByLoanId(loanId);

			if (repayments.isEmpty()) {
				throw new RuntimeException("No repayments found for loan ID: " + loanId);
			}

			// Sort repayments by paidDate (or scheduled date if paidDate is null)
			repayments.sort(Comparator.comparing(rt -> rt.getPaidDate() != null ? rt.getPaidDate() : rt.getDate()));

			if (installmentNo < 1 || installmentNo > repayments.size()) {
				throw new RuntimeException("Invalid installment number. Total installments: " + repayments.size());
			}

			// nth installment (1-based index)
			RepaymentTransaction transaction = repayments.get(installmentNo - 1);

			PdfWriter writer = new PdfWriter(outputStream);
			PdfDocument pdf = new PdfDocument(writer);
			Document document = new Document(pdf);
			document.setMargins(20, 20, 20, 20);

			PdfFont fontToUse = (notoSansFont != null) ? notoSansFont : PdfFontFactory.createFont();

			// ---------- HEADER ----------
			document.add(new Paragraph("PAWNPRIME PAYMENT RECEIPT").setFontSize(16).setBold()
					.setFontColor(PRIMARY_COLOR).setTextAlignment(TextAlignment.CENTER).setFont(fontToUse));

			document.add(new Table(1).useAllAvailableWidth().addCell(
					new Cell().add(new Paragraph("")).setHeight(1).setBackgroundColor(ACCENT_COLOR).setBorder(null)));

			String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a"))
					+ " IST";

			document.add(new Paragraph("Receipt Date: " + currentDateTime).setFontSize(9).setFontColor(SECONDARY_COLOR)
					.setTextAlignment(TextAlignment.RIGHT).setFont(fontToUse));

			document.add(new Paragraph("Receipt No: RCPT-" + transaction.getId() + "-" + LocalDate.now().getYear())
					.setFontSize(10).setBold().setFontColor(PRIMARY_COLOR).setTextAlignment(TextAlignment.CENTER)
					.setMarginBottom(10).setFont(fontToUse));

			// Show which installment this is
			document.add(new Paragraph("Installment No: " + installmentNo).setFontSize(10).setBold()
					.setFontColor(ColorConstants.BLACK).setTextAlignment(TextAlignment.CENTER).setMarginBottom(10)
					.setFont(fontToUse));

			// ---------- CUSTOMER INFO ----------
			document.add(new Paragraph("Client: " + customer.getName()
					+ (customer.getMiddleName() != null ? " " + customer.getMiddleName() : "")
					+ (customer.getLastName() != null ? " " + customer.getLastName() : "") + " | Contact: "
					+ customer.getMobile()).setFontSize(12).setFontColor(PRIMARY_COLOR).setMarginBottom(15)
					.setFont(fontToUse));

			// ---------- TRANSACTION TABLE ----------
			Table transactionTable = new Table(new float[] { 2, 3 }).useAllAvailableWidth();
			transactionTable.setMarginBottom(15);

			addTableRow(transactionTable, "Transaction ID", String.valueOf(transaction.getId()), true, fontToUse);
			addTableRow(transactionTable, "Loan ID", String.valueOf(loan.getId()), false, fontToUse);
			addTableRow(transactionTable, "Scheduled Date",
					transaction.getDate() != null ? transaction.getDate().toString() : "N/A", true, fontToUse);
			addTableRow(transactionTable, "Paid Date",
					transaction.getPaidDate() != null ? transaction.getPaidDate().toString() : "N/A", false, fontToUse);
			addTableRow(transactionTable, "Principal", "₹ " + String.format("%,.2f", transaction.getPrincipalAmt()),
					true, fontToUse);
			addTableRow(transactionTable, "Interest", "₹ " + String.format("%,.2f", transaction.getInterestAmt()),
					false, fontToUse);
			addTableRow(transactionTable, "Total Amount", "₹ " + String.format("%,.2f", transaction.getTotalAmt()),
					true, fontToUse);
			addTableRow(transactionTable, "Payment Method",
					transaction.getType() != null ? transaction.getType().toString() : "N/A", false, fontToUse);

			document.add(transactionTable);

			// ---------- FOOTER ----------
			document.add(new Paragraph("This receipt confirms the payment transaction for the associated loan. "
					+ "Please retain this document for your records.").setFontSize(9).setFontColor(SECONDARY_COLOR)
					.setTextAlignment(TextAlignment.CENTER).setMarginTop(15).setFont(fontToUse));

			document.close();

		} catch (Exception e) {
			System.err.println("ITEXT PDF ERROR (single installment): " + e.getMessage());
			throw new RuntimeException("Failed to generate repayment receipt PDF for installment.", e);
		}
	}

}