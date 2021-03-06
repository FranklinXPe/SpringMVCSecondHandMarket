package com.openwebinars.secondhandmarket.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openwebinars.secondhandmarket.modelo.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

	Usuario findFirstByEmail(String email);
}
