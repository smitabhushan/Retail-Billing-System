package in.ankitdaksh.billingsoftware.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.ankitdaksh.billingsoftware.io.ItemRequest;
import in.ankitdaksh.billingsoftware.io.ItemResponse;
import in.ankitdaksh.billingsoftware.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/admin/items")
    public ItemResponse addItem(@RequestPart("item") String itemString, @RequestPart("file") MultipartFile file){
        ObjectMapper objectMapper=new ObjectMapper();
        ItemRequest itemRequest=null;
        try {
            // Convert JSON string → ItemRequest object
            itemRequest = objectMapper.readValue(itemString, ItemRequest.class);
            return itemService.add(itemRequest, file);
        }
        catch (JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error occurred while processing the JSON", ex);
        }

    }

    // Fetch all items
    @GetMapping("/items")
    public List<ItemResponse> readItems() {
        return itemService.fetchItems();
    }

    // Delete item by ID
    @DeleteMapping("/admin/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable String itemId) {
        try {
            itemService.deleteItems(itemId);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Item not found: " + itemId,e);
        }
    }


}
