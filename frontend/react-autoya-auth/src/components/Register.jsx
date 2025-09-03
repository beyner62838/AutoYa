import React, { useState } from "react";
import { register } from "../api";

export default function Register({ onSwitch }) {
  const [form, setForm] = useState({
    nombres: "",
    apellidos: "",
    telefono: "",
    email: "",
    password: "",
    rol: "",
    confirm: ""
  });
  const [loading, setLoading] = useState(false);

  const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (form.password !== form.confirm) {
      alert("Las contraseñas no coinciden");
      return;
    }
    setLoading(true);
    try {
      // Call Spring Boot API: POST /api/auth/register
      const payload = { username: form.username, email: form.email, password: form.password };
      await register(payload);
      alert("✅ Registro exitoso. Ahora puedes iniciar sesión.");
      onSwitch();
    } catch (err) {
      console.error(err);
      alert("❌ Error en el registro. Revisa los datos.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <h2>Crear cuenta en AutoYA</h2>
      <form onSubmit={handleSubmit}>
        <div className="input-group">
          <label htmlFor="nombres">Nombres</label>
          <input id="nombres" name="nombres" value={form.nombres} onChange={onChange} required placeholder="Nombres"/>
        </div>
        <div className="input-group">
          <label htmlFor="apellidos">Apellido</label>
          <input id="apellidos" name="apellidos" value={form.apellidos} onChange={onChange} required placeholder="Apellidos" />
        </div>
        <div className="input-group">
          <label htmlFor="telefono">Telefono</label>
          <input id="telefono" name="telefono" value={form.telefono} onChange={onChange} required placeholder="3108526020" />
        </div>
        <div className="input-group">
          <label htmlFor="rol">Rol</label>
          <select id="rol" name="rol" value={form.rol} onChange={onChange} required>
            <option value="">-- Selecciona rol --</option>
            <option value="cliente">Cliente</option>
            <option value="propietario">Propietario</option>
          </select>
        </div>
        <div className="input-group">
          <label htmlFor="email">Email</label>
          <input id="email" name="email" type="email" value={form.email} onChange={onChange} required placeholder="tu@email.com"/>
        </div>
        <div className="input-group">
          <label htmlFor="password">Contraseña</label>
          <input id="password" name="password" type="password" value={form.password} onChange={onChange} required placeholder="Contraseña"/>
        </div>
        <div className="input-group">
          <label htmlFor="confirm">Confirmar Contraseña</label>
          <input id="confirm" name="confirm" type="password" value={form.confirm} onChange={onChange} required placeholder="Repite la contraseña"/>
        </div>
        <button className="btn" type="submit" disabled={loading}>{loading ? 'Guardando...' : 'Registrarme'}</button>
        <div className="extra">
          <p>¿Ya tienes cuenta? <a href="#" onClick={(e) => { e.preventDefault(); onSwitch(); }}>Inicia sesión</a></p>
        </div>
      </form>
    </div>
  );
}
