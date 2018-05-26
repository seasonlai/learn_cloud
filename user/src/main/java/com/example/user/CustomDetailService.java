package com.example.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2018/5/26.
 */
@Component
public class CustomDetailService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        switch (s){
            case "user":
                return new SecurityUser("user","password1","user-role");
            case "admin":
                return new SecurityUser("admin","password2","admin-role");
        }
        return null;
    }
}
