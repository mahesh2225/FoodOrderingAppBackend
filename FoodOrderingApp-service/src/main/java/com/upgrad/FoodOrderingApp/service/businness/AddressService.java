package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AddressService {

    @Autowired
    private StateDao stateDao;

    @Autowired
    private AddressDao addressDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(AddressEntity addEntity) {
        return addressDao.deleteAddress(addEntity);
    }

    public AddressEntity getAddressByUUID(String uuid, CustomerEntity custEntity) throws AuthorizationFailedException, AddressNotFoundException{
        CustomerAddressEntity customerAddEntity = addressDao.getAddressByUUID(uuid, custEntity.getUuid());
        if(customerAddEntity == null ) {
            throw new AddressNotFoundException("ANF-003","No address by this id");
        } else if ( customerAddEntity.getCustomer() != custEntity ) {
            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        } else {
            return customerAddEntity.getAddress();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(AddressEntity addEntity, CustomerEntity custEntity) throws SaveAddressException {

        if ( addEntity.getPincode() == null || !(addEntity.getPincode().matches("^\\d{6,6}$"))) {
            throw new SaveAddressException("SAR-002", "Invalid pincode.");
        }

        if ( addEntity.getFlatBuilNo() == null ||
                addEntity.getFlatBuilNo().isEmpty() ||
                addEntity.getLocality() == null ||
                addEntity.getLocality().isEmpty() ||
                addEntity.getCity() == null ||
                addEntity.getCity().isEmpty() ) {
            throw new SaveAddressException("SAR-001", "No field can be empty.");
        }

        CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setAddress(addEntity);
        customerAddressEntity.setCustomer(custEntity);

        try {
            return addressDao.saveAddress(customerAddressEntity);
        } catch (Exception e) {
            throw new SaveAddressException("ADR-000", "Unknown database error while saving Address");
        }
    }



    public List<AddressEntity> getAllAddress(CustomerEntity custEntity) {

        List<CustomerAddressEntity> listCustAddEntity = addressDao.getAllCustomerAddress(custEntity);
        List<AddressEntity> listAddEntity = new ArrayList<>();
        for (CustomerAddressEntity ca: listCustAddEntity) {
            listAddEntity.add(ca.getAddress());
        }

        return listAddEntity;
    }

    public List<StateEntity> getAllStates() {
        return stateDao.getAllState();
    }

    public StateEntity getStateByUUID(String uuid) throws AddressNotFoundException {
        try {
            return stateDao.getStateByUuid(uuid);
        } catch (Exception e) {
            throw new AddressNotFoundException("ANF-002", "No state by this id.");
        }
    }
}
