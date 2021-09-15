package com.example.bill.controller;

import com.example.bill.dto.BillRequestDto;
import com.example.bill.dto.BillResponseDto;
import com.example.bill.service.BillService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping("/{billId}")
    public BillResponseDto getBill(@PathVariable Long billId) {
        return new BillResponseDto(billService.getBillById(billId));
    }

    @PostMapping("/")
    public Long createBill(@RequestBody BillRequestDto billRequestDto) {
        return billService.createBill(billRequestDto.getAccountId(),
                billRequestDto.getAmount(), billRequestDto.getIsDefault(), billRequestDto.getOverdraftEnabled());
    }

    @PutMapping("/{billId}")
    public BillResponseDto updateBill(@PathVariable Long billId,
                                      @RequestBody BillRequestDto billRequestDto) {
        return new BillResponseDto(billService.updateBill(billId, billRequestDto.getAccountId(),
                billRequestDto.getAmount(), billRequestDto.getIsDefault(), billRequestDto.getOverdraftEnabled()));
    }

    @DeleteMapping("/{billId}")
    public BillResponseDto deleteBill(@PathVariable Long billId) {
        return new BillResponseDto(billService.deleteBill(billId));
    }

    @GetMapping("/account/{accountId}")
    public List<BillResponseDto> getBillsByAccountId(@PathVariable Long accountId) {
        return billService.getBillsByAccountId(accountId)
                .stream()
                .map(BillResponseDto::new)
                .collect(Collectors.toList());
    }
}
