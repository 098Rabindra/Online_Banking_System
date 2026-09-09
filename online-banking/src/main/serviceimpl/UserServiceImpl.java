package com.bank.serviceimpl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bank.entity.Role;
import com.bank.entity.User;
import com.bank.exception.ResourceNotFoundException;
import com.bank.repository.UserRepository;
import com.bank.service.UserService;


@Service
@SuppressWarnings("null")
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }



    @Override
    public User getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id));
    }



    @Override
    public List<User> getAllUsers() {

        return userRepository.findByRole(Role.ROLE_USER);
    }



    @Override
    public User updateUser(
            Long id,
            User updatedUser) {


        User user = getUserById(id);


        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setFatherName(updatedUser.getFatherName());
        user.setDob(updatedUser.getDob());

        user.setEmail(updatedUser.getEmail());
        user.setMobile(updatedUser.getMobile());


        user.setAddressLine1(updatedUser.getAddressLine1());
        user.setAddressLine2(updatedUser.getAddressLine2());
        user.setCountry(updatedUser.getCountry());
        user.setState(updatedUser.getState());
        user.setCity(updatedUser.getCity());
        user.setPincode(updatedUser.getPincode());


        user.setAadhaar(updatedUser.getAadhaar());
        user.setPan(updatedUser.getPan());
        user.setAccountType(updatedUser.getAccountType());


        return userRepository.save(user);
    }



    @Override
    public void deleteUser(Long id) {

        User user = getUserById(id);

        userRepository.delete(user);
    }



    @Override
    public void changePassword(
            String username,
            String oldPassword,
            String newPassword,
            String confirmPassword) {


        User user =
            userRepository.findByUsername(username)
            .orElseThrow(() ->
                new RuntimeException(
                    "User not found"));



        if(!passwordEncoder.matches(
                oldPassword,
                user.getPassword())) {


            throw new RuntimeException(
                    "Old password incorrect");
        }



        if(!newPassword.equals(confirmPassword)) {


            throw new RuntimeException(
                    "Password not match");
        }



        user.setPassword(
            passwordEncoder.encode(
                newPassword
            )
        );


        userRepository.save(user);
    }

}