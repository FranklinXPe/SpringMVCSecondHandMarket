package com.openwebinars.secondhandmarket.configuracion;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.openwebinars.secondhandmarket.upload.StorageProperties;

@Configuration
@EnableJpaAuditing // De esta manera las clases con atributo anotadas con @CreatedDate se creara automaticamente la marca de tiempo sin que hagamos nada. Sera tomada del sistema
@EnableConfigurationProperties(StorageProperties.class) // Estamos usando los properties para manejar la subida de archivos
public class ConfiguracionAuditoria {

}
