# Spring Security + BCrypt Authentication Demo

A beginner-friendly Spring Boot project demonstrating user registration and login with **BCrypt password hashing**, **form-based authentication**, and **MySQL** as the database.

---

## What This Project Does

- Users can **register** with a username and password
- Passwords are **hashed with BCrypt** before being stored in MySQL — the plain-text password is never saved
- On **login**, Spring Security verifies credentials automatically against the database
- Authenticated users are redirected to a protected home page
- Unauthenticated users are redirected to the login page
- Users can **logout** and their session is invalidated

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2 |
| Security | Spring Security 6 |
| Password Hashing | BCrypt (strength 10) |
| Database | MySQL 8+ |
| ORM | Spring Data JPA / Hibernate |
| Templating | Thymeleaf |
| Build Tool | Maven |
| Java Version | Java 17+ |

---

## Project Structure

```
src/main/java/com/example/security/
│
├── SpringSecurityDemoApplication.java   ← Entry point
│
├── model/
│   └── User.java                        ← Entity: id, username, password, role
│
├── repository/
│   └── UserRepository.java              ← JPA queries (findByUsername, etc.)
│
├── service/
│   ├── UserService.java                 ← Register logic, BCrypt encoding
│   └── CustomUserDetailsService.java    ← Loads user from DB for Spring Security
│
├── config/
│   └── SecurityConfig.java              ← Filter chain, login/logout rules, BCrypt bean
│
└── controller/
    └── AuthController.java              ← Routes: /login, /register, /home

src/main/resources/
├── application.properties               ← DB config, server port
└── templates/
    ├── login.html                       ← Login form page
    ├── register.html                    ← Registration form page
    └── home.html                        ← Protected welcome page
```

---

## How It Works

### Registration Flow

```
User fills register form → POST /register
  → AuthController.registerUser()
  → UserService.register()
  → BCryptPasswordEncoder.encode(plainPassword)
  → Save { username, hashedPassword, role } to MySQL
  → Redirect to /login
```

### Login Flow

```
User fills login form → POST /login  (handled by Spring Security internally)
  → CustomUserDetailsService.loadUserByUsername()
  → Fetches user from MySQL
  → BCrypt.matches(plainPassword, storedHash)  ✓
  → Session created, redirect to /home
```

### Logout Flow

```
User clicks Logout → POST /logout
  → Spring Security invalidates the session
  → Redirect to /login
```

---

## Database Schema

Hibernate auto-creates this table on first run (`spring.jpa.hibernate.ddl-auto=update`).

```sql
CREATE TABLE users (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,   -- BCrypt hash, e.g. $2a$10$...
    role     VARCHAR(255) NOT NULL    -- e.g. ROLE_USER
);
```

---

## Setup & Running

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- MySQL 8+ running locally

### Step 1 — Create the database

```sql
CREATE DATABASE spring_security_db;
```

> Hibernate will create the `users` table automatically on first run.

### Step 2 — Configure database credentials

Open `src/main/resources/application.properties` and update:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/spring_security_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password_here
```

### Step 3 — Run

```bash
mvn spring-boot:run
```

The server starts at `http://localhost:8080`.

---

## Pages & Routes

| Method | URL | Access | Description |
|---|---|---|---|
| GET | `/register` | Public | Show registration form |
| POST | `/register` | Public | Submit registration |
| GET | `/login` | Public | Show login form |
| POST | `/login` | Public | Submit login (handled by Spring Security) |
| GET | `/home` | Protected | Welcome page after login |
| POST | `/logout` | Protected | Logout and clear session |

---

## Trying It Out

1. Open `http://localhost:8080/register` in your browser
2. Enter a username and password → click **Create Account**
3. You are redirected to the login page
4. Enter the same credentials → click **Login**
5. You land on the **Home** page showing your username
6. Click **Logout** to end the session

Try visiting `http://localhost:8080/home` while logged out — Spring Security will redirect you back to `/login` automatically.

---

## Key Concepts Explained

**BCrypt** — A one-way hashing algorithm. The same password hashed twice produces two different hashes, but `BCrypt.matches()` can verify both. This means even if your database is leaked, passwords cannot be reversed.

**CustomUserDetailsService** — Implements Spring Security's `UserDetailsService` interface. Spring calls `loadUserByUsername()` automatically during login — you just return the user from MySQL and Spring handles the password comparison.

**DaoAuthenticationProvider** — Wires your `UserDetailsService` and `PasswordEncoder` together. Spring uses it internally to authenticate login attempts.

**SecurityFilterChain** — Defines the HTTP security rules: which URLs are public, where the login page is, where to redirect after login/logout, and whether CSRF protection is active.

**Session-based authentication** — After a successful login Spring creates an `HttpSession`. The browser stores a session cookie (`JSESSIONID`) and sends it on every request. Spring looks up the session to know who the user is — no credentials need to be sent again.

---

## Common Errors

| Error | Cause | Fix |
|---|---|---|
| Redirected to `/login` on every page | Not logged in or session expired | Log in again |
| `Bad credentials` on login | Wrong username or password | Check what was registered in DB |
| `Could not create connection to database` | MySQL not running or wrong credentials | Check `application.properties` |
| `Username already taken` on register | Duplicate username in DB | Choose a different username |
| White label error page on `/` | No mapping for root URL | Go to `/login` or `/register` |
