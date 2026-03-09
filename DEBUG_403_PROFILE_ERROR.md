# 403 Error Diagnosis Guide

## What We Just Did

We've added enhanced logging to help diagnose the 403 error on the profile endpoint. The backend will now print detailed information about:

1. Whether the Authorization header is being received
2. Whether the JWT token is valid
3. Whether the userId is being extracted correctly
4. Whether authentication is being set in SecurityContext

## How to Fix This

### **Step 1: Verify the Token is Being Sent**

In your frontend, log the Authorization header before sending:

```javascript
console.log("Authorization Header:", `Bearer ${token}`);
console.log("Token value:", token);
console.log("Token length:", token ? token.length : "null");
```

### **Step 2: Rebuild the Backend**

```powershell
cd C:\PROJECT - Z\zencoo-backend
mvnw.cmd clean package
mvnw.cmd spring-boot:run
```

### **Step 3: Check the Backend Logs**

Look for log messages like:

```
GET /api/profile called, userId=123
JWT token valid, extracted userId=123, setting authentication
```

OR if it's failing:

```
GET /api/profile called, userId=null
Authorization header exists but not Bearer format: ...
JWT token validation failed
```

## Common Causes of 403 Error

### **1. Token Not Being Sent**

**Log would show:**

```
No Authorization header found in request: /api/profile
```

**Fix:** Ensure your axios interceptor is adding the token:

```javascript
axiosInstance.interceptors.request.use((config) => {
  const token = secureStore.getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

### **2. Token Format Wrong**

**Log would show:**

```
Authorization header exists but not Bearer format: ...
```

**Fix:** Ensure token is prefixed with "Bearer " (with space):

```javascript
// ❌ WRONG
Authorization: token123...

// ✅ CORRECT
Authorization: Bearer token123...
```

### **3. Token Expired**

**Log would show:**

```
Expired JWT token: The Token has expired
JWT token validation failed
```

**Fix:** After 24 hours, the token expires. Users need to re-login or implement token refresh.

### **4. Token Corrupted/Invalid**

**Log would show:**

```
Invalid JWT token: Unable to read the JSON...
JWT token validation failed
```

**Fix:** Make sure the exact same token from the `/api/auth/register` response is being stored and sent. Don't modify it in any way.

### **5. Token Not From Latest Backend**

**Log would show:**

```
Invalid JWT signature: ...
JWT token validation failed
```

**Fix:** This happens if:

- Old token from before the backend was redeployed
- Solution: Clear secure storage and re-register

## Testing Steps

### **Test 1: Direct cURL Request**

```powershell
# Get a fresh token first by registering
$registerResponse = curl -X POST http://192.168.0.203:8080/api/auth/register `
  -H "Content-Type: application/json" `
  -d '{
    "email":"test@example.com",
    "username":"testuser",
    "password":"password123",
    "fullName":"Test User",
    "doorNumber":"1234",
    "community":"Main"
  }'

# Extract token from response and save it
# Then use it to call profile endpoint
curl -X GET http://192.168.0.203:8080/api/profile `
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### **Test 2: Frontend Logging**

Add this to your axios setup:

```javascript
axiosInstance.interceptors.request.use((config) => {
  const token = secureStore.getToken();
  console.log("🔐 Request to:", config.url);
  console.log("🔐 Token exists:", !!token);
  console.log("🔐 Token length:", token ? token.length : "null");
  console.log(
    "🔐 Authorization header:",
    config.headers.Authorization ? "SET" : "NOT SET"
  );

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error("❌ Error Response Status:", error.response?.status);
    console.error("❌ Error Response Data:", error.response?.data);
    console.error("❌ Error Config:", error.config);
    return Promise.reject(error);
  }
);
```

## Backend Logs to Check

After rebuilding and making a request to `/api/profile`, check for these patterns in the logs:

✅ **Success Pattern:**

```
[DEBUG] Processing request: GET /api/profile
[DEBUG] Found Bearer token
[INFO] JWT token valid, extracted userId=123, setting authentication
[DEBUG] Authentication set in SecurityContext for userId=123
[INFO] GET /api/profile called, userId=123
```

❌ **Failure Patterns:**

```
[DEBUG] No Authorization header found in request: /api/profile
[WARN] Authorization header exists but not Bearer format: ...
[WARN] JWT token validation failed
[INFO] GET /api/profile called, userId=null
```

## Quick Checklist

- [ ] Token is being returned from `/api/auth/register` response
- [ ] Token is stored in secure storage
- [ ] Token includes "Bearer " prefix in Authorization header
- [ ] Token is sent with request (check axios interceptor)
- [ ] Backend is rebuilt and restarted
- [ ] Check backend logs for specific error messages
- [ ] Token is not expired (check timestamp)
- [ ] Token hasn't been modified or corrupted

## If Still Not Working

1. **Share the complete backend log** starting from app startup through the failed request
2. **Share the exact Authorization header value** (first 100 characters is fine)
3. **Share the exact error response** from the 403 error
4. **Confirm** the token from register endpoint is the exact same one being sent
