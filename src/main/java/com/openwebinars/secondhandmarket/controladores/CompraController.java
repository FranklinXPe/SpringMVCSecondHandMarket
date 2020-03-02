package com.openwebinars.secondhandmarket.controladores;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.openwebinars.secondhandmarket.modelo.Compra;
import com.openwebinars.secondhandmarket.modelo.Producto;
import com.openwebinars.secondhandmarket.modelo.Usuario;
import com.openwebinars.secondhandmarket.servicios.CompraServicio;
import com.openwebinars.secondhandmarket.servicios.ProductoServicio;
import com.openwebinars.secondhandmarket.servicios.UsuarioServicio;

@Controller
@RequestMapping("/app") // Este controler esta en la zona privada
public class CompraController {

	// La compra es donde coinciden los 3 servicios
	@Autowired
	CompraServicio compraServicio; // xq acabermos comprando
	@Autowired
	ProductoServicio productoServicio; // los productos lo insertaremos en el carrito
	@Autowired
	UsuarioServicio usuarioServicio; // quien es la persona que hace la compra
	
	
	//Para manejar las sesiones entre paginas
	@Autowired
	HttpSession session;
	
	// Almacenamos el usuario que esta logueado
	private Usuario usuario;
	
	// Vamos a necesitar tener como ModelAttribute el "carrito"
	// Estos metodos en un controlador anotado con @ModelAttribute iban a poner dentro del modelo
	// el resultado de ejecutar este metodo.
	@ModelAttribute("carrito")
	public List<Producto> productosCarrito(){ // Aqui pondremos los productos del carrito
		
		// El carrito lo vamos a sacar de la sesion, obteniendo una serie de Id de los productos que vamos a comprar
		List<Long> contenido=(List<Long>) session.getAttribute("carrito");
		
		// Para poder ver los productos del carrito, buscaremos los productos por esos IDs y lo devolveremos caso contrario mostrara vacio
		return (contenido ==null)? null: productoServicio.variosPorId(contenido);
	}
	
	//Coste total de los productos del carrito
	@ModelAttribute("total_carrito")
	public Double totalCarrito() {
		List<Producto> productosCarrito=productosCarrito(); // obtenemos todos los productos del carrito
		if(productosCarrito !=null)
			return productosCarrito.stream() // sacamos el precio y los vamos subando
							.mapToDouble(p -> p.getPrecio())
							.sum(); // y devolvemos la suma final.
		return 0.0;
	}
	
	
	//Vamos a obtener un listado de todas nuestras compras
	//
	@ModelAttribute("mis_compras")
	public List<Compra> misCompras(){
		String email=SecurityContextHolder.getContext().getAuthentication().getName(); // Para saber quien somos nosostros ? 
		usuario=usuarioServicio.buscarPorEmail(email); // buscamos el usuario x email
		return compraServicio.porPropietario(usuario); // retornamos todas las compras por propietario
	}
	
	
	
	// reornamos a la plantilla correspondiente
	@GetMapping("/carrito")
	public String verCarrito(Model model) {
		return "app/compra/carrito";
	}
	
	
	// Si queremos añadir un producto al carrito
	@GetMapping("/carrito/add/{id}")
	public String addCarrito(Model model, @PathVariable Long id) {
		List<Long> contenido=(List<Long>) session.getAttribute("carrito");
		if(contenido == null) {
			contenido = new ArrayList<Long>(); // si es nulo, creamos el contenido
		}
		
		if(!contenido.contains(id) ) { // para no añadir duplicados, vemos si ya esta agregado en el carrito
			contenido.add(id);
		}
		
		session.setAttribute("carrito", contenido); //Volvemos a almacenar el carrito en la session 
		return "redirect:/app/carrito"; // nos redirigimos a visualizarlo
	}
	
	
	@GetMapping("/carrito/eliminar/{id}")
	public String borrarDeCarrito(Model model, @PathVariable Long id) {
		List<Long> contenido=(List<Long>) session.getAttribute("carrito");
		if(contenido == null) {
			return "redirect:/public"; // alguien esta tratando de enviarnos consultas maliciosas, no hay carrito y quiere borrar un item?
		}
		
		contenido.remove(id); // sino, eliminamos el Id
		if(contenido.isEmpty() ) { 
			session.removeAttribute("carrito"); // si el carrito esta vacio, lo sacamos de la sesion
		}else
			session.setAttribute("carrito", contenido); // sino agregamos el carrito actualizado a la sesion
		
		
		return "redirect:/app/carrito"; // nos redirigimos a visualizarlo
	}
	
	
	// El proceso de finalizacion de compra o checkout 
	@GetMapping("/carrito/finalizar")
	public String checkout() {
		List<Long> contenido=(List<Long>) session.getAttribute("carrito"); // obtenemos la session del carrito
		if(contenido ==null) { 
			return "redirect:/public"; //si no hay nada lo llevamos al public (listado de productos)
		}
		
		List<Producto> productos=productosCarrito();

		
		Compra c=compraServicio.insertar(new Compra(),usuario); // insertamos una "nueva compra" con este usuario
		
		// añadimos a esa compra los diferentes productos, osea asignando a cada producto la compra a la que estan asignados
		productos.forEach(p -> compraServicio.addProductoCompra(p, c)); 
		session.removeAttribute("carrito"); // quitariamos el carrito desde la session
		
		return "redirect:/app/compra/factura/"+c.getId(); // nos redirigimos hacia la factura
	}
	
	
	@GetMapping("/compra/factura/{id}")
	public String factura(Model model, @PathVariable Long id) {
		Compra c=compraServicio.buscarPorId(id); // buscamos la compra
		List<Producto> productos=productoServicio.productosDeUnaCompra(c); // sacamos los productos de esa compra
		model.addAttribute("productos",productos); // mostrar en esa estructura de la pagina web, el listado de los productos con sus precios
		model.addAttribute("compra",c);
		model.addAttribute("total_compra",productos.stream().mapToDouble(p-> p.getPrecio()).sum());
		return "/app/compra/factura"; // lo redirigimos a la vista
	}
	
	
	@GetMapping("/miscompras")
	public String verMisCompras(Model model) {
		return "/app/compra/listado";
	}
}
