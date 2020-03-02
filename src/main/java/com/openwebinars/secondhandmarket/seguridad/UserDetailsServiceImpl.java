package com.openwebinars.secondhandmarket.seguridad;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.openwebinars.secondhandmarket.modelo.Usuario;
import com.openwebinars.secondhandmarket.repositorio.UsuarioRepository;

/*==== ESTA CLASE LA INYECTAREMOS EN NUESTRA CLASE DE SeguridadConfig =======
 *  Más referencia en: https://openwebinars.net/academia/aprende/spring-boot/4862/   */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UsuarioRepository repositorio;
	
	/* *
	 * POR EL MERO HECHO DE IMPLEMENTAR ESTA INTERFAZ, DEBEMOS REESCRIBIR ESTE METODO QUE BUSCA UN USUARIO POR EL UserName
	 * Y devolver un objeto UserDetails para que pueda completar el proceso de autenticacion
	 * */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		// En este caso el nombre de usuairo es el EMAIL
		Usuario usuario= repositorio.findFirstByEmail(username);
		
		// Aqui es donde tenemos que construir nuestro UserBuilder
		UserBuilder builder=null;
		
		if(usuario !=null) {
			// Construimos el Usuario
			builder=User.withUsername(username);
			builder.disabled(false); // que no este deshabilitado
			builder.password(usuario.getPassword()); // que el password sea el del usuario
			builder.authorities(new SimpleGrantedAuthority("ROLE_USER")); // Utilizamos esta clase para no implementar nuestra propia clase Authority
			
			
		}else {
			throw new UsernameNotFoundException("Usuario no encontrado.");
		}
		
		// SI todo esta bien devolvemos el usuario construido
		return builder.build();
	}

}
