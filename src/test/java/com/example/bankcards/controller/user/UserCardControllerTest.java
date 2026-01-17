package com.example.bankcards.controller.user;

import com.example.bankcards.config.SecurityConfiguration;
import com.example.bankcards.config.TestConfig;
import com.example.bankcards.config.TestSecureConfiguration;
import com.example.bankcards.controller.AbstractControllerTest;
import com.example.bankcards.dto.request.card.CreateTransactionRequest;
import com.example.bankcards.dto.request.req.CreateCardReqRequest;
import com.example.bankcards.dto.response.BalanceCardResponse;
import com.example.bankcards.dto.response.CardRequestResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.TransactionResponse;
import com.example.bankcards.security.JwtRequestFilter;
import com.example.bankcards.service.CardRequestService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.RequestStatus;
import com.example.bankcards.util.RequestType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserCardController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfiguration.class, JwtRequestFilter.class})
)
@Import({TestSecureConfiguration.class, TestConfig.class})
class UserCardControllerTest extends AbstractControllerTest {
    @MockBean private CardService cardService;
    @MockBean private CardRequestService cardRequestService;
    @MockBean private TransactionService transactionService;

    @Test
    public void createCardRequest_return201_whenValid() throws Exception {
        CreateCardReqRequest request = new CreateCardReqRequest();
        request.setCardId(10);
        request.setRequestType(RequestType.BLOCK);

        CardRequestResponse response = new CardRequestResponse(
                1, "**** **** **** 4444", RequestType.BLOCK, RequestStatus.WAITING, LocalDateTime.now()
        );

        when(cardRequestService.createRequestForChangeStatusCard(request, USER_ID))
                .thenReturn(response);

        mockMvc.perform(post("/bank/user/card/status")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void createCardRequest_return400_whenInvalidBody() throws Exception {
        CreateCardReqRequest request = new CreateCardReqRequest();

        mockMvc.perform(post("/bank/user/card/status")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createTransaction_return201_whenValid() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setFromCardId(1);
        request.setToCardId(2);
        request.setAmount(new BigDecimal("100.50"));

        TransactionResponse response = new TransactionResponse(
                1, 1, 20, new BigDecimal("100.50"), LocalDateTime.now()
        );

        when(transactionService.create(request, USER_ID))
                .thenReturn(response);

        mockMvc.perform(post("/bank/user/card/transaction")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectWriter.writeValueAsString(response)));
    }

    @Test
    public void getCards_return200_whenCalled() throws Exception {
        CardResponse cardResp = new CardResponse(1, USER_ID, "**** **** **** 4444", LocalDate.now(), CardStatus.ACTIVE, BigDecimal.TEN);
        Page<CardResponse> pageResponse = new PageImpl<>(List.of(cardResp));

        when(cardService.getPageByUserId(USER_ID, 0, 10))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/bank/user/card/view")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(pageResponse)));
    }

    @Test
    public void getCards_return200_withCustomParams() throws Exception {
        Page<CardResponse> emptyPage = new PageImpl<>(Collections.emptyList());

        when(cardService.getPageByUserId(USER_ID, 0, 10))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/bank/user/card/view")
                        .with(authentication(authentication))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    public void getCardsByStatus_return200_withDefaultStatus() throws Exception {
        Page<CardResponse> emptyPage = new PageImpl<>(Collections.emptyList());

        when(cardService.getPageByUserIdAndStatus(USER_ID, CardStatus.ACTIVE, 0, 10))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/bank/user/card/filter")
                        .with(authentication(authentication)))
                .andExpect(status().isOk());
    }

    @Test
    public void getBalanceCard_return200_whenExists() throws Exception {
        int cardId = 1;
        BalanceCardResponse response = new BalanceCardResponse(new BigDecimal("5000.00"));

        when(cardService.getBalanceByIdAndUserId(cardId, USER_ID))
                .thenReturn(response);

        mockMvc.perform(get("/bank/user/card/balance")
                        .with(authentication(authentication))
                        .param("id", String.valueOf(cardId)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(response)));
    }

    @Test
    public void getBalanceCard_return400_whenIdMissing() throws Exception {
        mockMvc.perform(get("/bank/user/card/balance")
                        .with(authentication(authentication)))
                .andExpect(status().isBadRequest());
    }
}