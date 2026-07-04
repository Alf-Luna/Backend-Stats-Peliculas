package com.mooncowpines.KinoStats.Controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mooncowpines.KinoStats.DTO.UserDetailsUpdateDTO;
import com.mooncowpines.KinoStats.DTO.UserPasswordUpdateDTO;
import com.mooncowpines.KinoStats.Exceptions.ValidationException;
import com.mooncowpines.KinoStats.Model.User;
import com.mooncowpines.KinoStats.Service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("api/v1/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        Optional<User> user = userService.getUserById(id);

        if(user.isPresent()){
            return ResponseEntity.ok()
                        .header("Header", "Values")
                        .body(user.get());
        }
        else{
            Map<String, String> errorBody = new HashMap<>();
            errorBody.put("message", "No existe un usuario con la ID: " + id);
            errorBody.put("status", "404");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@Valid @RequestBody User user) {
        try {
            userService.addUser(user);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(user.getId())
                    .toUri();
            return ResponseEntity.created(location).body(user);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/details")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @Valid @RequestBody UserDetailsUpdateDTO dto) {
        userService.updateUser(id, dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody UserPasswordUpdateDTO dto) {
        userService.updatePassword(id, dto);
        return ResponseEntity.ok().build();
    }
    
    // @DeleteMapping("/{id}")
    // public ResponseEntity<?> deleteUser(@PathVariable Long id){
    //     try{
    //         userService.deleteUserById(id);
    //         return ResponseEntity.noContent().build();
    //     } catch(Exception e){
    //         return ResponseEntity.notFound().build();
    //     }
    // }
}