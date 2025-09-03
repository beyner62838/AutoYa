import React, { useState } from "react";
import Login from "./components/Login";
import Register from "./components/Register";

export default function App() {
  const [view, setView] = useState("login"); // "login" or "register"
  return (
    <div className="app-root">
      {view === "login" ? (
        <Login onSwitch={() => setView("register")} />
      ) : (
        <Register onSwitch={() => setView("login")} />
      )}
    </div>
  );
}
