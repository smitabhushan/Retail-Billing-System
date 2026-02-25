package in.ankitdaksh.billingsoftware.service.impl;


import in.ankitdaksh.billingsoftware.entity.CategoryEntity;
import in.ankitdaksh.billingsoftware.io.CategoryRequest;
import in.ankitdaksh.billingsoftware.io.CategoryResponse;
import in.ankitdaksh.billingsoftware.repository.CategoryRepository;
import in.ankitdaksh.billingsoftware.repository.ItemRepository;
import in.ankitdaksh.billingsoftware.service.CategoryService;
import in.ankitdaksh.billingsoftware.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final FileUploadService fileUploadService;
    private final ItemRepository itemRepository;

//    @Override
//    public CategoryResponse add(CategoryRequest request, MultipartFile file) {
//        String imgUrl=fileUploadService.uploadFile(file); iye aws se upload krenege tab use hogi
//        CategoryEntity newCategory =convertToEntity(request);
//        newCategory.setImgUrl(imgUrl);
//        newCategory=categoryRepository.save(newCategory);
//        return convertToResponse(newCategory);
//
//    }

    @Override
    public CategoryResponse add(CategoryRequest request, MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + "." +StringUtils.getFilenameExtension(file.getOriginalFilename());
            Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            String imgUrl = "http://localhost:8080/uploads/" + fileName;

            CategoryEntity newCategory = convertToEntity(request);
            newCategory.setImgUrl(imgUrl);
            newCategory = categoryRepository.save(newCategory);
            return convertToResponse(newCategory);

        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage(), e);
        }
    }

    //it gives a list of all response like how many item you add in a cart
    @Override
    public List<CategoryResponse> read() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryEntity -> convertToResponse(categoryEntity))
                .collect(Collectors.toList());
    }

//    @Override
//    public void delete(String categoryId) {
//        CategoryEntity existingCategory = categoryRepository.findByCategoryId(categoryId)
//                 .orElseThrow(() -> new RuntimeException("Category not found: "+categoryId));
//        fileUploadService.deleteFile(existingCategory.getImgUrl());
//        categoryRepository.delete(existingCategory);
//    }

    @Override
    public void delete(String categoryId) {
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found: "+categoryId));
        String imgUrl=existingCategory.getImgUrl();
        String fileName=imgUrl.substring(imgUrl.lastIndexOf("/")+1);
        Path uploadPath=Paths.get("uploads").toAbsolutePath().normalize();
        Path filePath =uploadPath.resolve(fileName);

        try{
            Files.deleteIfExists(filePath);
        }catch (IOException e){
            e.printStackTrace();
        }
        categoryRepository.delete(existingCategory);
    }

    private CategoryResponse convertToResponse(CategoryEntity newCategory) {
       Integer itemsCount= itemRepository.countByCategoryId(newCategory.getId());

        return CategoryResponse.builder()
                .categoryId(newCategory.getCategoryId())
                .name(newCategory.getName())
                .description(newCategory.getDescription())
                .bgColor(newCategory.getBgColor())
                .imgUrl(newCategory.getImgUrl())
                .createdAt(newCategory.getCreatedAt())
                .updatedAt(newCategory.getUpdatedAt())
                .items(itemsCount)
                .build();

    }

    private CategoryEntity convertToEntity(CategoryRequest request) {
        return CategoryEntity.builder()
                .categoryId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .bgColor(request.getBgColor())
                .build();
    }
}
