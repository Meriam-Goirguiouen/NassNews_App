package ma.nassnewsapp.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.nassnewsapp.backend.entities.Evenement;
import ma.nassnewsapp.backend.services.EvenementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EvenementController.class) // Loads only the web layer components for this controller
class EvenementControllerTest {

    @Autowired
    private MockMvc mockMvc; // For simulating HTTP requests

    @Autowired
    private ObjectMapper objectMapper; // For converting Java objects to JSON strings

    @MockBean // Creates a mock of EvenementService and adds it to the application context
    private EvenementService evenementService;

    @Test
    void shouldFetchEventsByVilleId() throws Exception {
        // Given
        Evenement event1 = new Evenement(1, "Event A", "...", "Agadir", LocalDate.now(), "Concert", 1);
        given(evenementService.getEvenementsByVille(1)).willReturn(List.of(event1));

        // When & Then
        mockMvc.perform(get("/api/events").param("villeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titre", is("Event A")));
    }

    @Test
    void shouldFetchEventByIdWhenEventExists() throws Exception {
        // Given
        Evenement event1 = new Evenement(1, "Event A", "...", "Agadir", LocalDate.now(), "Concert", 1);
        given(evenementService.getEvenementById(1)).willReturn(Optional.of(event1));

        // When & Then
        mockMvc.perform(get("/api/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEvenement", is(1)))
                .andExpect(jsonPath("$.titre", is("Event A")));
    }

    @Test
    void shouldReturn404WhenFetchingNonExistentEvent() throws Exception {
        // Given
        given(evenementService.getEvenementById(99)).willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/events/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewEvent() throws Exception {
        // Given
        Evenement eventToCreate = new Evenement(100, "New Event", "...", "...", LocalDate.now(), "Test", 10);
        given(evenementService.createEvenement(any(Evenement.class))).willReturn(eventToCreate);

        // When & Then
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titre", is("New Event")));
    }
}
