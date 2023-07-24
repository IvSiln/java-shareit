package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetBookings() throws Exception {
        BookingState state = BookingState.ALL;
        int from = 0;
        int size = 10;
        long userId = 1L;

        when(bookingClient.getBookings(eq(userId), eq(state), eq(from), eq(size)))
                .thenReturn(ResponseEntity.ok().body(Collections.emptyList()));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state.name())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(bookingClient, times(1)).getBookings(eq(userId), eq(state), eq(from), eq(size));
    }

    @Test
    public void testGetBookingsForOwnerItems() throws Exception {
        BookingState state = BookingState.ALL;
        int from = 0;
        int size = 10;
        long userId = 1L;

        when(bookingClient.getBookingsForOwnersItems(eq(userId), eq(state), eq(from), eq(size)))
                .thenReturn(ResponseEntity.ok().body(Collections.emptyList()));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state.name())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(bookingClient, times(1)).getBookingsForOwnersItems(eq(userId), eq(state), eq(from), eq(size));
    }

    @Test
    public void testPatch() throws Exception {
        long userId = 1L;
        long bookingId = 123L;
        boolean approved = true;

        when(bookingClient.patchBooking(eq(userId), eq(bookingId), eq(approved)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).patchBooking(eq(userId), eq(bookingId), eq(approved));
    }

    @Test
    public void testGetBooking() throws Exception {
        long userId = 1L;
        long bookingId = 123L;

        when(bookingClient.getBooking(eq(userId), eq(bookingId)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBooking(eq(userId), eq(bookingId));
    }
}
