
# react-autoya-auth

Proyecto React (Vite) con pantallas de Login y Registro basadas en el HTML que proporcionaste.
Las pantallas consumen un backend Spring Boot esperado en `http://localhost:8080/api/auth` con las rutas:

- POST /api/auth/login  -> {"username","password"}
- POST /api/auth/register -> {"username","email","password"}

Cómo usar:
1. Tener Node.js instalado.
2. `npm install`
3. `npm run dev`
4. Abrir `http://localhost:5173` (o el puerto que Vite indique).

Notas:
- El proyecto es minimal y pensado como plantilla. Ajusta las rutas de la API según tu backend.
- Si usas Spring Security y cookies/sesiones, revisa `credentials` en `src/api.js`.

