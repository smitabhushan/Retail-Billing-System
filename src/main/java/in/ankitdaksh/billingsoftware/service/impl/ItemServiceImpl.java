//package in.ankitdaksh.billingsoftware.service.impl;
//
//import in.ankitdaksh.billingsoftware.entity.CategoryEntity;
//import in.ankitdaksh.billingsoftware.entity.ItemEntity;
//import in.ankitdaksh.billingsoftware.io.ItemRequest;
//import in.ankitdaksh.billingsoftware.io.ItemResponse;
//import in.ankitdaksh.billingsoftware.repository.CategoryRepository;
//import in.ankitdaksh.billingsoftware.repository.ItemRepository;
//import in.ankitdaksh.billingsoftware.service.FileUploadService;
//import in.ankitdaksh.billingsoftware.service.ItemService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class ItemServiceImpl implements ItemService {
//    private final FileUploadService fileUploadService;
//    private final CategoryRepository categoryRepository;
//    private final ItemRepository itemRepository;
//    @Override
//    public ItemResponse add(ItemRequest request, MultipartFile file) {
//        String imgUrl=fileUploadService.uploadFile(file);
//        ItemEntity newItem=convertToEntity(request);
//        CategoryEntity existingCategory=categoryRepository.findByCategoryId(request.getCategoryId())
//                .orElseThrow(() ->new RuntimeException("Category not found: "+request.getCategoryId()));
//        newItem.setCategory(existingCategory);
//        newItem.setItemId(i);
//        newItem=itemRepository.save(newItem);
//        return convertToResponse(newItem);
//    }
//    //Convert Request → Entity
//    private ItemEntity convertToEntity(ItemRequest request) {
//        return ItemEntity.builder()
//                .itemId(UUID.randomUUID().toString())
//                .name(request.getName())
//                .description(request.getDescription())
//                .price(request.getPrice())
//                .build();
//    }
//
//
//    // Convert Entity → Response
//    private ItemResponse convertToResponse(ItemEntity newItem) {
//        return ItemResponse.builder()
//                .itemId(newItem.getItemId())
//                .name(newItem.getName())
//                .description(newItem.getDescription())
//                .price(newItem.getPrice())
//                .imgUrl(newItem.getImgUrl())
//                .categoryName(newItem.getCategory().getName())
//                .categoryId(newItem.getCategory().getCategoryId())
//                .createdAt(newItem.getCreatedAt())
//                .updatedAt(newItem.getUpdatedAt())
//                .build();
//    }
//
//   //Fetch all items
//    public List<ItemResponse> fetchItems() {
//        return itemRepository.findAll()
//                .stream()
//                .map(itemEntity -> convertToResponse(itemEntity))
//                .collect(Collectors.toList());
//    }
//
//     //Delete items by id
//    public void deleteItems(String itemId) {
//        ItemEntity existingItem = itemRepository.findByItemId(itemId)
//                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
//        boolean isFileDeleted = fileUploadService.deleteFile(existingItem.getImgUrl());
//        if (isFileDeleted) {
//            itemRepository.delete(existingItem);
//        }
//        else {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete item image file");
//        }
//    }
//
//}
package in.ankitdaksh.billingsoftware.service.impl;

import in.ankitdaksh.billingsoftware.entity.CategoryEntity;
import in.ankitdaksh.billingsoftware.entity.ItemEntity;
import in.ankitdaksh.billingsoftware.io.ItemRequest;
import in.ankitdaksh.billingsoftware.io.ItemResponse;
import in.ankitdaksh.billingsoftware.repository.CategoryRepository;
import in.ankitdaksh.billingsoftware.repository.ItemRepository;
import in.ankitdaksh.billingsoftware.service.FileUploadService;
import in.ankitdaksh.billingsoftware.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final FileUploadService fileUploadService;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemResponse add(ItemRequest request, MultipartFile file) {
        try {
            // Generate unique file name
            String fileName = UUID.randomUUID().toString() + "." +
                    StringUtils.getFilenameExtension(file.getOriginalFilename());

            // Create uploads directory if not exists
            Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // Copy file to target location
            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Build image URL (assuming static resource mapping for /uploads/**)
            String imgUrl = "http://localhost:8080/uploads/" + fileName;

            // Convert request to entity
            ItemEntity newItem = convertToEntity(request);

            // Validate category
            CategoryEntity existingCategory = categoryRepository.findByCategoryId(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryId()));

            newItem.setCategory(existingCategory);
            newItem.setImgUrl(imgUrl);

            // Save entity
            newItem = itemRepository.save(newItem);

            return convertToResponse(newItem);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error occurred while saving file", e);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error occurred while saving item: " + e.getMessage(), e);
        }
    }
//FOR AWS
//    @Override
//    public ItemResponse add(ItemRequest request, MultipartFile file) {
//        // Upload file → get image URL
//        String imgUrl = fileUploadService.uploadFile(file);
//
//        // Convert request → entity
//        ItemEntity newItem = convertToEntity(request);
//
//        // Validate category
//        CategoryEntity existingCategory = categoryRepository.findByCategoryId(request.getCategoryId())
//                .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryId()));
//
//        // Set category + image URL
//        newItem.setCategory(existingCategory);
//        newItem.setImgUrl(imgUrl);
//
//        // Save entity
//        newItem = itemRepository.save(newItem);
//
//        // Convert entity → response
//        return convertToResponse(newItem);
//    }

    // Convert Request → Entity
    private ItemEntity convertToEntity(ItemRequest request) {
        return ItemEntity.builder()
                .itemId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
    }

    // Convert Entity → Response
    private ItemResponse convertToResponse(ItemEntity newItem) {
        return ItemResponse.builder()
                .itemId(newItem.getItemId())
                .name(newItem.getName())
                .description(newItem.getDescription())
                .price(newItem.getPrice())
                .imgUrl(newItem.getImgUrl())
                .categoryName(newItem.getCategory().getName())
                .categoryId(newItem.getCategory().getCategoryId())
                .createdAt(newItem.getCreatedAt())
                .updatedAt(newItem.getUpdatedAt())
                .build();
    }

    // Fetch all items
    public List<ItemResponse> fetchItems() {
        return itemRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Delete items by id WITH AWS
//    public void deleteItems(String itemId) {
//        ItemEntity existingItem = itemRepository.findByItemId(itemId)
//                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
//
//        boolean isFileDeleted = fileUploadService.deleteFile(existingItem.getImgUrl());
//        if (isFileDeleted) {
//            itemRepository.delete(existingItem);
//        } else {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete item image file");
//        }
//    }

    public void deleteItems(String itemId) {
        ItemEntity existingItem = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

        String imgUrl=existingItem.getImgUrl();
        String fileName=imgUrl.substring(imgUrl.lastIndexOf("/")+1);
        Path uploadPath=Paths.get("uploads").toAbsolutePath().normalize();
        Path filePath =uploadPath.resolve(fileName);

        try{
            Files.deleteIfExists(filePath);
        }catch (IOException e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete item image file");
        }
    }
}