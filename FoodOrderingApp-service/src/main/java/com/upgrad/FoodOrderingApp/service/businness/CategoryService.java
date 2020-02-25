package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    public List<CategoryEntity> getCategoriesByRestaurant(String restaurantId) {
        List<RestaurantCategoryEntity> listRestaurantCategoryEntity = categoryDao.getCategoriesByRestaurant(restaurantId);
        List<CategoryEntity> listCategoryEntity = new ArrayList<>();
        for(RestaurantCategoryEntity rc: listRestaurantCategoryEntity ) {
            listCategoryEntity.add(rc.getCategory());
        }
        return listCategoryEntity;
    }

    public CategoryEntity getCategoryById(String categoryId) throws CategoryNotFoundException {

        if(categoryId == null || categoryId.isEmpty()) {
            throw new CategoryNotFoundException("CNF-001","Category id field should not be empty");
        }

        CategoryEntity categoryEntity = categoryDao.getCategoryById(categoryId);
        if(categoryEntity == null) {
            throw new CategoryNotFoundException("CNF-002","No category by this id");
        }

        List<CategoryItemEntity> listCategoryItemEntity = categoryDao.getCategoryItemEntityByCategoryUuid(categoryId);

        List<ItemEntity> listItemEntity = new ArrayList<>();
        for(CategoryItemEntity ci :listCategoryItemEntity) {
            listItemEntity.add(ci.getItem());
        }
        categoryEntity.setItems(listItemEntity);
        return categoryEntity;
    }

    public List<CategoryEntity> getAllCategoriesOrderedByName() {
        return categoryDao.getAllCategoriesOrderedByName();
    }
}
