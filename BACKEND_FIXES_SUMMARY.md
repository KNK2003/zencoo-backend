# Backend Authentication & Feed Endpoint Fixes

## Issues Identified & Fixed

### 1. **❌ Registration Endpoint Not Returning JWT Token**

**Problem:** After signup, the `/api/auth/register` endpoint was only returning:

```json
{ "message": "Registration successful" }
```

**Solution:** Updated to return JWT token immediately after registration:

```json
{
  "message": "Registration successful",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "userId": 123
}
```

**File Modified:** `src/main/java/com/zencoo/controller/AuthController.java`

**Frontend Action:** After signup, automatically store the returned token in secure storage and use it for subsequent requests.

---

### 2. **❌ JWT Token Validation Not Properly Logged**

**Problem:** The JWT filter was silently failing to validate tokens without logging errors, making debugging impossible.

**Solution:** Added detailed logging at DEBUG and ERROR levels:

- Logs when Authorization header is found
- Logs token extraction
- Logs successful token validation
- Logs specific JWT error reasons (expired, malformed, signature invalid, etc.)

**File Modified:**

- `src/main/java/com/zencoo/security/JwtAuthenticationFilter.java`
- `src/main/java/com/zencoo/util/JwtUtil.java`

**Backend Action:** Check backend logs for specific JWT validation errors using:

```bash
# Look for logs starting with "JWT token" or "Invalid JWT"
```

---

### 3. **Feed Endpoint Requires Authentication**

**Endpoint:** `GET /api/posts`

**Security:** This endpoint is protected and requires a valid JWT token in the Authorization header.

**Format Required:**

```
Authorization: Bearer <JWT_TOKEN>
```

**File Location:** `src/main/java/com/zencoo/controller/PostController.java` (line 28)

---

## What Frontend Should Do

### **After Signup (Registration)**

1. Parse the response from `/api/auth/register`
2. Extract the `token` field
3. Store it in `secureStore.ts` (or your secure storage)
4. Use it for all future API calls

### **For Feed & Protected Endpoints**

Always include:

```javascript
headers: {
  'Authorization': `Bearer ${token}`
}
```

### **Debug 403 Errors**

The 403 error you were seeing could be due to:

1. ❌ Token not included in Authorization header
2. ❌ Token format wrong (missing "Bearer " prefix)
3. ❌ Token expired (> 24 hours)
4. ❌ Token invalid/malformed

---

## Security Config Summary

**Unauthenticated Endpoints (No Token Required):**

- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/auth/check-email`
- `GET /api/auth/check-username`

**Authenticated Endpoints (Token Required):**

- `GET /api/posts` - Get all posts
- `POST /api/posts` - Create post
- `GET /api/profile` - Get own profile
- `PATCH /api/profile/*` - Update profile
- `GET /api/residents` - List residents
- `GET /api/friends/*` - Friend operations
- `POST /api/auth/logout` - Logout

---

## CORS Configuration

**Allowed Origins:**

- `http://192.168.0.201:*` (any port on this IP)
- `http://192.168.0.203:*` (any port on this IP)

**Allowed Methods:** GET, POST, PUT, DELETE, OPTIONS

**Allowed Headers:** All (`*`)

**Credentials:** Allowed

---

## Testing Checklist

- [ ] Sign up with new account → verify `token` in response
- [ ] Store token in secure storage
- [ ] Call `/api/posts` with token in Authorization header → should work
- [ ] Call `/api/posts` without token → should get 403
- [ ] Call with invalid token → should get 403
- [ ] Call with expired token → should get 403

---

## Rebuild & Restart

1. **Clean rebuild:**

   ```bash
   mvnw.cmd clean package
   ```

2. **Run backend:**

   ```bash
   mvnw.cmd spring-boot:run
   ```

3. **Check logs for errors** starting with "JWT" or "Configuring"

---

## Contact

If you see authentication errors, please provide:

1. Request method and endpoint (e.g., `GET /api/posts`)
2. Full Authorization header value (first 50 chars is enough)
3. Backend logs (search for "JWT" or "403")
4. HTTP status code from response
