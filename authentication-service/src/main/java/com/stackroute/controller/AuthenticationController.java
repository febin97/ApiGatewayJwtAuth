package com.stackroute.controller;

import com.stackroute.config.JwtTokenUtil;
import com.stackroute.model.JwtResponse;
import com.stackroute.model.UserDTO;
import com.stackroute.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*",allowedHeaders = "*")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UserDTO user) throws Exception {
        try {
            user.setLoginType("general");
            jwtUserDetailsService.save(user);
            return ResponseEntity.ok(new JwtResponse("User Saved"));
        }catch (Exception e){
            return ResponseEntity.ok(new JwtResponse("UserEmail already Exists"));
        }

    }
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
        try{
            if(user.getLoginType().equalsIgnoreCase("google") || user.getLoginType().equalsIgnoreCase("facebook")){
                try {
                    jwtUserDetailsService.save(user);
                }catch (Exception e){
                    System.out.println("No need to save");
                }
            }
            final UserDetails userDetails = jwtUserDetailsService
                    .loadUserByUsername(user.getUserEmail());
            System.out.println(userDetails);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if(!encoder.matches(user.getPassword(),userDetails.getPassword())){
                return ResponseEntity.ok(new JwtResponse("Login failed wrong Password"));
            }
            final String token = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new JwtResponse(token));
        }catch (Exception e){
            return ResponseEntity.ok(new JwtResponse("Login failed check Email"));
        }

    }
    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
