package com.brand.octocat.controller;

import com.brand.octocat.error.UsernameNotFoundException;
import com.brand.octocat.model.dto.UserDto;
import com.brand.octocat.service.GithubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GithubController.class)
class GithubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GithubService service;

    @Test
    void get_shouldReturnUserDto_whenUserExists() throws Exception {
        UserDto dto = new UserDto(
                "octocat",
                "The Octocat",
                "avatar",
                "San Francisco",
                null,
                "url",
                "Tue, 25 Jan 2011 18:44:36 GMT",
                List.of()
        );

        when(service.getUser("octocat")).thenReturn(dto);

        mockMvc.perform(get("/api/github/octocat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_name").value("octocat"))
                .andExpect(jsonPath("$.display_name").value("The Octocat"));
    }

    @Test
    void get_shouldReturn400_whenUsernameBlank() throws Exception {
        mockMvc.perform(get("/api/github/ "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Username must not be empty"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void get_shouldReturn404_whenUserNotFound() throws Exception {
        when(service.getUser("missing"))
                .thenThrow(new UsernameNotFoundException("missing"));

        mockMvc.perform(get("/api/github/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User Not Found"))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("GitHub user not found: missing"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void get_shouldReturn404_whenUsernameDoNotExist() throws Exception {
        mockMvc.perform(get("/api/github/"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found. Verify URL"))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("No static resource api/github."))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void get_shouldReturn503_whenRestClientExceptionOccurs() throws Exception {
        when(service.getUser("octocat"))
                .thenThrow(new RestClientException("GitHub timeout"));

        mockMvc.perform(get("/api/github/octocat"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("Upstream Service Error"))
                .andExpect(jsonPath("$.status").value("SERVICE_UNAVAILABLE"))
                .andExpect(jsonPath("$.message").value("GitHub timeout"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void get_shouldReturn503_whenHttpClientErrorExceptionOccurs() throws Exception {
        when(service.getUser("octocat"))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        mockMvc.perform(get("/api/github/octocat"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("Upstream Service Error"))
                .andExpect(jsonPath("$.status").value("SERVICE_UNAVAILABLE"))
                .andExpect(jsonPath("$.message").value("400 Bad Request"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void get_shouldReturn500_whenUnexpectedExceptionOccurs() throws Exception {
        when(service.getUser("octocat"))
                .thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/api/github/octocat"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.status").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("boom"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

}
