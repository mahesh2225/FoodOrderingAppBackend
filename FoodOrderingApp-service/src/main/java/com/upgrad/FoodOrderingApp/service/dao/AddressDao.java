package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AddressDao {

    @PersistenceContext
    private EntityManager entityManager;
    public List<CustomerAddressEntity> getAllCustomerAddress(CustomerEntity customer){
        try {
            return entityManager.createNamedQuery("getAllCustomerAddress", CustomerAddressEntity.class).setParameter("customer",customer).getResultList();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            System.out.println(".....................Database Error");
            e.printStackTrace();
            return null;
        }
    }

    public CustomerAddressEntity getAddressByUUID(String addressUuid, String customerUuid){
        try {
            return entityManager.createNamedQuery("getCustomerAddressByUUID", CustomerAddressEntity.class).setParameter("addressUuid",addressUuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            System.out.println(".....................Database Error");
            e.printStackTrace();
            return null;
        }
    }

     public AddressEntity deleteAddress(AddressEntity addressEntity){
        try {
            entityManager.remove(addressEntity);
        } catch (Exception e) {
            System.out.println(".....................Database Error");
            e.printStackTrace();
        }
        return addressEntity;
    }

    public AddressEntity saveAddress(CustomerAddressEntity customerAddressEntity){
        try {
            entityManager.persist(customerAddressEntity.getAddress());
            entityManager.persist(customerAddressEntity);
        } catch (Exception e) {
            System.out.println(".....................Database Error");
            e.printStackTrace();
        }
        return customerAddressEntity.getAddress();
    }

}