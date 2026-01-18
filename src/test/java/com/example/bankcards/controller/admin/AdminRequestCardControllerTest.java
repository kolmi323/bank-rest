package com.example.bankcards.controller.admin;

import com.example.bankcards.config.SecurityConfiguration;
import com.example.bankcards.config.TestConfig;
import com.example.bankcards.config.TestSecureConfiguration;
import com.example.bankcards.controller.AbstractControllerTest;
import com.example.bankcards.dto.request.req.ChangeCardReqRequest;
import com.example.bankcards.dto.response.request.CardRequestResponse;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.security.JwtRequestFilter;
import com.example.bankcards.service.CardRequestService;
import com.example.bankcards.util.RequestStatus;
import com.example.bankcards.util.RequestType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminRequestCardController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfiguration.class, JwtRequestFilter.class})
)
@Import({TestSecureConfiguration.class, TestConfig.class})
class AdminRequestCardControllerTest extends AbstractControllerTest {
    @MockBean private CardRequestService cardRequestService;

    private ChangeCardReqRequest changeRequest;
    private CardRequestResponse responseAccepted;
    private CardRequestResponse responseRejected;
    private Page<CardRequestResponse> pageResponse;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        changeRequest = new ChangeCardReqRequest();
        changeRequest.setRequestId(100);

        responseAccepted = new CardRequestResponse(
                100, "**** 1234", RequestType.BLOCK, RequestStatus.ACCEPTED, LocalDateTime.now()
        );

        responseRejected = new CardRequestResponse(
                100, "**** 1234", RequestType.BLOCK, RequestStatus.REJECTED, LocalDateTime.now()
        );

        pageResponse = new PageImpl<>(List.of(responseAccepted));
    }

    @Test
    public void acceptRequest_return200_whenValid() throws Exception {
        when(cardRequestService.acceptRequest(changeRequest.getRequestId()))
                .thenReturn(responseAccepted);

        mockMvc.perform(post("/bank/admin/request/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(changeRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(responseAccepted)));
    }

    @Test
    public void acceptRequest_return400_whenServiceThrowsException() throws Exception {
        doThrow(new BadRequestException("Request already processed"))
                .when(cardRequestService).acceptRequest(changeRequest.getRequestId());

        mockMvc.perform(post("/bank/admin/request/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(changeRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void rejectRequest_return200_whenValid() throws Exception {
        when(cardRequestService.rejectRequest(changeRequest.getRequestId()))
                .thenReturn(responseRejected);

        mockMvc.perform(post("/bank/admin/request/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(changeRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(responseRejected)));
    }

    @Test
    public void viewRequests_return200_withDefaultParams() throws Exception {
        when(cardRequestService.getPage(0, 10)).thenReturn(pageResponse);

        mockMvc.perform(get("/bank/admin/request/view")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(pageResponse)));
    }

    @Test
    public void filterRequestsByStatus_return200_withDefaultStatus() throws Exception {
        when(cardRequestService.getPageByRequestStatus(RequestStatus.WAITING, 0, 10))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/bank/admin/request/filter/status"))
                .andExpect(status().isOk());
    }

    @Test
    public void filterRequestsByStatus_return200_withCustomStatus() throws Exception {
        when(cardRequestService.getPageByRequestStatus(RequestStatus.ACCEPTED, 0, 10))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/bank/admin/request/filter/status")
                        .param("status", "ACCEPTED"))
                .andExpect(status().isOk());
    }

    @Test
    public void filterRequestsByCardId_return200_withDefaultId() throws Exception {
        when(cardRequestService.getPageByCardId(1, 0, 10))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/bank/admin/request/filter/card"))
                .andExpect(status().isOk());
    }

    @Test
    public void filterRequestsByCardId_return200_withCustomId() throws Exception {
        int cardId = 1454414234;
        when(cardRequestService.getPageByCardId(cardId, 0, 10))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/bank/admin/request/filter/card")
                        .param("card_id", String.valueOf(cardId)))
                .andExpect(status().isOk());
    }

    @Test
    public void filterRequestsByCardIdAndStatus_return200_whenValid() throws Exception {
        int cardId = 123;
        when(cardRequestService.getPageByCardIdAndRequestStatus(cardId, RequestStatus.WAITING, 0, 10))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/bank/admin/request/filter/main")
                        .param("card_id", String.valueOf(cardId)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(pageResponse)));
    }

    @Test
    public void filterRequestsByCardIdAndStatus_return200_withAllParams() throws Exception {
        int cardId = 21;
        RequestStatus status = RequestStatus.REJECTED;

        when(cardRequestService.getPageByCardIdAndRequestStatus(cardId, status, 0, 10))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/bank/admin/request/filter/main")
                        .param("card_id", String.valueOf(cardId))
                        .param("status", status.name()))
                .andExpect(status().isOk());
    }

    @Test
    public void filterRequestsByCardIdAndStatus_return400_whenCardIdMissing() throws Exception {
        mockMvc.perform(get("/bank/admin/request/filter/main"))
                .andExpect(status().isBadRequest());
    }
}