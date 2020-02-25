package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RestaurantEntity> restaurantsByRating() {
        try {
            return entityManager.createNamedQuery("getRestaurantsByRating", RestaurantEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            System.out.println(".....................Database Error");
            e.printStackTrace();
            return null;
        }
    }



    public RestaurantEntity restaurantByUUID(String uuid) {
        try {
            return entityManager.createNamedQuery("getRestaurantByUUID", RestaurantEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            System.out.println(".....................Database Error");
            e.printStackTrace();
            return null;
        }
    }

    public RestaurantEntity updateRestaurant(final RestaurantEntity updateRestaurantEntity) {
        try {
            entityManager.merge(updateRestaurantEntity);
        } catch (Exception e) {
            System.out.println(".....................Database Error");
            e.printStackTrace();
        }
        return updateRestaurantEntity;
    }

    public List<RestaurantCategoryEntity> restaurantByCategory(String categoryId) {
        try {
            return entityManager.createNamedQuery("getRestaurantByCategory", RestaurantCategoryEntity.class).setParameter("categoryId", categoryId).getResultList();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            System.out.println(".....................Database Error");
            e.printStackTrace();
            return null;
        }
    }

    public List<RestaurantEntity> restaurantsByName(String restaurantName) {
        try {
            return entityManager.createNamedQuery("getRestaurantsByName", RestaurantEntity.class).setParameter("restaurantName", "%" +restaurantName + "%").getResultList();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            System.out.println(".....................Database Error");
            e.printStackTrace();
            return null;
        }
    }


}
