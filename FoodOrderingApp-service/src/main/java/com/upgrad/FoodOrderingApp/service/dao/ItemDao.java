package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<ItemEntity> getItemsByCategoryAndRestaurant(String RestaurantUuid, String CategoryUuid) {
        try {
            return entityManager.createNamedQuery("getItemsByCategoryAndRestaurant", ItemEntity.class).setParameter("categoryId",CategoryUuid).setParameter("restaurantId", RestaurantUuid).getResultList();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            System.out.println(".....................Database Error");
            e.printStackTrace();
            return null;
        }
    }


    public ItemEntity getItemsByUuid(String uuid) {
        try {
            return entityManager.createNamedQuery("getItemsByUuid", ItemEntity.class).setParameter("uuid",uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            System.out.println(".....................Database Error");
            e.printStackTrace();
            return null;
        }
    }

    public List<ItemEntity> getItemsByPopularity(Long restaurantId) {
        try {
            List<ItemEntity> listItemEntity = entityManager.createNamedQuery("getItemsByPopularity", ItemEntity.class).setParameter("restaurantId",restaurantId).getResultList();
            List<ItemEntity>  subListItemEntity = new ArrayList<>();
            int listSize = listItemEntity.size();
            if(listSize > 0) {
                subListItemEntity.addAll(listItemEntity.subList(0, Math.min(listSize, 5)));
            }
            return subListItemEntity;
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            System.out.println(".....................Database Error");
            e.printStackTrace();
            return null;
        }
    }
}


