// Simple API helper that calls a Spring Boot backend running on localhost:8080
const API_BASE = "http://localhost:8081/auth";

export async function login({ email, password }) {
  const res = await fetch(`${API_BASE}/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
    credentials: "include" // si usas cookies/sesiones
  });

  const contentType = res.headers.get("content-type") || "";
  let data;
  if (contentType.includes("application/json")) {
    data = await res.json();
  } else {
    data = { text: await res.text() }; // evita que se “pierda” la respuesta
  }

  if (!res.ok) throw { status: res.status, data };
  return data;
}

export async function register(payload) {
  const res = await fetch(`${API_BASE}/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw { status: res.status, data };
  return data;
}
