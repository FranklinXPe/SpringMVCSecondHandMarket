package com.openwebinars.secondhandmarket.servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openwebinars.secondhandmarket.modelo.Compra;
import com.openwebinars.secondhandmarket.modelo.Producto;
import com.openwebinars.secondhandmarket.modelo.Usuario;
import com.openwebinars.secondhandmarket.repositorio.CompraRepository;

@Service
public class CompraServicio {
	
	@Autowired
	CompraRepository repositorio; // En el dominio de la Compra, incluimos su respectivo repositorio
	
	@Autowired
	ProductoServicio productoServicio; // En el dominio de Compra, incluimos el "servicio de Producto" que es de la relacion foreing key con Compras

	public Compra insertar(Compra c, Usuario u) {
		c.setPropietario(u);
		return repositorio.save(c);
	}
	
	public Compra insertar(Compra c) {
		return repositorio.save(c);
	}
	
	// Por el modelo de datos esta establecido, sabemos que que: VER ENTIDADES y el diagrama de clases
	//La compra no conoce los productos que tiene dentro, sino es el cada producto (en este caso solo puede ser comprado una vez)
	// es el Producto quien sabe a qué compra pertenece
	public Producto addProductoCompra(Producto p, Compra c) { 
		p.setCompra(c);// Agregas una compra al producto
		return productoServicio.editar(p); // añadimos nuevos productos a ella
	}
	
	public Compra buscarPorId(long id) {
		return repositorio.findById(id).orElse(null);
	}
	
	public List<Compra> todas(){
		return repositorio.findAll();
	}
	
	
	public List<Compra> porPropietario(Usuario u){
		return repositorio.findByPropietario(u);
	}
	
}
