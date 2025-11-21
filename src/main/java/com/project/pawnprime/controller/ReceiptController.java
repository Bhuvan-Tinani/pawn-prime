package com.project.pawnprime.controller;

import com.project.pawnprime.service.ReceiptService;
import com.project.pawnprime.model.RepaymentTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/receipt")
public class ReceiptController {

    private final ReceiptService receiptService;

    @Autowired
    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping(value = "/noc/jas/{loanId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getNoc(@PathVariable Long loanId) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        receiptService.exportNoc(loanId, outputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "noc_" + loanId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }

    @GetMapping(value = "/noc/{loanId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getNocWithIText(@PathVariable Long loanId) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        receiptService.exportNocWithIText(loanId, outputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "noc_itext_" + loanId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }

    @GetMapping(value = "/close-loan/{loanId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getCloseLoanReceipt(@PathVariable Long loanId) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        receiptService.exportCloseLoanReceipt(loanId, outputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "close_loan_" + loanId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }

    @GetMapping(value = "/repayment/{loanId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getRepaymentReceipts(@PathVariable Long loanId) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        receiptService.exportRepaymentReceipts(loanId, outputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "repayment_" + loanId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }
}