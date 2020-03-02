package com.openwebinars.secondhandmarket.seguridad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



/* Más referencia en: https://openwebinars.net/academia/aprende/spring-boot/4862/ */
@Configuration
@EnableWebSecurity
public class SeguridadConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	UserDetailsService userDetailsService;  // Más referencia en: https://openwebinars.net/academia/aprende/spring-boot/4862/
	
	
	/*========= SE OCUPA DE LA AUTENTICACION ============*/
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder()); //En este caso si vamos a implementarlo
		
		// De esta forma ya tenemos configurado la autenticación haciendo uso del UserDetailService
		// y cada vez que quiera hacer la hacer la autenticación utilizara este servicio, localizara al usuario
		// en la base de datos y tambien encriptara la contraseña con BCrypt
	}
	
	
	//Usamos el password encode de BCrypt que asi de sencillo nos provee Spring, solo instanaciando la clase, llamando a passwordencoder y colocandolo como BEAN
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	/*========= SE OCUPA DE LA AUTORIZACION ============*/
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/", "/webjars/**", "/css/**", "/h2-console/**", "/public/**", "/auth/**", "/files/**")
				.permitAll() // debe permitir todas las url de arriba
				.anyRequest().authenticated() // cualquier otra peticion debe estar autenticada
			.and()
			.formLogin() // Vamos habilitar nuestro formulario de Login
				.loginPage("/auth") // cuya pagina estara en esta URI
				.defaultSuccessUrl("/public/index",true) // Cuando se loguee correctamente nos lleve a esta pagina URI
				.loginProcessingUrl("/auth/login-post") // Aqui si usaremos un controlador para manejar la peticion
				.permitAll() // permitirselo a todos
			.and()
			.logout() // añadimos el logout
				.logoutUrl("/auth/logout") // donde la URL del logout es este
				.logoutSuccessUrl("/public/index"); // Y cuando se desloguee nos lleve a la zona publica
				
		http.csrf().disable(); // añadir lo necesario para poder utilizar la consola de H2
		http.headers().frameOptions().disable(); // Por ultimo nos queda añadir lo necesario para poder utilizar la consola de H2 que deshabilita el FRAME de la consola
	
			
	}
	

}
