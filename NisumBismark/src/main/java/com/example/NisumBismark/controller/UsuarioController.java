package com.example.NisumBismark.controller;

import com.example.NisumBismark.model.Usuario;
import com.example.NisumBismark.service.EmailValidationService;
import com.example.NisumBismark.service.PasswordValidationService;
import com.example.NisumBismark.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordValidationService passwordValidationService;

    @Autowired
    private EmailValidationService emailValidationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<?> listarUsuarios(){
        try {
            List<Usuario> usuarioList = new ArrayList<>(usuarioService.listar());

            if (usuarioList.isEmpty()){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections
                        .singletonMap("mensaje","No se encontraron usuarios registrados."));
            }

            return new ResponseEntity<>(usuarioList, HttpStatus.OK);
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections
                    .singletonMap("mensaje","Ocurrio un error en la peticion."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable UUID id){
        Optional<Usuario> usuarioOptional = usuarioService.porId(id);
        if(usuarioOptional.isPresent()){
            return ResponseEntity.ok(usuarioOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections
                .singletonMap("mensaje","No se encontro la informacion del usuario."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Usuario usuario, BindingResult result, @PathVariable UUID id){
        if (result.hasErrors()){
            return validar(result);
        }
        Optional<Usuario> usuarioOptional = usuarioService.porId(id);
        if(usuarioOptional.isPresent()){
            Usuario usuarioDB = usuarioOptional.get();

            if (!emailValidationService.validateEmail(usuario.getEmail())){
                return ResponseEntity.badRequest()
                        .body(Collections
                                .singletonMap("mensaje","El correo electrónico no cumple con el formato requerido."));
            }

            if (!usuario.getEmail().equalsIgnoreCase(usuarioDB.getEmail()) && usuarioService.porEmail(usuario.getEmail()).isPresent()){
                return ResponseEntity.badRequest()
                        .body(Collections
                                .singletonMap("mensaje","El correo ya registrado."));
            }

            if(!passwordValidationService.validatePassword(usuario.getPassword())){
                return ResponseEntity.badRequest()
                        .body(Collections
                                .singletonMap("mensaje","La contraseña no cumple con el formato requerido."));
            }

            usuarioDB.getPhones().clear();
            usuarioDB.setUsername(usuario.getUsername());
            usuarioDB.setEmail(usuario.getEmail());
            usuarioDB.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuarioDB.getPhones().addAll(usuario.getPhones());
            usuarioDB.setModified(new Date(System.currentTimeMillis()));
            return new ResponseEntity<>(usuarioService.guardar(usuarioDB), HttpStatus.CREATED);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections
                .singletonMap("mensaje","No se encontro la informacion del usuario."));
    }

    @PutMapping("inactivar/{id}")
    public ResponseEntity<?> inactivar(@PathVariable UUID id){

        Optional<Usuario> usuarioOptional = usuarioService.porId(id);
        if(usuarioOptional.isPresent()){
            usuarioOptional.get().setActive(false);
            usuarioOptional.get().setModified(new Date(System.currentTimeMillis()));
            return new ResponseEntity<>(usuarioService.guardar(usuarioOptional.get()), HttpStatus.CREATED);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections
                .singletonMap("mensaje","No se encontro la informacion del usuario."));

    }

    @PutMapping("activar/{id}")
    public ResponseEntity<?> activar(@PathVariable UUID id){

        Optional<Usuario> usuarioOptional = usuarioService.porId(id);
        if(usuarioOptional.isPresent()){
            usuarioOptional.get().setActive(true);
            usuarioOptional.get().setModified(new Date(System.currentTimeMillis()));
            return new ResponseEntity<>(usuarioService.guardar(usuarioOptional.get()), HttpStatus.CREATED);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections
                .singletonMap("mensaje","No se encontro la informacion del usuario."));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable UUID id){
        Optional<Usuario> usuarioOptional = usuarioService.porId(id);
        if(usuarioOptional.isPresent()){
            usuarioService.eliminar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections
                    .singletonMap("mensaje","Informacion del usuario eliminada con exito."));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections
                .singletonMap("mensaje","No se encontro la informacion del usuario."));
    }

    private static ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField(), "El campo "+err.getField()+ " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}
