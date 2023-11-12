package com.example.NisumBismark.auth;

import com.example.NisumBismark.model.Usuario;
import com.example.NisumBismark.service.EmailValidationService;
import com.example.NisumBismark.service.PasswordValidationService;
import com.example.NisumBismark.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private PasswordValidationService passwordValidationService;
    @Autowired
    private EmailValidationService emailValidationService;
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping(value = "login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "register")
    public ResponseEntity<?> register(@Valid @RequestBody Usuario request, BindingResult result) {
        //Verificamos que los datos esten correctos
        if (result.hasErrors()){
            return validar(result);
        }
        //Validamos la estructura de correo
        if (!emailValidationService.validateEmail(request.getEmail())){
            return ResponseEntity.badRequest()
                    .body(Collections
                            .singletonMap("mensaje","El correo electrónico no cumple con el formato requerido."));
        }
        //Verificamos si el correo se encuentra registrado
        if (usuarioService.porEmail(request.getEmail()).isPresent()){
            return ResponseEntity.badRequest()
                    .body(Collections
                            .singletonMap("mensaje","El correo ya registrado."));
        }
        //Verificamos si la contraseña cumple com el formato
        if(!passwordValidationService.validatePassword(request.getPassword())){
            return ResponseEntity.badRequest()
                    .body(Collections
                            .singletonMap("mensaje","La contraseña no cumple con el formato requerido."));
        }
        return ResponseEntity.ok(authService.register(request));
    }

    private static ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField(), "El campo "+err.getField()+ " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}
