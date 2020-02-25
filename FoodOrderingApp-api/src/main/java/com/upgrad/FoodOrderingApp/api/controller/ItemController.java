package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.api.model.ItemListResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemList.ItemTypeEnum;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CrossOrigin(allowedHeaders="*", origins="*", exposedHeaders=("access-token"))
@RestController
@RequestMapping("/")
public class ItemController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ItemService itemService;

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/item/restaurant/{restaurant_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ItemListResponse> getItemList(
            @PathVariable("restaurant_id") final String restaurantId)
            throws RestaurantNotFoundException {

        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantId);
        List<ItemEntity> listEntity = itemService.getItemsByPopularity(restaurantEntity);
        ArrayList<ItemList> listItemList = new ArrayList<>();
        for(ItemEntity i: listEntity){
            listItemList.add(new ItemList().id(UUID.fromString(i.getUuid()))
                    .price(i.getPrice()).itemName(i.getItemName())
                    .itemType(ItemTypeEnum.fromValue(i.getType().toString())));
        }
        ItemListResponse listItemResponse = new ItemListResponse();
        listItemResponse.addAll(0, listItemList);
        return new ResponseEntity<ItemListResponse>(listItemResponse, HttpStatus.OK);
    }

}