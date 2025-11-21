package com.project.pawnprime.controller;

import com.project.pawnprime.dto.loanDTO.LoanCalcRequest;
import com.project.pawnprime.dto.loanDTO.LoanCalcResponse;
import com.project.pawnprime.dto.loanDTO.LoanDTO;
import com.project.pawnprime.dto.loanDTO.LoanRequestStatus;
import com.project.pawnprime.dto.loanDTO.LoanScheduleDTO;
import com.project.pawnprime.dto.loanDTO.LoanSummaryDTO;
import com.project.pawnprime.mapper.LoanMapper;
import com.project.pawnprime.model.Agent;
import com.project.pawnprime.model.Loan;
import com.project.pawnprime.service.AgentService;
import com.project.pawnprime.service.LoanService;
import com.project.pawnprime.service.RepaymentTransactionService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.ContentDisposition;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/loans")
@PreAuthorize("hasRole('AGENT','ADMIN')")
public class LoanController {

    private final LoanService loanService;
    private final AgentService agentService;
    private final RepaymentTransactionService repaymentTransService;

    public LoanController(LoanService loanService, AgentService agentService,
            RepaymentTransactionService repaymentTransService) {
        this.loanService = loanService;
        this.agentService = agentService;
        this.repaymentTransService = repaymentTransService;
    }

    // Create loan for a customer
    @PostMapping("/customer/{customerId}")
    public LoanDTO createLoan(@PathVariable Long customerId, @RequestBody LoanDTO loanDTO) {
        loanDTO.setDate(LocalDate.now()); // Always set today's date from server
        Loan loan = LoanMapper.toEntity(loanDTO); // DTO → Entity
        Agent agent = agentService.getAgentById(loanDTO.getAgentId()).orElse(null);
        if (agent != null) {
            loan.setAgent(agent);
            Loan savedLoan = loanService.createLoan(customerId, loan);
            return LoanMapper.toDTO(savedLoan); // Entity → DTO
        }
        return null;
    }

    // Get all loans
    @GetMapping
    public List<LoanDTO> getAllLoans() {
        return loanService.getAllLoans().stream().map(LoanMapper::toDTO).collect(Collectors.toList());
    }

    // Get loans for a specific customer
    @GetMapping("/customer/{customerId}")
    public List<LoanDTO> getLoansByCustomer(@PathVariable Long customerId) {
        return loanService.getLoansByCustomer(customerId).stream().map(LoanMapper::toDTO).collect(Collectors.toList());
    }

    // Get loan by ID
    @GetMapping("/{loanId}")
    public LoanDTO getLoanById(@PathVariable Long loanId) {
        Loan loan = loanService.getLoanById(loanId);
        return LoanMapper.toDTO(loan);
    }

    // Update loan
    @PutMapping("/{loanId}")
    public LoanDTO updateLoan(@PathVariable Long loanId, @RequestBody LoanDTO loanDTO) {
        Loan loan = LoanMapper.toEntity(loanDTO);
        Loan updatedLoan = loanService.updateLoan(loanId, loan);
        return LoanMapper.toDTO(updatedLoan);
    }

    // Delete loan
    @DeleteMapping("/{loanId}")
    public String deleteLoan(@PathVariable Long loanId) {
        loanService.deleteLoan(loanId);
        return "Loan deleted successfully!";
    }

    @GetMapping("/{loanId}/schedule")
    public List<LoanScheduleDTO> getLoanSchedule(@PathVariable Long loanId) {
        Loan loan = loanService.getLoanById(loanId);
        double principal = loan.getLoanVal();
        double annualRate = loan.getInterestRate(); // e.g., 4%
        int months = loan.getDuration();
        LocalDate startDate = loan.getDate();

        // simple interest total
        double totalInterest = (principal * annualRate * (months / 12.0)) / 100;
        double totalPayable = principal + totalInterest;

        // monthly breakup
        double monthlyPrincipal = principal / months;
        double monthlyInterest = totalInterest / months;
        double monthlyInstallment = monthlyPrincipal + monthlyInterest;

        List<LoanScheduleDTO> schedule = new ArrayList<>();
        double balance = totalPayable;

        for (int i = 1; i <= months; i++) {
            balance -= monthlyInstallment;

            LoanScheduleDTO dto = new LoanScheduleDTO();
            dto.setInstallmentNo(i);
            dto.setLoanId(loan.getId());
            dto.setPrincipalAmount(Math.round(monthlyPrincipal * 100.0) / 100.0);
            dto.setInterestAmount(Math.round(monthlyInterest * 100.0) / 100.0);
            dto.setTotalInstallment(Math.round(monthlyInstallment * 100.0) / 100.0);
            dto.setRemainingAmount(Math.max(0, Math.round(balance * 100.0) / 100.0));
            dto.setDate(startDate.plusMonths(i));

            schedule.add(dto);
        }

        return schedule;
    }

    // Modified calculator endpoint with ornament calculation
    @PostMapping("/LoanCalculator")
    public List<LoanCalcResponse> loanCalculator(@RequestBody LoanCalcRequest request) {
        return loanService.calculateSchedule(request);
    }

    // NEW ENDPOINT: Fetch fresh prices from external API (called after login)
    @PostMapping("/fetch-prices")
    public Map<String, Object> fetchFreshPrices() {
        try {
            Map<String, Double> prices = loanService.fetchAndCachePrices();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("prices", prices);
            response.put("lastUpdated", loanService.getLastFetchedTime());
            response.put("message", "Prices updated successfully");
            response.put("source", "MetalpriceAPI");
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Failed to fetch prices: " + e.getMessage());
            response.put("fallback", true);
            return response;
        }
    }

    // MODIFIED: Get current cached prices (used by Calculator and Dashboard)
    @GetMapping("/prices")
    public Map<String, Object> getCurrentPrices() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Double> prices = new HashMap<>();
        prices.put("gold", loanService.getCurrentGoldPrice());
        prices.put("silver", loanService.getCurrentSilverPrice());

        response.put("success", true);
        response.put("prices", prices);
        response.put("lastUpdated", loanService.getLastFetchedTime());
        response.put("source", "cached");
        return response;
    }

    // Optional: Endpoint to get price for specific type
    @GetMapping("/prices/{type}")
    public Map<String, Object> getPrice(@PathVariable String type) {
        Map<String, Object> response = new HashMap<>();
        try {
            double price;
            if ("gold".equalsIgnoreCase(type)) {
                price = loanService.getCurrentGoldPrice();
            } else if ("silver".equalsIgnoreCase(type)) {
                price = loanService.getCurrentSilverPrice();
            } else {
                response.put("error", "Invalid type. Use 'gold' or 'silver'");
                return response;
            }

            response.put("success", true);
            response.put("type", type.toLowerCase());
            response.put("pricePerGram", price);
            response.put("currency", "INR");
            response.put("lastUpdated", loanService.getLastFetchedTime());

        } catch (Exception e) {
            response.put("error", "Failed to get price for " + type);
        }

        return response;
    }

    @GetMapping("/agent/{agentId}")
    public List<LoanDTO> getLoansByAgent(@PathVariable Long agentId) {
        List<Loan> loans = loanService.getLoansByAgentId(agentId);
        return LoanMapper.toDTOList(loans);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanRequestStatus>> getLoanByStatus(@PathVariable String status) {
        List<Loan> loanlist = loanService.getLoanByStatus(status);
        return ResponseEntity.ok(LoanMapper.toAdminLoanDTOList(loanlist));
    }

    @PostMapping("/status/{loanId}")
    public boolean updateLoanStatus(@PathVariable Long loanId, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status.equals("approved") || status.equals("pending") || status.equals("rejected")) {
            return loanService.changeLoanStatus(loanId, status);
        }
        return false;
    }

    @GetMapping("/{loanId}/repayment")
    public List<LoanScheduleDTO> getLoanRepayment(@PathVariable Long loanId) {
        Loan loan = loanService.getByStatusAndId(loanId, "T_DONE");
        if (loan == null) {
            return null;
        }

        int paidLoan = (int) repaymentTransService.getRepaymentCountForLoan(loanId);

        double principal = loan.getLoanVal();
        double annualRate = loan.getInterestRate(); // e.g., 4%
        int months = loan.getDuration();
        LocalDate startDate = loan.getDate();

        // simple interest total
        double totalInterest = (principal * annualRate * (months / 12.0)) / 100;
        double totalPayable = principal + totalInterest;

        // monthly breakup
        double monthlyPrincipal = principal / months;
        double monthlyInterest = totalInterest / months;
        double monthlyInstallment = monthlyPrincipal + monthlyInterest;

        List<LoanScheduleDTO> schedule = new ArrayList<>();
        double balance = totalPayable;

        for (int i = 1; i <= months; i++) {
            balance -= monthlyInstallment;

            LoanScheduleDTO dto = new LoanScheduleDTO();
            dto.setInstallmentNo(i);
            dto.setLoanId(loan.getId());
            dto.setPrincipalAmount(Math.round(monthlyPrincipal * 100.0) / 100.0);
            dto.setInterestAmount(Math.round(monthlyInterest * 100.0) / 100.0);
            dto.setTotalInstallment(Math.round(monthlyInstallment * 100.0) / 100.0);
            dto.setRemainingAmount(Math.max(0, Math.round(balance * 100.0) / 100.0));
            dto.setDate(startDate.plusMonths(i));

            // Set status based on paid installments count
            if (i <= paidLoan) {
                dto.setStatus("PAID");
            } else {
                dto.setStatus("PENDING");
            }

            schedule.add(dto);
        }

        return schedule;
    }

    // Search loans by customer Aadhar number
    @GetMapping("/search-summary")
    public List<LoanSummaryDTO> getLoansSummaryByAadhar(@RequestParam String aadharNo) {
        List<Loan> loans = loanService.getLoansByCustomerAadhar(aadharNo);
        return LoanMapper.toSummaryDTOList(loans);
    }

    @GetMapping("/{loanId}/receipt")
    public ResponseEntity<byte[]> generateReceipt(@PathVariable Long loanId) {
        Loan loan = loanService.getLoanById(loanId);
        if (loan == null) {
            return ResponseEntity.notFound().build();
        }
        List<LoanScheduleDTO> schedule = this.getLoanSchedule(loanId);
        try {
            byte[] pdfBytes = generatePdf(loan, schedule);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("PawnPrime_Receipt_" + loanId + ".pdf").build());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private byte[] generatePdf(Loan loan, List<LoanScheduleDTO> schedule) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        
        // Setting up fonts
        PdfFont regularFont = PdfFontFactory.createFont("Helvetica");
        PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");

        // Define custom colors
        Color primaryColor = new DeviceRgb(0, 102, 204); // Blue for branding
        Color accentColor = new DeviceRgb(230, 230, 250); // Light lavender background
        Color textColor = ColorConstants.BLACK;

        // Creating document
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(30, 30, 30, 30);

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
        leftHeader.add(new Paragraph(new Text("Loan Receipt").setFont(regularFont).setFontSize(14).setFontColor(textColor)));

        Cell rightHeader = new Cell();
        rightHeader.setBorder(Border.NO_BORDER);
        rightHeader.setPadding(10);
        rightHeader.setTextAlignment(TextAlignment.RIGHT);
        rightHeader.add(new Paragraph(new Text("Receipt Date").setFont(regularFont).setFontSize(10).setFontColor(textColor)));
        rightHeader.add(new Paragraph(new Text(LocalDate.now().format(dateFormatter)).setFont(boldFont).setFontSize(12).setFontColor(textColor)));
        rightHeader.add(new Paragraph(new Text("Receipt No.").setFont(regularFont).setFontSize(10).setFontColor(textColor)));
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

        // Add cells for loan info
        addKeyValueCell(loanInfoTable, "Loan ID:", String.valueOf(loan.getId()), regularFont, boldFont, accentColor, textColor);
       // addKeyValueCell(loanInfoTable, "Customer:", loan.getCustomerName(), regularFont, boldFont, accentColor, textColor);
       // addKeyValueCell(loanInfoTable, "Customer ID:", String.valueOf(loan.getCustomerId()), regularFont, boldFont, accentColor, textColor);
       // addKeyValueCell(loanInfoTable, "Agent ID:", String.valueOf(loan.getAgentId()), regularFont, boldFont, accentColor, textColor);
        addKeyValueCell(loanInfoTable, "Status:", loan.getLoanStatus(), regularFont, boldFont, accentColor, textColor);
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

        // Repayment Schedule Section
        document.add(new Paragraph("Repayment Schedule")
                .setFont(boldFont)
                .setFontSize(16)
                .setFontColor(primaryColor)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20)
                .setMarginBottom(10));
        
        Table scheduleTable = new Table(UnitValue.createPercentArray(new float[]{10, 20, 20, 20, 20, 20}));
        scheduleTable.setWidth(UnitValue.createPercentValue(100));
        scheduleTable.setMarginBottom(20);

        // Header row
        addHeaderCell(scheduleTable, "#", boldFont, primaryColor, ColorConstants.WHITE);
        addHeaderCell(scheduleTable, "Date", boldFont, primaryColor, ColorConstants.WHITE);
        addHeaderCell(scheduleTable, "Principal", boldFont, primaryColor, ColorConstants.WHITE);
        addHeaderCell(scheduleTable, "Interest", boldFont, primaryColor, ColorConstants.WHITE);
        addHeaderCell(scheduleTable, "Installment", boldFont, primaryColor, ColorConstants.WHITE);
        addHeaderCell(scheduleTable, "Remaining", boldFont, primaryColor, ColorConstants.WHITE);

        // Data rows with alternating background
        for (int i = 0; i < schedule.size(); i++) {
            LoanScheduleDTO item = schedule.get(i);
            Color rowColor = (i % 2 == 0) ? ColorConstants.WHITE : new DeviceRgb(245, 245, 250);
            addDataCell(scheduleTable, String.valueOf(item.getInstallmentNo()), TextAlignment.CENTER, regularFont, textColor, rowColor);
            addDataCell(scheduleTable, item.getDate().format(dateFormatter), TextAlignment.CENTER, regularFont, textColor, rowColor);
            addDataCell(scheduleTable, currencyFormat.format(item.getPrincipalAmount()), TextAlignment.RIGHT, regularFont, textColor, rowColor);
            addDataCell(scheduleTable, currencyFormat.format(item.getInterestAmount()), TextAlignment.RIGHT, regularFont, textColor, rowColor);
            addDataCell(scheduleTable, currencyFormat.format(item.getTotalInstallment()), TextAlignment.RIGHT, regularFont, textColor, rowColor);
            addDataCell(scheduleTable, currencyFormat.format(item.getRemainingAmount()), TextAlignment.RIGHT, regularFont, textColor, rowColor);
        }

        document.add(scheduleTable);

        // Footer
        document.add(new Paragraph("Thank you for choosing PawnPrime")
                .setFont(boldFont)
                .setFontSize(12)
                .setFontColor(primaryColor)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20));
        document.add(new Paragraph("This is a computer-generated receipt and does not require a signature")
                .setFont(regularFont)
                .setFontSize(10)
                .setFontColor(textColor)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Secure Transaction")
                .setFont(regularFont)
                .setFontSize(10)
                .setFontColor(textColor)
                .setTextAlignment(TextAlignment.CENTER));

        document.close();
        return baos.toByteArray();
    }

    private void addKeyValueCell(Table table, String key, String value, PdfFont regularFont, PdfFont boldFont, Color keyBgColor, Color textColor) {
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

    private void addHeaderCell(Table table, String text, PdfFont boldFont, Color bgColor, Color fontColor) {
        Cell cell = new Cell().add(new Paragraph(text).setFont(boldFont).setFontSize(11).setFontColor(fontColor));
        cell.setBackgroundColor(bgColor);
        cell.setTextAlignment(TextAlignment.CENTER);
        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        cell.setPadding(10);
        cell.setBorder(new SolidBorder(0.5f));
        table.addCell(cell);
    }

    private void addDataCell(Table table, String text, TextAlignment alignment, PdfFont regularFont, Color textColor, Color bgColor) {
        Cell cell = new Cell().add(new Paragraph(text).setFont(regularFont).setFontSize(10).setFontColor(textColor));
        cell.setTextAlignment(alignment);
        cell.setPadding(8);
        cell.setBorder(new SolidBorder(0.5f));
        cell.setBackgroundColor(bgColor);
        table.addCell(cell);
    }
}