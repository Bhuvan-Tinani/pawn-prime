package com.project.pawnprime.controller;

import com.project.pawnprime.dto.CloseLoanDTO;
import com.project.pawnprime.service.CloseLoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/close-loan")
public class CloseLoanController {

    @Autowired
    private CloseLoanService closeLoanService;

    @PostMapping
    public ResponseEntity<CloseLoanDTO> closeLoan(@RequestBody CloseLoanDTO closeLoanDTO) {
        try {
            CloseLoanDTO responseDTO = closeLoanService.closeLoan(closeLoanDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CloseLoanDTO());
        }
    }
}