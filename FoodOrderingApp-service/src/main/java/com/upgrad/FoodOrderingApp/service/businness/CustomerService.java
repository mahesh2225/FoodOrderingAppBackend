package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CustomerAuthDao customerAuthDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;


    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCust(CustomerEntity custEntity) throws SignUpRestrictedException {

        CustomerEntity existedCustomer = customerDao.getUserByContactNumber(custEntity.getContactNumber());
        if (existedCustomer != null) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        }

        if (!custEntity.getEmail().matches("[a-zA-Z0-9]{3,}@[a-zA-Z0-9]{2,}\\.[a-zA-Z0-9]{2,}")) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }

        if (!custEntity.getContactNumber().matches("[0-9]{10,}")) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }

        if (!isPasswordStrong(custEntity.getPassword())) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }

        String[] encryptedText = passwordCryptographyProvider.encrypt(custEntity.getPassword());
        custEntity.setSalt(encryptedText[0]);
        custEntity.setPassword(encryptedText[1]);

        try {
            return customerDao.createCustomer(custEntity);
        } catch (Exception e) {
            throw new SignUpRestrictedException("SGR-000", "Unknown database error while creating customer");
        }

    }

    private boolean isPasswordStrong(String password) {
        //min 8 char long
        //1 digit
        //1 one uppercase letter
        //1 - [#@$%&*!^]
        //total score of password
        boolean containdigit = false;
        boolean containUpperCaseChar = false;
        boolean containSpecialChar = false;

        if (password.length() < 8)
            return false;
        char c;

        for (int i = 0; i < password.length(); i++) {

            c = password.charAt(i);

            if (c >= '0' && c <= '9') {
                containdigit = true;
            } else if (c >= 'A' && c <= 'Z') {
                containUpperCaseChar = true;
            } else if (c == '#' || c == '@' || c == '$' || c == '%' || c == '&' || c == '*' || c == '!' || c == '^') {
                containSpecialChar = true;
            }

            if (containdigit & containUpperCaseChar & containSpecialChar) {
                return true;
            }
        }

        return false;
    }

    public CustomerAuthEntity authenticate(String contactNumber, String password) throws AuthenticationFailedException {

        if (contactNumber == null ||
                contactNumber.isEmpty() ||
                password == null ||
                password.isEmpty()) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }

        CustomerEntity customerEntity = customerDao.getUserByContactNumber(contactNumber);
        if (customerEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }
        String encryptedPassword = passwordCryptographyProvider.encrypt(password, customerEntity.getSalt());
        if (!encryptedPassword.equals(customerEntity.getPassword())) {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }
        CustomerAuthEntity custAuthEntity = new CustomerAuthEntity();
        custAuthEntity.setCustomer(customerEntity);
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime expiresAt = now.plusHours(8);
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
        custAuthEntity.setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));
        custAuthEntity.setLoginAt(now);
        custAuthEntity.setExpiresAt(expiresAt);
        custAuthEntity.setUuid(UUID.randomUUID().toString());
        customerAuthDao.createAuthToken(custAuthEntity);
        return custAuthEntity;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity logout(final String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity custAuthEntity = getCustomerAuth(accessToken);
        custAuthEntity.setLogoutAt(ZonedDateTime.now());
        return customerAuthDao.updateAuthToken(custAuthEntity);
    }

    public CustomerEntity getCustomer(final String accessToken) throws AuthorizationFailedException {
        return getCustomerAuth(accessToken).getCustomer();
    }


    private CustomerAuthEntity getCustomerAuth(final String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity custAuthEntity = customerAuthDao.getAuthTokenEntityByAccessToken(accessToken);
        if (custAuthEntity != null) {
            if (custAuthEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
            } else if (custAuthEntity.getExpiresAt().compareTo(ZonedDateTime.now()) <= 0) {
                throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
            }
            return custAuthEntity;
        } else {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(CustomerEntity custEntity) throws AuthorizationFailedException {
        return customerDao.updateCustomer(custEntity);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustPwd(String oldPassword, String newPassword, CustomerEntity custEntity) throws UpdateCustomerException {
        if (!isPasswordStrong(newPassword)) {
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        }
        String encryptedOldPassword = passwordCryptographyProvider.encrypt(oldPassword, custEntity.getSalt());
        if (!encryptedOldPassword.equals(custEntity.getPassword())) {
            throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
        }
        custEntity.setPassword(passwordCryptographyProvider.encrypt(newPassword, custEntity.getSalt()));
        return customerDao.updateCustomer(custEntity);
    }


}