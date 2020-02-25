package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.common.Utility;
import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.PasswordCryptographyProvider;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@CrossOrigin(allowedHeaders="*", origins="*", exposedHeaders=("access-token"))
@RestController
@RequestMapping("/")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/customer/login",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(
            @RequestHeader("authorization") final String authorization)
            throws AuthenticationFailedException, AuthorizationFailedException {
        //spliting and extracting authorization base 64 code string from "authorization" field
        String[] base64EncodedString = authorization.split("Basic ");
        //decode base64 string from a "authorization" field
        if(base64EncodedString.length != 2 ) {
            throw new AuthenticationFailedException("ATH-003","Incorrect format of decoded customer name and password");
        }
        byte[] decodedArray = passwordCryptographyProvider.getBase64DecodedStringAsBytes(base64EncodedString[1]);
        String decodedString = new String(decodedArray);
        //decoded string contain username and password separated by ":"
        String[] decodedUserNamePassword = decodedString.split(":");
        if ( decodedUserNamePassword.length != 2 ) {
            throw new AuthenticationFailedException("ATH-003","Incorrect format of decoded customer name and password");
        }
        //getting CustomerEntity from Auth Token
        CustomerAuthEntity custAuthEntity = customerService.authenticate(decodedUserNamePassword[0],decodedUserNamePassword[1]);
        //sending response with customer uuid and access token in HttpHeader
        LoginResponse loginResponse = new LoginResponse()
                                            .id(custAuthEntity.getCustomer().getUuid())
                                            .firstName(custAuthEntity.getCustomer().getFirstName())
                                            .lastName(custAuthEntity.getCustomer().getLastName())
                                            .contactNumber(custAuthEntity.getCustomer().getContactNumber())
                                            .emailAddress(custAuthEntity.getCustomer().getEmail())
                                            .message("SIGNED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", custAuthEntity.getAccessToken());
        return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/customer/signup",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> customerSignup(
            @RequestBody final SignupCustomerRequest signupCustRequest)
            throws SignUpRestrictedException {
        if (signupCustRequest.getFirstName() == null ||
                signupCustRequest.getFirstName().isEmpty() ||
                signupCustRequest.getEmailAddress() == null ||
                signupCustRequest.getEmailAddress().isEmpty() ||
                signupCustRequest.getContactNumber() == null ||
                signupCustRequest.getContactNumber().isEmpty() ||
                signupCustRequest.getPassword() == null ||
                signupCustRequest.getPassword().isEmpty()) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled.");
        }

        //creating a new CustomerEntity Object
        CustomerEntity customerEntity = new CustomerEntity();
        //creating a new random unique uuid and set it to new Customer Entity
        customerEntity.setUuid(UUID.randomUUID().toString());
        customerEntity.setFirstName(signupCustRequest.getFirstName());
        customerEntity.setLastName(signupCustRequest.getLastName());
        customerEntity.setEmail(signupCustRequest.getEmailAddress());
        customerEntity.setContactNumber(signupCustRequest.getContactNumber());
        customerEntity.setPassword(signupCustRequest.getPassword());
        //Calling CustomerService to create a new customer Entity
        CustomerEntity createdCustmerEntity = customerService.saveCust(customerEntity);
        //creating response with customer uuid
        SignupCustomerResponse signupCustResponse = new SignupCustomerResponse().id(createdCustmerEntity.getUuid()).status("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupCustomerResponse>(signupCustResponse, HttpStatus.CREATED);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/customer/logout",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {
        CustomerAuthEntity custAuthEntity = customerService.logout(Utility.getTokenFromAuthorizationField(authorization));
        //creating response with logoutResponse customer uuid
        LogoutResponse logoutResponse = new LogoutResponse().id(custAuthEntity.getCustomer().getUuid()).message("SIGNED OUT SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<LogoutResponse>(logoutResponse, headers, HttpStatus.OK);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/customer/password",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> updatePassword(
            @RequestHeader("authorization") final String authorization,
            @RequestBody final UpdatePasswordRequest updatePwdRequest)
            throws UpdateCustomerException, AuthorizationFailedException {
        if(updatePwdRequest.getOldPassword() == null ||
                updatePwdRequest.getOldPassword().isEmpty() ||
                updatePwdRequest.getNewPassword() == null ||
                updatePwdRequest.getNewPassword().isEmpty()) {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }
        // Calling authentication Service with access token.
        CustomerEntity customerEntity = customerService.getCustomer(Utility.getTokenFromAuthorizationField(authorization));
        customerService.updateCustPwd(updatePwdRequest.getOldPassword(),updatePwdRequest.getNewPassword(), customerEntity);
        //creating response with customer uuid
        UpdatePasswordResponse updatePwdResponse = new UpdatePasswordResponse().id(customerEntity.getUuid()).status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");
        return new ResponseEntity<UpdatePasswordResponse>(updatePwdResponse, HttpStatus.OK);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/customer",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateCustomer(
            @RequestHeader("authorization") final String authorization,
            @RequestBody final UpdateCustomerRequest updateCustRequest)
            throws UpdateCustomerException, AuthorizationFailedException {
        if( updateCustRequest.getFirstName() == null ||
                updateCustRequest.getFirstName().isEmpty()){
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }
        // Calling authenticationService with access token came in authorization field.
        CustomerEntity customerEntity = customerService.getCustomer(Utility.getTokenFromAuthorizationField(authorization));
        customerEntity.setFirstName(updateCustRequest.getFirstName());
        if( updateCustRequest.getLastName() != null &&
                !updateCustRequest.getLastName().isEmpty()) {
            customerEntity.setLastName(updateCustRequest.getLastName());
        }
        CustomerEntity updateCustomerEntity = customerService.updateCustomer(customerEntity);
        //creating response with customer uuid
        UpdateCustomerResponse updateCustResponse = new UpdateCustomerResponse()
                .id(updateCustomerEntity.getUuid())
                .firstName(updateCustomerEntity.getFirstName())
                .lastName(updateCustomerEntity.getLastName())
                .status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");
        return new ResponseEntity<UpdateCustomerResponse>(updateCustResponse, HttpStatus.OK);
    }


}
