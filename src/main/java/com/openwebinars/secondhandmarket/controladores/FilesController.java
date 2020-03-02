package com.openwebinars.secondhandmarket.controladores;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.openwebinars.secondhandmarket.upload.StorageService;

@Controller
public class FilesController {

	StorageService storageService;
	
	
	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serverFile(@PathVariable String filename){
		Resource file = storageService.loadAsResource(filename);
		
		return ResponseEntity.ok().body(file);
	}
	
}
