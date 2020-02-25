package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.common.Utility;
import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
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
public class AddressController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/address/customer",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAddressList(
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {
        CustomerEntity customerEntity = customerService.getCustomer(Utility.getTokenFromAuthorizationField(authorization));
        List<AddressEntity> listAddEntity = addressService.getAllAddress(customerEntity);
        List<AddressList> listAddress = null;
        if(listAddEntity.size() != 0 ) {
            listAddress = new ArrayList<AddressList>();
            for (AddressEntity addEntity: listAddEntity) {
                AddressListState addListState = new AddressListState()
                        .id(UUID.fromString(addEntity.getState().getUuid()))
                        .stateName(addEntity.getState().getStateName());
                listAddress.add(new AddressList()
                        .id(UUID.fromString(addEntity.getUuid()))
                        .flatBuildingName(addEntity.getFlatBuilNo())
                        .locality(addEntity.getLocality())
                        .city(addEntity.getCity())
                        .pincode(addEntity.getPincode())
                        .state(addListState));
            }
        }
        AddressListResponse addListResponse = new AddressListResponse().addresses(listAddress);
        return new ResponseEntity<AddressListResponse>(addListResponse, HttpStatus.OK);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/address",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(
            @RequestHeader("authorization") final String authorization,
            @RequestBody(required = false) final SaveAddressRequest saveAddRequest)
            throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {
        CustomerEntity custEntity = customerService.getCustomer(Utility.getTokenFromAuthorizationField(authorization));
        StateEntity stateEntity = addressService.getStateByUUID(saveAddRequest.getStateUuid());
        AddressEntity addEntity = new AddressEntity();
        //creating new random uuid and set to new Address Entity
        addEntity.setUuid(UUID.randomUUID().toString());
        addEntity.setFlatBuilNo(saveAddRequest.getFlatBuildingName());
        addEntity.setLocality(saveAddRequest.getLocality());
        addEntity.setCity(saveAddRequest.getCity());
        addEntity.setPincode(saveAddRequest.getPincode());
        addEntity.setState(stateEntity);
        //Calling AddressService to create a new AddressEntity
        AddressEntity createdAddressEntity =  addressService.saveAddress(addEntity, custEntity);
        SaveAddressResponse saveAddResponse = new SaveAddressResponse().id(createdAddressEntity.getUuid()).status("ADDRESS SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SaveAddressResponse>(saveAddResponse, HttpStatus.CREATED);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/states",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatesListResponse> getStatesList() {
        List<StateEntity> listStateEntity = addressService.getAllStates();
        List<StatesList> listStates = null;

        if( listStateEntity.size() != 0 ) {
            listStates = new ArrayList<StatesList>();
            for (StateEntity stateEntity: listStateEntity) {
                listStates.add(new StatesList().id(UUID.fromString(stateEntity.getUuid())).stateName(stateEntity.getStateName()));
            }
        }

        StatesListResponse listStatesResponse = new StatesListResponse().states(listStates);
        return new ResponseEntity<StatesListResponse>(listStatesResponse, HttpStatus.OK);
    }

    @DeleteMapping("/address/{address_id}")
    public ResponseEntity<DeleteAddressResponse> deleteAddress(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("address_id") final String addressId)
            throws AuthorizationFailedException, AddressNotFoundException {

        CustomerEntity custEntity = customerService.getCustomer(Utility.getTokenFromAuthorizationField(authorization));

        //Calling AddressService to search AddressEntity
        AddressEntity addEntity =  addressService.getAddressByUUID(addressId, custEntity);
        AddressEntity delAddressEntity = addressService.deleteAddress(addEntity);
        DeleteAddressResponse delAddressResponse = new DeleteAddressResponse().id(UUID.fromString(delAddressEntity.getUuid())).status("ADDRESS DELETED SUCCESSFULLY");
        return  new ResponseEntity<>(delAddressResponse, HttpStatus.OK);
    }
}

