package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CrossOrigin(allowedHeaders="*", origins="*", exposedHeaders=("access-token"))
@RestController
@RequestMapping("/")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemService itemService;

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/category",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse> getAllCategoriesOrderedByName() {
        List<CategoryEntity> listCategoryEntity = categoryService.getAllCategoriesOrderedByName();
        List<CategoryListResponse> listCategoryResponse = null;
        if(listCategoryEntity.size() != 0) {
            listCategoryResponse = new ArrayList<>();
            for (CategoryEntity c : listCategoryEntity) {
                listCategoryResponse.add(new CategoryListResponse()
                        .id(UUID.fromString(c.getUuid()))
                        .categoryName(c.getCategoryName()));
            }
        }
        CategoriesListResponse listCategoriesResponse = new CategoriesListResponse().categories(listCategoryResponse);
        return new ResponseEntity<>(listCategoriesResponse, HttpStatus.OK);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/category/{category_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getCategoryDetails(
            @PathVariable("category_id") final String categoryId)
            throws CategoryNotFoundException {
        if( categoryId == null || categoryId.isEmpty()) {
            throw new CategoryNotFoundException("CNF-001","Category id field should not be empty");
        }
        CategoryEntity categoryEntity = categoryService.getCategoryById(categoryId);
        List<ItemList> listItem = new ArrayList<ItemList>();
        for(ItemEntity i : categoryEntity.getItems()){
            listItem.add(new ItemList()
                    .id(UUID.fromString(i.getUuid()))
                    .itemType(ItemList.ItemTypeEnum.fromValue(i.getType().toString()))
                    .itemName(i.getItemName())
                    .price(i.getPrice()));
        }
        CategoryDetailsResponse categoryResponse = new CategoryDetailsResponse()
                .id(UUID.fromString(categoryEntity.getUuid()))
                .categoryName(categoryEntity.getCategoryName())
                .itemList(listItem);

        return new ResponseEntity<CategoryDetailsResponse>(categoryResponse,HttpStatus.OK);
    }
}
