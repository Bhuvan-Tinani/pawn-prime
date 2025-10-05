package com.project.pawnprime.service;

import com.project.pawnprime.model.Loan;
import com.project.pawnprime.repo.LoanRepository;
import com.project.pawnprime.repo.RepaymentTransactionRepository;

import net.sf.jasperreports.engine.JRAbstractExporter;
// Jasper imports
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReceiptService {

    private final RepaymentTransactionRepository repaymentRepo;
    private final LoanRepository loanRepo;
    private final ResourceLoader resourceLoader;

    public ReceiptService(RepaymentTransactionRepository repaymentRepo,
                          LoanRepository loanRepo,
                          ResourceLoader resourceLoader) {
        this.repaymentRepo = repaymentRepo;
        this.loanRepo = loanRepo;
        this.resourceLoader = resourceLoader;
    }

    public void exportLoanReceipt(Long loanId, OutputStream outputStream) throws IOException {

        // 3. Load, Fill, and Export Report
        try {
            // Load the compiled report file (.jasper) from the classpath
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(
                    resourceLoader.getResource("classpath:reports/receipt.jasper").getInputStream()
            );
            // Export to PDF
            net.sf.jasperreports.pdf.JRPdfExporter exporter = new net.sf.jasperreports.pdf.JRPdfExporter();
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            exporter.exportReport();

        } catch (JRException e) {
            // Catch JasperReports specific exceptions
            System.err.println("JASPER REPORTS ERROR: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate PDF receipt due to reporting error.", e);
        }
    }
}