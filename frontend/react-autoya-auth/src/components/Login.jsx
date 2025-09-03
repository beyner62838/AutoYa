import React, { useState } from "react";
import { login } from "../api";

export default function Login({ onSwitch }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [successImg, setSuccessImg] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      // Call Spring Boot API: POST /api/auth/login
      const data = await login({ email, password });
      alert("✅ Bienvenido " + (data.email || email));
      // display image dynamically like the original HTML example
      setSuccessImg("https://drive.google.com/file/d/1ewU7ra-ya_hqWqTlURtmf9wquzW3a6-P/view?usp=drive_link");
    } catch (err) {
      console.error(err);
      alert("❌ Usuario o contraseña incorrectos");
      alert("email:"+email);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <h2>¡Bienvenido a AutoYA!❤️</h2>
      <form id="loginForm" onSubmit={handleSubmit}>
        <div className="input-group">
          <label htmlFor="email">Usuario</label>
          <input
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Ingresa tu usuario"
            required
          />
        </div>
        <div className="input-group">
          <label htmlFor="password">Contraseña</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Ingresa tu contraseña"
            required
          />
        </div>
        <button className="btn" type="submit" disabled={loading}>
          {loading ? "Cargando..." : "Iniciar sesión"}
        </button>
        <div className="extra">
          <p>¿No tienes cuenta? <a href="#" onClick={(e) => { e.preventDefault(); onSwitch(); }}>Regístrate</a></p>
          <p>¿Olvidaste tu contraseña? <a href="#">Recupérala aquí</a></p>
        </div>
      </form>
      {successImg && <img src={successImg} alt="success" style={{marginTop:12, borderRadius:8, maxWidth:"100%"}} />}
    </div>
  );
}
