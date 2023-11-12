package com.example.NisumBismark.auth;

import com.example.NisumBismark.auth.AuthService;
import com.example.NisumBismark.auth.AuthResponse;
import com.example.NisumBismark.model.Role;
import com.example.NisumBismark.model.Usuario;
import com.example.NisumBismark.repository.UsuarioRepository;
import com.example.NisumBismark.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void testRegister() {
        // Datos de prueba
        Usuario usuarioRequest = new Usuario();
        usuarioRequest.setUsername("usuario123");
        usuarioRequest.setPassword("as3A_2er");
        usuarioRequest.setEmail("usuario@example.com");

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(UUID.fromString("c07d68e5-481c-4e33-8ed4-344ce856f9b2"));
        usuarioGuardado.setUsername(usuarioRequest.getUsername());
        usuarioGuardado.setPassword("as3A_2er"); // La contraseña se codificará
        usuarioGuardado.setEmail(usuarioRequest.getEmail());
        usuarioGuardado.setRole(Role.USER);

        // Configuración de comportamientos simulados
        when(passwordEncoder.encode(anyString())).thenReturn("as3A_2er");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);
        when(jwtService.getToken(any(Usuario.class))).thenReturn("tokenGenerado");

        // Llamada al método de registro
        Usuario usuarioRegistrado = authService.register(usuarioRequest);

        // Verificaciones
        assertEquals(usuarioGuardado.getId(), usuarioRegistrado.getId());
        assertEquals(usuarioGuardado.getUsername(), usuarioRegistrado.getUsername());
        assertEquals(usuarioGuardado.getPassword(), usuarioRegistrado.getPassword());
        assertEquals(usuarioGuardado.getEmail(), usuarioRegistrado.getEmail());
        assertEquals(usuarioGuardado.getRole(), usuarioRegistrado.getRole());
        assertEquals("tokenGenerado", usuarioRegistrado.getToken());

        // Verifica que los métodos se llamaron correctamente
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(jwtService, times(1)).getToken(any(Usuario.class));
    }
}
