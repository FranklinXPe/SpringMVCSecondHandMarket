package com.openwebinars.secondhandmarket.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.openwebinars.secondhandmarket.modelo.Usuario;
import com.openwebinars.secondhandmarket.repositorio.UsuarioRepository;

@Service
public class UsuarioServicio {

	@Autowired
	private UsuarioRepository repositorio;
	
	private @Autowired
	BCryptPasswordEncoder passwordEncoder; // la contraseña la recibimos en texto plano del formulario y con esto lo encriptamos
	
	
	public Usuario registrar(Usuario u) {
		u.setPassword(passwordEncoder.encode(u.getPassword())); // De esta manera se almacena el usuario con la contraseña Encriptada
		return repositorio.save(u);
	}
	
	
	public Usuario findById(long id) {
		return repositorio.findById(id).orElse(null);
	}
	
	public Usuario buscarPorEmail(String email) {
		return repositorio.findFirstByEmail(email);
	}
}
