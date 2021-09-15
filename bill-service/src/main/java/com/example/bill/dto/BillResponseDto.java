package com.example.bill.dto;

import com.example.bill.entity.Bill;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class BillResponseDto {

        private Long billId;
        private Long accountId;
        private BigDecimal amount;
        private Boolean isDefault;
        private OffsetDateTime creationDate;
        private Boolean overdraftEnabled;

    public BillResponseDto(Bill bill) {
        billId = bill.getBillId();
        accountId = bill.getAccountId();
        isDefault = bill.getIsDefault();
        amount = bill.getAmount();
        overdraftEnabled = bill.getOverdraftEnabled();
        creationDate = bill.getCreationDate();
    }
}
