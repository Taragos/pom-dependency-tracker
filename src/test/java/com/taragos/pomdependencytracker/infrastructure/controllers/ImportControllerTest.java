package com.taragos.pomdependencytracker.infrastructure.controllers;

import com.taragos.pomdependencytracker.infrastructure.repositories.ArtifactRepository;
import com.taragos.pomdependencytracker.infrastructure.services.ImportService;
import com.taragos.pomdependencytracker.infrastructure.services.ParserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ImportController.class)
@TestPropertySource(locations = "/test.properties")
class ImportControllerTest {

    private static MockMvc mockMvc;
    @Value("${spring.auth.user.username}")
    String userUsername;
    @Value("${spring.auth.user.password}")
    String userPassword;
    @MockBean
    private ArtifactRepository artifactRepository;
    @MockBean
    private ParserService parserService;
    @MockBean
    private ImportService importService;


    @Test
    void createArtifact() {
    }
}