package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserServices;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.catalina.User;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*" , maxAge = 3600)
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserServices userServices;
    @GetMapping
    public ResponseEntity<List<UserModel>> getAllUser(){
        return ResponseEntity.status(HttpStatus.OK).body(userServices.findAll());

    }
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getOneUser(@PathVariable(value="userId") UUID userId) {
        Optional<UserModel> userModelOptional = userServices.findById(userId);
        if (!userModelOptional.isPresent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");

        }
        else {
            return ResponseEntity.status(HttpStatus.OK).body(userModelOptional.get());
        }

    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value="userId") UUID userId){

        Optional<UserModel> userModelOptional = userServices.findById(userId);
        if (!userModelOptional.isPresent()){

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        }
        else{
            userServices.delete(userModelOptional.get());

            return ResponseEntity.status(HttpStatus.OK).body("user deleted success");
        }
    }


    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(value="userId") UUID userId ,
                         @RequestBody @Validated(UserDto.UserView.UserPut.class)
                         @JsonView(UserDto.UserView.UserPut.class) UserDto userDto){

        Optional<UserModel> userModelOptional = userServices.findById(userId);
        if (!userModelOptional.isPresent()){

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        }
        else{
            var userModel = userModelOptional.get();
             userModel.setFullName(userDto.getFullName());
             userModel.setPhoneNumber(userDto.getPhoneNumber());
             userModel.setCpf(userDto.getCpf());
             userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
             userServices.save(userModel);
            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable(value="userId") UUID userId ,
                                                 @RequestBody @Validated(UserDto.UserView.PasswordPut.class)
                                                 @JsonView(UserDto.UserView.PasswordPut.class) UserDto userDto){

        Optional<UserModel> userModelOptional = userServices.findById(userId);
        if (!userModelOptional.isPresent()){

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        }
         if (!userModelOptional.get().getPassword().equals(userDto.getOldPassword()))
         {
             return ResponseEntity.status(HttpStatus.CONFLICT).body(" Error: Mismatched old password");
         }
        else{
            var userModel = userModelOptional.get();
            userModel.setPassword(userDto.getPassword());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            userServices.save(userModel);
            return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully.");
        }
    }


    @PutMapping("/{userId}/image")
    public ResponseEntity<Object> updateImag(@PathVariable(value="userId") UUID userId ,
                                             @RequestBody @Validated(UserDto.UserView.ImagePut.class)
                                             @JsonView(UserDto.UserView.ImagePut.class) UserDto userDto){

        Optional<UserModel> userModelOptional = userServices.findById(userId);
        if (!userModelOptional.isPresent()){

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        }

        else{
            var userModel = userModelOptional.get();
           userModel.setImageUrl(userDto.getImageUrl());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            userServices.save(userModel);
            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }
}
