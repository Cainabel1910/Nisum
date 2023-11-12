package com.example.NisumBismark.auth;

import com.example.NisumBismark.jwt.JwtService;
import com.example.NisumBismark.model.Role;
import com.example.NisumBismark.model.Usuario;
import com.example.NisumBismark.repository.UsuarioRepository;
import com.example.NisumBismark.service.EmailValidationService;
import com.example.NisumBismark.service.PasswordValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Date;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        //Verificamos usuario
        UserDetails user = usuarioRepository.findByUsername(request.getUsername()).orElseThrow();
        //Generamos el token
        String token = jwtService.getToken(user);
        //Asignamos valor del nuevo token y actualizamos la fecha de loguin
        Usuario usuarioBD = usuarioRepository.findByUsername(request.getUsername()).orElseThrow();
        usuarioBD.setLastLogin(new Date(System.currentTimeMillis()));
        usuarioBD.setToken(token);
        usuarioRepository.save(usuarioBD);
        //Generamos la respuesta(token)
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);
        return authResponse;
    }

    public Usuario register(Usuario request) {
        //Asignamos el rol por defecto y codificamos la contraseña
        request.setRole(Role.USER);
        request.setPassword(passwordEncoder.encode( request.getPassword()));
        Usuario usuarioNew = usuarioRepository.save(request);
        //Asignamos el nuevo token
        usuarioNew.setToken(jwtService.getToken(request));

        return usuarioNew;

    }
}
