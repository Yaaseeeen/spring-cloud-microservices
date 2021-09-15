package com.example.deposit.service;

import com.example.deposit.dto.DepositResponseDTO;
import com.example.deposit.entity.Deposit;
import com.example.deposit.exception.DepositServiceException;
import com.example.deposit.repository.DepositRepository;
import com.example.deposit.rest.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class DepositService {

    public static final String TOPIC_EXCHANGE_DEPOSIT = "js.deposit.notify.exchange";
    public static final String ROUTING_KEY_DEPOSIT = "js.key.deposit";
    private final DepositRepository depositRepository;
    private final AccountServiceClient accountServiceClient;
    private final BillServiceClient billServiceClient;
    private final RabbitTemplate rabbitTemplate;

    // todo
//    @Autowired
    public DepositService(DepositRepository depositRepository, AccountServiceClient accountServiceClient,
                          BillServiceClient billServiceClient, RabbitTemplate rabbitTemplate) {
        this.depositRepository = depositRepository;
        this.accountServiceClient = accountServiceClient;
        this.billServiceClient = billServiceClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    public DepositResponseDTO deposit(Long accountId, Long billId, BigDecimal amount) {
        if (accountId == null && billId == null) {
            throw new DepositServiceException("Account is null and bill is null");
        }
        if (billId != null) {
            BillResponseDto billResponseDto = billServiceClient.getBillById(billId);
            BillRequestDto billRequestDto = createBillRequest(amount, billResponseDto);

            billServiceClient.update(billId, billRequestDto);
            AccountResponseDto accountResponseDto = accountServiceClient.getAccountById(billRequestDto.getAccountId());
            depositRepository.save(new Deposit(amount, billId, OffsetDateTime.now(), accountResponseDto.getEmail()));

            return createResponse(amount, accountResponseDto);
        }
        BillResponseDto defaultBill = getDefaultBill(accountId);
        BillRequestDto billRequestDto = createBillRequest(amount, defaultBill);
        billServiceClient.update(defaultBill.getBillId(), billRequestDto);
        AccountResponseDto account = accountServiceClient.getAccountById(accountId);
        depositRepository.save(new Deposit(amount, defaultBill.getBillId(), OffsetDateTime.now(), account.getEmail()));
        return createResponse(amount, account);
    }

    private DepositResponseDTO createResponse(BigDecimal amount, AccountResponseDto accountResponseDto) {
        DepositResponseDTO depositResponseDTO = new DepositResponseDTO(amount, accountResponseDto.getEmail());
        ObjectMapper mapper = new ObjectMapper();
        try {
            rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_DEPOSIT, ROUTING_KEY_DEPOSIT,
                    mapper.writeValueAsString(depositResponseDTO));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new DepositServiceException("Can't send message to RabbitMQ");
        }
        return depositResponseDTO;
    }

    private BillRequestDto createBillRequest(BigDecimal amount, BillResponseDto billResponseDto) {
        BillRequestDto billRequestDto = new BillRequestDto();
        billRequestDto.setAccountId(billResponseDto.getAccountId());
        billRequestDto.setCreationDate(billResponseDto.getCreationDate());
        billRequestDto.setIsDefault(billResponseDto.getIsDefault());
        billRequestDto.setOverdraftEnabled(billResponseDto.getOverdraftEnabled());
        billRequestDto.setAmount(billResponseDto.getAmount().add(amount));
        return billRequestDto;
    }

    private BillResponseDto getDefaultBill(Long accountId) {
        return billServiceClient.getBillsByAccountId(accountId).stream()
                .filter(BillResponseDto::getIsDefault)
                .findAny()
                .orElseThrow(() -> new DepositServiceException("Unable to find default bill for account: " + accountId));
    }
}
