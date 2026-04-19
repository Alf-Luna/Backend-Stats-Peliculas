package com.mooncowpines.KinoStats.Security;
import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mooncowpines.KinoStats.Model.User;
import com.mooncowpines.KinoStats.Repository.UserRepository;
import com.mooncowpines.KinoStats.Service.UserService;

@Service
public class CustomUserDetailService implements UserDetailsService{
    
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    public CustomUserDetailService(UserService userService){
        this.userService = userService;
    }

    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException{
        
        Optional<User> user = userService.getUserByName(name);

        if(user.isEmpty()){
            throw new UsernameNotFoundException("No se encontro el usuario: " + name);
        }

        return new org.springframework.security.core.userdetails.User(
            user.get().getUsername(),
            user.get().getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}