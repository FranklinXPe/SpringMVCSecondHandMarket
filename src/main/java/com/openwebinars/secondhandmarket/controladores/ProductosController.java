package com.openwebinars.secondhandmarket.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.openwebinars.secondhandmarket.modelo.Producto;
import com.openwebinars.secondhandmarket.modelo.Usuario;
import com.openwebinars.secondhandmarket.servicios.ProductoServicio;
import com.openwebinars.secondhandmarket.servicios.UsuarioServicio;
import com.openwebinars.secondhandmarket.upload.StorageService;

@Controller
@RequestMapping("/app")
public class ProductosController {
	
	/* Necesitaremos los servicios de Producto y Servicio y Almacenamiento de Fichero*/
	@Autowired
	ProductoServicio productoServicio;
	
	@Autowired
	UsuarioServicio usuarioServicio;
	
	@Autowired
	StorageService storageService;
	
	// igualmente necesitamos los datos del Usuario propietario de los productos
	private Usuario usuario;
	
	
	@ModelAttribute("misproductos")
	public List<Producto> misProductos(){
		// Obtenemos el userName (que en este caso es el email)
		String email= SecurityContextHolder.getContext().getAuthentication().getName();
		usuario = usuarioServicio.buscarPorEmail(email);
		// sacamos los productos de este modelo y los guardamos en el "MODELO" para tenerlos siempre disponible
		return productoServicio.productosDeUnPropietario(usuario);
	}
	
	
	//Para mostrar la lista de "Mis productos" sera igual a la lista de productos en la zona publica
	//Con la diferencia que los productos que se muestran son correspondiente al CommandObject "misproductos"
	@GetMapping("/misproductos")
	public String list(Model model, @RequestParam(name = "q", required = false) String query) {
		if(query !=null)
			model.addAttribute("misproductos", productoServicio.buscarMisProductos(query, usuario));
	
		
		return "app/producto/lista";
	}
	
	
	//Si deseo eliminar uno de los productos que elegi para comprar
	@GetMapping("/misproductos/{id}/eliminar")
	public String eliminar(@PathVariable Long id) {
		Producto p=productoServicio.findById(id);
		if(p.getCompra() == null) {
			productoServicio.borrar(p);
		}
		
		return "redirect:/app/misproductos"; // lo redirigiria denuevo a "mis productos"
	}
	
	@GetMapping("/producto/nuevo")
	public String nuevoProductoForm(Model model) {
		model.addAttribute("producto", new Producto());
		return "app/producto/form";
	}
	
	// Para enviar la data del nuevo producto hacemos uso del PostMapping (Maneja tambien los archivos que se esta subiendo)
	@PostMapping("/producto/nuevo/submit")
	public String nuevoProductoSubmit(@ModelAttribute Producto producto, @RequestParam("file") MultipartFile file) {
		
		if(!file.isEmpty()) {
			String imagen= storageService.store(file);
			producto.setImagen(MvcUriComponentsBuilder.fromMethodName(FilesController.class, "serverFile", imagen).build().toUriString());
		}
		
		
		producto.setPropietario(usuario);
		productoServicio.insertar(producto);
		return "redirect:/app/misproductos";
	}

}
