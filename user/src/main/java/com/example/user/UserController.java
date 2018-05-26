package com.example.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Created by Administrator on 2018/5/21.
 */
@RestController
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    public User findById(@PathVariable("id")Long id){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetails){
            UserDetails user = (UserDetails) principal;
            Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                LOGGER.info("当前用户：{}，角色：{}",user.getUsername(),authority.getAuthority());
            }
        }else {

        }
        return  userRepository.findOne(id);
    }

}
