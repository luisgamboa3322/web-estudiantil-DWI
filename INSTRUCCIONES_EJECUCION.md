# 🚀 INSTRUCCIONES PARA EJECUTAR EL SISTEMA

## ✅ **PROCESOS TERMINADOS:**
- ✅ Node.js (Angular frontend) - terminated
- ✅ Java (Spring Boot backend) - terminated

---

## 🎯 **COMANDOS PARA EJECUTAR EL SISTEMA:**

### **1. BACKEND (Spring Boot)**
```bash
# Navegar a la carpeta del backend
cd EstudiaM-s/demo

# Ejecutar el backend (Puerto 8083)
mvn spring-boot:run
```

### **2. FRONTEND (Angular)**
```bash
# Abrir una nueva terminal
# Navegar a la carpeta del frontend
cd EstudiaM-s/frontend

# Ejecutar el frontend con proxy (Puerto 4201 - RECOMENDADO)
ng serve --host 0.0.0.0 --port 4201 --proxy-config proxy.conf.json
```

### **3. ACCESOS AL SISTEMA:**
- **Frontend:** http://localhost:4201/
- **Backend API:** http://localhost:8083/

---

## 🎨 **LO QUE VERÁS:**

### **Frontend (Angular):**
- ✅ Login moderno con Bootstrap + Tailwind
- ✅ Dashboard de selección de roles
- ✅ Dashboards diferenciados por rol
- ✅ Diseño responsive

### **Backend (Spring Boot):**
- ✅ API REST funcional
- ✅ Base de datos MySQL conectada
- ✅ Autenticación con Spring Security
- ✅ Datos de prueba pre-cargados

---

## 👥 **USUARIOS DE PRUEBA:**
Los usuarios están pre-configurados en el sistema (ver DataInitializer).

---

## 🛠️ **SOLUCIÓN DE PROBLEMAS:**

### **Si no arranca el frontend:**
```bash
# Verificar dependencias
cd EstudiaM-s/frontend
npm install
```

### **Si no arranca el backend:**
```bash
# Verificar MySQL
# Asegúrate de que MySQL esté ejecutándose
# Puerto: 3306
```

---

**¡Ahora puedes ejecutar el sistema y ver el resultado final! 🎉**