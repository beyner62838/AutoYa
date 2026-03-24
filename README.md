#  AutoYa — Marketplace P2P de alquiler de vehículos (multiciudad)

##  Pitch 
- **Problema:** El alquiler tradicional de autos en agencias es rígido, costoso y con disponibilidad limitada; al mismo tiempo, muchos vehículos particulares están subutilizados.  
- **Solución:** Plataforma P2P donde los propietarios publican sus vehículos y los clientes los reservan con seguridad, contratos digitales y trazabilidad.  
- **Diferenciador:** Multiciudad, arquitectura distribuida con **CQRS + Redis** para disponibilidad y búsqueda rápida, y **SAGA** para gestión de reservas/pagos de forma resiliente.  
---

## 🔗 Enlaces Únicos 
- Mockups (Figma/Canva): [link]  
- Tablero Jira (board): (https://corhuila-team-n93mngxo.atlassian.net/jira/software/projects/AUT/boards/34?atlOrigin=eyJpIjoiZDU1ZTUwZTE0NjI2NDkyYTlhOWMyMmNiYWUyNDQ4NmYiLCJwIjoiaiJ9)  
- Roadmap Jira: https://corhuila-team-n93mngxo.atlassian.net/jira/software/projects/AUT/boards/34/backlog?atlOrigin=eyJpIjoiZTllMDRhMWQyMjI0NDQ0YjgzOGEyYzA3NWFmMTA3NWIiLCJwIjoiaiJ9 
- Backlog import (CSV): [/docs/backlog.csv]  
- Presentación:   
- Diagrama de arquitectura: [link]  
- Repos Backend: [link]  
- Repos Frontend: [link]  
- Repos Data: [link]  

---

##  Alcance 
- Registro/login (roles **CLIENTE** / **PROPIETARIO**).  
- Publicación de vehículos (propietario) con fotos, disponibilidad y precio por día.  
- Búsqueda por ciudad, fechas y filtros (precio, tipo de vehículo, etc.).  
- Reserva con **HOLD + confirmación** (pago mock con contrato digital).  
- Notificación de confirmación al propietario y cliente.  

---

##  Arquitectura 
- **Backend:** Spring Boot (microservicios: Auth, Vehículos, Disponibilidad, Reservas, Pagos-mock, Notifs).  
- **Cache/locking:** Redis.  
- **Búsqueda:** PostgreSQL (MVP). 
- **Storage imágenes/documentos:** MinIO/S3.  
- **Frontend:** Web (PWA).  
- **Mensajería/eventos (avanzado):** MQTT.  

---

## Cómo trabajamos 
- Sprints de **1 semanas** (6 sprints).  
- Board en Jira con **Epics, User Stories** y criterios de aceptación (G/W/T).  
- **DoR/DoD** documentados en la wiki del proyecto.  

---

##  Registro de decisiones 
- `/docs/adr-001-arquitectura.md` → CQRS + Redis para disponibilidad y reservas.  
- `/docs/adr-002-pagos-mock-primero.md` → Pago simulado en MVP.

---

## Repositorios del proyecto
- **Frontend AutoYa**: https://github.com/AlvaroV19/AutoYa-Frontend
- **Backend AutoYa**: https://github.com/AlvaroV19/AutoYa-Backend
- **Docs AutoYa**: https://github.com/AlvaroV19/AutoYa-Docs
- **Deployment AutoYa**: https://github.com/AlvaroV19/AutoYa-Deployment





