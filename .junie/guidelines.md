# Lineamientos del Proyecto Backend - LA Cosmetics

## Descripción General
Este proyecto constituye el backend para una aplicación de gestión de manufactura 
y comercialización de productos cosméticos. La planta principal está ubicada en 
Barranquilla, Colombia.

## Estructura del Proyecto

### Arquitectura
- Arquitectura REST basada en Spring Boot
- Implementación de patrón MVC (Model-View-Controller)
- Diseño modular por dominios de negocio

### Tecnologías Principales
- Java 21
- Spring Boot
- Spring Data JPA
- Jakarta EE
- Lombok

## Dominios Principales

### 1. Compras
- Gestión de proveedores
- Manejo de documentación (RUT, Cámara de Comercio)
- Sistema de búsqueda y paginación

### 2. Manufactura
- Gestión de procesos de producción
- Control de calidad
- Trazabilidad de lotes

### 3. Inventario
- Control de materias primas
- Gestión de productos terminados
- Movimientos de inventario

## Estándares de Desarrollo

### Nomenclatura
- Clases: PascalCase (ej: ProveedorService)
- Métodos: camelCase (ej: saveProveedor)
- Paquetes: minúsculas (ej: lacosmetics.planta)
- Endpoints: kebab-case (ej: /api/proveedores/search-by-name)

### Estructura de API REST
- Use nombres en plural para recursos (ej: /proveedores)
- Implementar paginación donde sea necesario
- Documentar todos los endpoints

### Manejo de Errores
- Usar códigos HTTP apropiados
- Implementar manejo de excepciones global
- Proporcionar mensajes de error descriptivos

### Seguridad
- Implementar autenticación JWT
- Validación de datos en endpoints
- Sanitización de entradas
- Control de acceso basado en roles (RBAC)

## Buenas Prácticas

### Código
- Mantener métodos concisos y enfocados
- Documentar código complejo
- Seguir principios SOLID
- Implementar pruebas unitarias

### Base de Datos
- Usar migrations para cambios en esquema
- Implementar índices apropiados
- Optimizar consultas

### Documentación
- Mantener README actualizado
- Documentar cambios significativos
- Incluir ejemplos de uso en API

## Control de Versiones
- Usar Git Flow
- Commits descriptivos
- Pull Requests con revisión de código
- No commits directos a main/master

## Despliegue
- Configuración por ambiente (dev, qa, prod)
- Variables de entorno para configuraciones sensibles
- Logs estructurados
- Monitoreo de aplicación

## Mantenimiento
- Actualizaciones regulares de dependencias
- Revisiones de seguridad periódicas
- Backup de datos
- Monitoreo de rendimiento
