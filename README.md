# Backend-Repo-Knowledge-Platform

# 📰 Knowledge Platform Article App — Backend

## 1️⃣ Approach

### Architecture Overview
REST API built with Node.js and Express, connected to a MySQL database.
JWT is used for authentication and route protection.

### Folder Structure
src/main/java/com/knowledge/
├── KnowledgePlatformApplication.java    # Main Spring Boot application
├── config/                               # Configuration classes
├── entity/                               # JPA Entity classes
│   ├── User.java                        # User entity
│   └── Article.java                     # Article entity
├── repository/                           # JPA Repository interfaces
│   ├── UserRepository.java
│   └── ArticleRepository.java
├── dto/                                  # Data Transfer Objects
│   ├── LoginRequest.java
│   ├── SignupRequest.java
│   ├── ArticleDTO.java
│   ├── CreateArticleRequest.java
│   └── AuthResponse.java
├── security/                             # JWT and Security
│   ├── JwtTokenProvider.java
│   └── CustomUserDetailsService.java
├── service/                              # Business logic
│   ├── AuthService.java                 # Authentication logic
│   ├── ArticleService.java              # Article management logic
│   └── AIService.java                   # AI integration 
├── controller/                           # REST Controllers
│   ├── AuthController.java              # Auth endpoints
│   ├── ArticleController.java           # Article endpoints
│   └── UserController.java              # User endpoints


### Key Design Decisions
- JWT-based stateless authentication
- Middleware-based route protection
- Separation of concerns (routes → controllers → models)

---

## 2️⃣ AI Usage

| Area | How AI Was Used |
|------|----------------|
| Code Generation | Used ChatGPT to generate initial Express boilerplate and route structure |
| API Design | Asked Claude to suggest RESTful naming conventions |
| Refactoring | Manually reviewed and optimized all AI-generated code |

> Example: "Used ChatGPT to generate initial  boilerplate, then manually 
> optimized middleware, error handling, and JWT logic."

---

## 3️⃣ Setup Instructions

### Prerequisites
- MySQL

### Environment Variables
Create a `.env` file in the root:
```
PORT=8080
jwt.secret=your_secret_key_min_256_bits_long_keep_it_secure_change_this_in_production_environment
anthropic.api.key=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
```

### Backend Setup
```bash
# Clone the repo
git clone https://github.com/Priyatham03/Backend-Repo-Knowledge-Platform.git

# Navigate into folder
cd project-backend

# Install dependencies
npm install

# Start the server
npm run dev
```

Server runs at: `http://localhost:8080`
