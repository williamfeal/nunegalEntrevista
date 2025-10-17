# 🧠 Backend Developer Technical Test – Similar Products API

## 🚀 Overview

This project implements a **Spring Boot 3 (WebFlux)** microservice that exposes a REST API to retrieve similar products for a given product ID.

The service **consumes an external mock system** (running on Docker, port `3001`) that provides existing product information and similar product IDs, then **aggregates and returns detailed information** to the caller through its own API.

---

## 🧩 Architecture

### 🔹 Purpose

Expose the following endpoint:


that returns a list of similar product details, using the following external APIs (provided by a mock container):

- `GET /product/{productId}/similarids` → returns IDs of similar products
- `GET /product/{productId}` → returns product details for each ID

### 🔹 Design

The application follows a **clean / hexagonal architecture**:

com.example.similarproducts
├── application/ → Business logic & use cases
│ └── SimilarProductsService.java
│ └── exception/

├── config/ → WebClient + property configuration

├── controller/ → REST endpoints exposed

├── domain/ → Immutable domain models (records)

├── infrastructure/
│ ├── client/ → External HTTP adapters (Catalog client)
│ └── http/ → Global exception handling

└── SimilarProductsApplication.java → Application entry point


### 🔹 Technologies

| Stack | Version | Purpose |
|--------|----------|----------|
| Java | 17+ | Runtime |
| Spring Boot | 3.x | Core framework |
| Spring WebFlux | Reactive API + WebClient |
| Reactor | Reactive Streams (`Mono`, `Flux`) |
| JUnit 5 + Mockito | Unit testing |
| OkHttp MockWebServer | HTTP mock testing |
| Docker Compose | Integration environment |
| k6 + InfluxDB + Grafana | Performance testing |

---

### ⚙️ How It Works

1. Client calls `GET /product/{id}/similar` on **port 5000**.
2. The service calls the mock system (`http://localhost:3001`):
    - `/product/{id}/similarids`
    - `/product/{id}` for each returned ID
3. The service aggregates results and responds with:

json:
[
  { "id": "2", "name": "Dress", "price": 19.99, "availability": true },
  { "id": "3", "name": "Blazer", "price": 29.99, "availability": false },
  { "id": "4", "name": "Boots", "price": 39.99, "availability": true }
]


4. Errors are handled gracefully:
    Product not found → 404 Not Found
    External system error → 502 Bad Gateway
    Unexpected error → 500 Internal Server Error

🧱 Components
    Component	                    Description
    CatalogSimilarProductsClient	Reactive HTTP client to the external catalog mock
    SimilarProductsService	        Orchestrates calls to fetch IDs and details in parallel
    SimilarProductsController	    Exposes /product/{id}/similar endpoint
    RestExceptionHandler	        Translates exceptions into HTTP responses
    WebClientConfig	                Configures WebClient (timeouts, base URL, memory limits)
    ProductDetail	                Immutable record representing product data.

🧪 Testing
🔹 Unit Tests Included
Layer	            Test Class	                        Framework       	            Coverage
HTTP Client	        CatalogSimilarProductsClientTest	MockWebServer + Reactor	        Valid and error responses
Service	            SimilarProductsServiceTest	        Mockito + StepVerifier	        Order, concurrency, errors
Controller	        SimilarProductsControllerTest	    WebFluxTest + WebTestClient  	200, 404, 502 responses

🧰 Run Locally (with mocks)
1️⃣ Start the mock environment
Make sure Docker is installed and run:
docker-compose up -d simulado influxdb grafana

2️⃣ Start the application
mvn spring-boot:run

Your API will be available at:
http://localhost:5000/product/1/similar

3️⃣ Validate mocks

Try in your browser or terminal:
http://localhost:3001/product/1/similarids

📊 Performance Testing

You can execute the same test the evaluators use:
docker-compose run --rm k6 run scripts/test.js

| Criterion                          | Description                           
| ---------------------------------- | ------------------------------------- 
| **Code clarity & maintainability** | Clean architecture, readable, modular 
| **Performance**                    | Reactive, concurrent, efficient      
| **Resilience**                     | Handles 404, 502, 500 correctly      

The implementation fully complies with the technical test requirements.
server:
  port: 5000
catalog:
  base-url: http://localhost:3001
  connect-timeout: PT2S
  read-timeout: PT3S

📦 Project setup
| Command                | Description                             |
| ---------------------- | --------------------------------------- |
| `mvn clean verify`     | Run all unit tests                      |
| `mvn spring-boot:run`  | Start the application                   |
| `docker-compose up -d` | Start mocks and performance environment |
| `docker-compose down`  | Stop all containers                     |

🧰 Example Endpoints
| HTTP  | Path                    | Description                                    |
| ----- | ----------------------- | ---------------------------------------------- |
| `GET` | `/product/{id}/similar` | Returns list of similar products               |
| `GET` | `/actuator/health`      | (Optional) Health check if actuator is enabled |


👨‍💻 Author

Developed by William Feal
Spring Boot / WebFlux backend implementation for the Backend Developer Technical Test.

