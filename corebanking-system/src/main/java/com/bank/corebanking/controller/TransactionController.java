package com.bank.corebanking.controller;

import com.bank.corebanking.dto.TransferRequest;
import com.bank.corebanking.entity.Transaction;
import com.bank.corebanking.service.TransactionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping("/transfer")
    public Transaction transfer(@RequestBody TransferRequest request){

        return service.transfer(
                request.getFromAccount(),
                request.getToAccount(),
                request.getAmount()
        );
    }
}