package ma.nassnewsapp.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import ma.nassnewsapp.backend.controllers.EvenementController;
import ma.nassnewsapp.backend.entities.Evenement;
import ma.nassnewsapp.backend.services.EvenementService;
import ma.nassnewsapp.backend.services.VilleService;
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

    @MockBean // Creates a mock of VilleService and adds it to the application context
    private VilleService villeService;

    @Test
    void shouldFetchEventsByVilleId() throws Exception {
        // Given
        Evenement event1 = new Evenement("event1", "Event A", "...", "Agadir", LocalDate.now(), "Concert", "ville1");
        given(evenementService.getEvenementsByVille("ville1")).willReturn(List.of(event1));

        // When & Then
        mockMvc.perform(get("/api/events").param("villeId", "ville1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titre", is("Event A")));
    }

    @Test
    void shouldFetchEventByIdWhenEventExists() throws Exception {
        // Given
        Evenement event1 = new Evenement("event1", "Event A", "...", "Agadir", LocalDate.now(), "Concert", "ville1");
        given(evenementService.getEvenementById("event1")).willReturn(Optional.of(event1));

        // When & Then
        mockMvc.perform(get("/api/events/event1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEvenement", is("event1")))
                .andExpect(jsonPath("$.titre", is("Event A")));
    }

    @Test
    void shouldReturn404WhenFetchingNonExistentEvent() throws Exception {
        // Given
        given(evenementService.getEvenementById("nonexistent")).willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/events/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewEvent() throws Exception {
        // Given
        Evenement eventToCreate = new Evenement("event100", "New Event", "...", "...", LocalDate.now(), "Test", "ville10");
        given(evenementService.createEvenement(any(Evenement.class))).willReturn(eventToCreate);

        // When & Then
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titre", is("New Event")));
    }
}
