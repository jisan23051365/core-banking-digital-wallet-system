package com.bank.corebanking.controller;

import com.bank.corebanking.entity.Wallet;
import com.bank.corebanking.service.WalletService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService service;

    public WalletController(WalletService service) {
        this.service = service;
    }

    @PostMapping
    public Wallet createWallet(@RequestBody Wallet wallet){
        return service.createWallet(wallet);
    }

    @GetMapping("/{walletNumber}")
    public Wallet getWallet(@PathVariable String walletNumber){
        return service.getWallet(walletNumber);
    }
}