package com.openwebinars.secondhandmarket.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.openwebinars.secondhandmarket.modelo.Usuario;
import com.openwebinars.secondhandmarket.servicios.UsuarioServicio;
import com.openwebinars.secondhandmarket.upload.StorageService;

@Controller
public class LoginController {

	@Autowired
	UsuarioServicio usuarioServicio;
	
	// Para la subida del Avatar
	@Autowired
	StorageService storageService;
	
	
	
	// Si alguien accede a la raiz de nuestro sitio, lo llevaremos directamente al public
	// que sera el  listado de los productos
	@GetMapping("/")
	public String welcome() { 
		return "redirect:/public/";
	}
	
	// Ahora configuramos el "show del login" porque el formulario de Login y register es el mismo
	// por eso le agregamos el command object del ususario que queremos crear
	@GetMapping("/auth/login")
	public String login(Model model) {
		model.addAttribute("usuario", new Usuario());
		return "login";
	}
	
	// Ahora configuramos el registro (NO DEL LOGIN ya que esta dentro del circuito de Spring Security y no lo debemos implementar ya)
	// sino del registro del Usuario que lo pasamos antes, lo inyectamos aqui.
	@PostMapping("/auth/register")
	public String register(@ModelAttribute Usuario usuario, @RequestParam("file") MultipartFile file)
	{
		if (!file.isEmpty()) {
			String imagen = storageService.store(file);
			usuario.setAvatar(MvcUriComponentsBuilder
					.fromMethodName(FilesController.class, "serveFile", imagen).build().toUriString());
			
		}
		
		usuarioServicio.registrar(usuario); // lo registramos
		return "redirect:/auth/login";  // Y una vez registrado, lo mandamos al login. (para que se autentique, ahora que se registro en la BD)
	}
	
	
}
