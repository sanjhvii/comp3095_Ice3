package ca.gbc.inventoryservice;

import ca.gbc.inventoryservice.dto.InventoryRequest;
import ca.gbc.inventoryservice.dto.InventoryResponse;
import ca.gbc.inventoryservice.model.Inventory;
import ca.gbc.inventoryservice.repository.InventoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class InventoryServiceApplicationTests extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InventoryRepository inventoryRepository;

    private void prepareInventoryData() {
        Inventory inventory1 = new Inventory(1L, "SKU123", 10);
        Inventory inventory2 = new Inventory(2L, "SKU456", 0); // Out of stock
        inventoryRepository.saveAll(List.of(inventory1, inventory2));
    }

    @Test
    void isInStockTest() throws Exception {
        prepareInventoryData();

        List<InventoryRequest> requests = List.of(
                new InventoryRequest("SKU123", 5), // In stock
                new InventoryRequest("SKU456", 1)  // Out of stock
        );

        String jsonRequest = objectMapper.writeValueAsString(requests);

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<InventoryResponse> responses = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, InventoryResponse.class));

                    assertEquals(2, responses.size());
                    assertTrue(responses.get(0).isSufficientStock());
                    assertFalse(responses.get(1).isSufficientStock());
                });
    }
}
