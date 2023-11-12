package com.example.NisumBismark.service;

import com.example.NisumBismark.model.Usuario;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioService {
    List<Usuario> listar();
    Optional<Usuario> porId(UUID id);
    Usuario guardar(Usuario usuario);
    void eliminar(UUID id);
    Optional<Usuario> porEmail(String email);
}
