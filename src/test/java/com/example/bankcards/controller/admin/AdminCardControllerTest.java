package com.example.bankcards.controller.admin;

import com.example.bankcards.config.SecurityConfiguration;
import com.example.bankcards.config.TestConfig;
import com.example.bankcards.config.TestSecureConfiguration;
import com.example.bankcards.controller.AbstractControllerTest;
import com.example.bankcards.dto.request.card.CreateCardRequest;
import com.example.bankcards.dto.request.card.DeleteCardRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.security.JwtRequestFilter;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardStatus;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminCardController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfiguration.class, JwtRequestFilter.class})
)
@Import({TestSecureConfiguration.class, TestConfig.class})
class AdminCardControllerTest extends AbstractControllerTest {
    @MockBean private CardService cardService;

    private CreateCardRequest createCardRequest;
    private CardResponse cardResponse;
    private DeleteCardRequest deleteCardRequest;
    private Page<CardResponse> cardPage;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        createCardRequest = new CreateCardRequest();
        createCardRequest.setUserId(10);
        createCardRequest.setValidPeriod(LocalDate.now().plusYears(3));
        createCardRequest.setBalance(new BigDecimal("1000.00"));

        cardResponse = new CardResponse(
                1, 10, "**** **** **** 4444", LocalDate.now().plusYears(3), CardStatus.ACTIVE, new BigDecimal("1000.00")
        );

        deleteCardRequest = new DeleteCardRequest();
        deleteCardRequest.setCardId(1);
        deleteCardRequest.setUserId(10);

        cardPage = new PageImpl<>(List.of(cardResponse));
    }

    @Test
    public void createCard_return201_whenValidRequest() throws Exception {
        when(cardService.create(createCardRequest)).thenReturn(cardResponse);

        mockMvc.perform(post("/bank/admin/card/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(createCardRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectWriter.writeValueAsString(cardResponse)));
    }

    @Test
    public void createCard_return400_whenInvalidRequest() throws Exception {
        CreateCardRequest invalidRequest = new CreateCardRequest();

        mockMvc.perform(post("/bank/admin/card/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteCard_return200_whenValidRequest() throws Exception {
        mockMvc.perform(delete("/bank/admin/card/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(deleteCardRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteCard_return400_whenServiceThrowsNotFound() throws Exception {
        doThrow(new NotFoundException("Card not found"))
                .when(cardService).deleteByIdAndUserId(deleteCardRequest.getCardId(), deleteCardRequest.getUserId());

        mockMvc.perform(delete("/bank/admin/card/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(deleteCardRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void viewCards_return200_withDefaultParams() throws Exception {
        when(cardService.getPage(0, 10)).thenReturn(cardPage);

        mockMvc.perform(get("/bank/admin/card/view")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(cardPage)));
    }

    @Test
    public void filterCardsByStatus_return200_whenStatusProvided() throws Exception {
        when(cardService.getPageByStatus(CardStatus.BLOCK, 0, 10))
                .thenReturn(cardPage);

        mockMvc.perform(get("/bank/admin/card/filter/status")
                        .param("status", "BLOCK")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(cardPage)));
    }

    @Test
    public void filterCardsByStatus_return200_withDefaultStatus() throws Exception {
        when(cardService.getPageByStatus(CardStatus.ACTIVE, 0, 10))
                .thenReturn(cardPage);

        mockMvc.perform(get("/bank/admin/card/filter/status"))
                .andExpect(status().isOk());
    }

    @Test
    public void filterCardsByUser_return200_whenUserIdProvided() throws Exception {
        int userId = 10;
        when(cardService.getPageByUserId(userId, 0, 10))
                .thenReturn(cardPage);

        mockMvc.perform(get("/bank/admin/card/filter/user")
                        .param("user_id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(cardPage)));
    }

    @Test
    public void filterCardsByUser_return400_whenUserIdMissing() throws Exception {
        mockMvc.perform(get("/bank/admin/card/filter/user"))
                .andExpect(status().isBadRequest());
    }
}