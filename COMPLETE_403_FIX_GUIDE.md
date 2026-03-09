# 403 Profile Error - Complete Troubleshooting Guide

## Backend Status: ✅ Running with Enhanced Logging

The backend is now running on **http://192.168.0.203:8080** with detailed logging enabled.

## What Changed

### 1. **Registration Endpoint Now Returns JWT Token**

**Endpoint:** `POST /api/auth/register`

**Response:**

```json
{
  "message": "Registration successful",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "userId": 123
}
```

### 2. **Enhanced JWT Validation Logging**

The backend now logs every step of JWT validation:

- When Authorization header is received
- When Bearer token is extracted
- When token validation succeeds/fails
- Specific error reasons (expired, malformed, signature invalid, etc.)

### 3. **Profile Endpoint Logging**

**Endpoint:** `GET /api/profile`

The endpoint now logs:

- When the request arrives
- What userId was extracted from the JWT
- Whether authentication was set

---

## How to Debug the 403 Error

### **Step 1: Frontend - Send a Request with Logging**

Add this to your frontend axios setup:

```javascript
// Before making the request
const token = secureStore.getToken();
console.log(
  "📝 Stored Token:",
  token ? token.substring(0, 50) + "..." : "NO TOKEN"
);
console.log("📝 Token Length:", token ? token.length : 0);

// Make the request with explicit Authorization
const response = await axiosInstance.get("/profile", {
  headers: {
    Authorization: `Bearer ${token}`,
  },
});
```

### **Step 2: Backend - Check the Logs**

**Expected SUCCESS logs when calling `/api/profile`:**

```
[DEBUG] Processing request: GET /api/profile
[DEBUG] Found Bearer token
[INFO] JWT token valid, extracted userId=123, setting authentication
[DEBUG] Authentication set in SecurityContext for userId=123
[INFO] GET /api/profile called, userId=123
[HTTP 200] Returns user profile data
```

**Expected FAILURE logs when NO token or invalid token:**

```
[DEBUG] Processing request: GET /api/profile
[DEBUG] No Authorization header found in request: /api/profile
[INFO] GET /api/profile called, userId=null
[WARN] userId is null - authentication not set
[HTTP 401] Returns "Unauthorized"
```

**Expected FAILURE logs when INVALID token:**

```
[DEBUG] Processing request: GET /api/profile
[DEBUG] Found Bearer token
[WARN] JWT token validation failed
[INFO] GET /api/profile called, userId=null
[WARN] userId is null - authentication not set
[HTTP 401] Returns "Unauthorized"
```

---

## Common Issues & Fixes

### **Issue 1: Getting 403 instead of 401**

403 = Authorization failed (token doesn't have right permissions)
401 = Authentication failed (no valid token)

If you're getting 403 with `userId=null`, it means:

- The token WAS recognized by Spring Security
- But authorization filter rejected the request

**Try this:**

1. Clear all browser storage
2. Clear app's secure storage
3. Re-register with a new account
4. Check that the token returned from `/api/auth/register` is saved correctly

---

### **Issue 2: Token Looks Correct But Still 403**

1. **Check token length** - Should be ~300-500 characters (JWT format)
2. **Check for extra spaces** - `Bearer TOKEN` not `BearerTOKEN`
3. **Check it's the EXACT token** from the register response - don't modify it

**Frontend test:**

```javascript
// Register
const registerRes = await axios.post(
  "http://192.168.0.203:8080/api/auth/register",
  { ...userData }
);

const receivedToken = registerRes.data.token;
console.log("Token from register:", receivedToken.substring(0, 50) + "...");

// Save it
await secureStore.setToken(receivedToken);

// Retrieve it
const savedToken = await secureStore.getToken();
console.log("Token from storage:", savedToken.substring(0, 50) + "...");

// Compare
console.log("Are they the same?", receivedToken === savedToken);
```

---

### **Issue 3: 403 on POST /api/posts but 200 on GET /api/profile**

This would indicate:

- Token is valid (profile works)
- But something is different about the posts endpoint

Check if `/api/posts` requires additional permissions or has different security rules.

---

## Complete Request Flow

```
Frontend: POST /api/auth/register
          ↓ [Response includes token]
Frontend: Save token to secure storage
          ↓
Frontend: GET /api/profile with Authorization: Bearer {token}
          ↓
Backend JwtFilter: Extract token from header
          ↓
Backend JwtFilter: Validate JWT signature and expiration
          ↓
Backend JwtFilter: Extract userId from token
          ↓
Backend JwtFilter: Create CustomUserDetails with userId
          ↓
Backend JwtFilter: Set authentication in SecurityContext
          ↓
Backend ProfileController: Extract userId from @AuthenticationPrincipal
          ↓
Backend ProfileController: Query database for user profile
          ↓
Response: 200 OK with user profile data
```

---

## Testing Checklist

- [ ] Backend is running on port 8080
- [ ] Token is returned from `/api/auth/register`
- [ ] Token is saved in secure storage without modification
- [ ] Token is sent in Authorization header with "Bearer " prefix
- [ ] Token is not expired (created within last 24 hours)
- [ ] Check backend logs for "JWT token valid"
- [ ] Check backend logs for "userId=123" (not null)
- [ ] Make request to `/api/profile` and verify it returns 200

---

## Quick Test with cURL

If you want to test directly without frontend:

```powershell
# 1. Register a user
$registerData = @{
    email = "test@example.com"
    username = "testuser"
    password = "test123"
    fullName = "Test User"
    doorNumber = "1234"
    community = "Main"
} | ConvertTo-Json

$registerResponse = Invoke-WebRequest `
    -Uri "http://192.168.0.203:8080/api/auth/register" `
    -Method POST `
    -ContentType "application/json" `
    -Body $registerData

$token = ($registerResponse.Content | ConvertFrom-Json).token

Write-Host "Token received: $($token.substring(0, 50))..."

# 2. Use the token to get profile
$profileResponse = Invoke-WebRequest `
    -Uri "http://192.168.0.203:8080/api/profile" `
    -Method GET `
    -Headers @{
        "Authorization" = "Bearer $token"
    }

Write-Host "Profile response:"
$profileResponse.Content | ConvertFrom-Json | Format-Table
```

---

## If Still Not Working

Please provide:

1. **Backend logs** (from startup through the failed request):

   - Show lines with `[DEBUG]`, `[INFO]`, `[WARN]` about JWT or profile

2. **Frontend logs** showing:

   - Token value (first 50 chars)
   - Authorization header value
   - Response status and error

3. **Exact error message** from:

   - Browser console
   - Frontend error handler
   - Response body

4. **Confirmation** that:
   - Backend is running (can you access `http://192.168.0.203:8080/api/auth/check-email?email=test@test.com`?)
   - Token from register endpoint is being saved
   - Token is less than 24 hours old
