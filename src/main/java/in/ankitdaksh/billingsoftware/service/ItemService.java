package in.ankitdaksh.billingsoftware.service;

import in.ankitdaksh.billingsoftware.io.ItemRequest;
import in.ankitdaksh.billingsoftware.io.ItemResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {
    ItemResponse add(ItemRequest request, MultipartFile file);
    List<ItemResponse> fetchItems();
    void deleteItems(String itemId);
}
