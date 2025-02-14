# 📢 Tenpo Challenge - Backend API

## 📌 Descripción
Esta API realiza cálculos con un **porcentaje aplicado**, obteniendo dicho porcentaje de un servicio externo.  
Además, implementa:
✅ **Historial de llamadas a la API**  
✅ **Manejo de errores HTTP y validaciones**  
✅ **Rate Limiting (máximo 3 requests por minuto)**  
✅ **Paginación en el historial**  
✅ **Soporte para despliegue en Docker**

---

## 🚀 Instalación y Ejecución

### 🔹 **Requisitos previos**
- **Java 21**
- **Maven 3.8+**
- **Docker (opcional, para la base de datos y despliegue)**
- **Docker Hub (si quieres usar la imagen ya publicada)**

---

### 🔹 **Instalacio con Maven**
1️⃣ **Clonar el repositorio:**
```sh
   git clone git@github.com:navajasleandro/calculator-challenge.git
   cd calculator-challenge
```

2️⃣ Instalar dependencias y compilar el proyecto:

```sh
  mvn clean package
```

### 🔹 **Ejecutar con Docker Compose**
📌 Para levantar la API junto con la base de datos PostgreSQL, ejecuta:

```sh
  docker-compose up --build
```
Esto ejecutará:

✅ PostgreSQL en localhost:5432

✅ API en localhost:8080

📌 Ahora puedes acceder a la API en:

🔗 http://localhost:8080/swagger-ui/index.html


Para detener los contenedores:

```sh
  docker-compose down
```

---
## 📄 **Documentación de la API**

### Ejemplos de interaccion con los servicios

### 📌 1️⃣ **Cálculo con porcentaje aplicado**

GET /calculate?num1=10&num2=5

🔹 Ejemplo con curl:

```sh
  curl -X GET "http://localhost:8080/calculate?num1=10&num2=5" -H  "accept: */*"
```

🔹 Respuesta:

```json
  {
    "num1":10.0,
    "num2":5.0,
    "percentage":10.0,
    "result":16.5
  } 
```

### 📌 2️⃣ **Historial de Llamadas (con paginación)**

GET /history?page=0&size=10

🔹 Ejemplo con curl:

```sh
  curl -X GET "http://localhost:8080/history?page=0&size=10" -H  "accept: */*"
```

🔹 Respuesta:

```json
  [
    {
      "timestamp":"2025-02-13T18:23:11.816806",
      "endpoint":"http://localhost:8080/calculate",
      "parameters":"num1=1&num2=19",
      "response":"{\"num1\":1.0,\"num2\":19.0,\"percentage\":10.0,\"result\":22.0}",
      "statusCode":200
    },
    {
      "timestamp":"2025-02-13T18:23:06.397932",
      "endpoint":"http://localhost:8080/calculate",
      "parameters":"num1=1&num2=-19",
      "response":"{\"status\":400,\"error\":\"Bad Request\",\"cause\":\"Validation failed: calculate.num2: must be greater than 0\"}",
      "statusCode":400
    }
  ]
```

### 🚦 **Rate Limiting**
📌 La API solo permite 3 requests por minuto.

📌 Si se supera este límite, devuelve 429 Too Many Requests.

🔹 Ejemplo con curl (4 requests seguidas):

```sh
  curl -X GET "http://localhost:8080/calculate?num1=10&num2=5" -H  "accept: */*"
  curl -X GET "http://localhost:8080/calculate?num1=10&num2=5" -H  "accept: */*"
  curl -X GET "http://localhost:8080/calculate?num1=10&num2=5" -H  "accept: */*"
  curl -X GET "http://localhost:8080/calculate?num1=10&num2=5" -H  "accept: */*"
```

🔹 Respuesta esperada (429 TOO MANY REQUESTS):

```json
  {
    "status": 429,
    "error": "Too Many Requests",
    "cause": "Rate limit exceeded. Try again later."
  }
```

### ❌ **Manejo de Errores**

| Código HTTP | Descripción | Ejemplo de Causa |
|------------|------------|------------------|
| `400` | Parámetros inválidos | Número negativo en `num1` o `num2` |
| `404` | No se encontró un valor en caché | No hay datos en caché y la API externa falló |
| `429` | Límite de requests alcanzado | Más de 3 requests por minuto |
| `500` | Error interno del servidor | Excepción no controlada en el backend |



---

## 🛠 Desarrollo y Contribución
🔹 Tecnologías utilizadas:

- **Spring Boot 3.4.2**
- **Spring Data JPA**
- **Redis cache**
- **Bucket4J (Rate Limiting)**
- **Springdoc OpenAPI (Swagger)**
- **PostgreSQL (Docker)**


---
## 🚀 **Contacto**

📩 Email: navajas.leandro@gmail.com

🔗 LinkedIn: [Leandro Navajas](https://www.linkedin.com/in/leandro-navajas/)




