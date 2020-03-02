package com.openwebinars.secondhandmarket.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openwebinars.secondhandmarket.modelo.Compra;
import com.openwebinars.secondhandmarket.modelo.Producto;
import com.openwebinars.secondhandmarket.modelo.Usuario;

public interface ProductoRepository extends JpaRepository<Producto, Long>  {

	List<Producto> findByPropietario(Usuario propietario);
	
	List<Producto> findByCompra(Compra compra);
	
	List<Producto> findByCompraIsNull(); // Osea productos que todavia esten en venta
	
	List<Producto> findByNombreContainsIgnoreCaseAndCompraIsNull(String  nombre);
	
	List<Producto> findByNombreContainsIgnoreCaseAndPropietario(String nombre, Usuario propietario );
	
}
