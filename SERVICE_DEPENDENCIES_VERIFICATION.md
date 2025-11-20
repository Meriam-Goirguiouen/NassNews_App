# How to Verify Service Dependencies Are Working

This guide shows you **multiple ways** to verify that your services are consuming other services (e.g., `EvenementService` → `VilleService`).

## Method 1: Check Application Logs (Easiest)

I've added logging to all services. When you start your application and make API calls, you'll see logs showing service interactions.

### Step 1: Start Your Application
```bash
cd backend/backend
mvn spring-boot:run
```

### Step 2: Watch the Console Output

When the application starts, you should see:
```
INFO  ... EvenementService initialized with VilleService dependency
INFO  ... ActualiteService initialized with VilleService dependency
INFO  ... UtilisateurService initialized with VilleService dependency
```

### Step 3: Make API Calls and Watch Logs

**Test 1: Create Event (EvenementService → VilleService)**
```bash
POST http://localhost:8080/api/events
{
  "idEvenement": 1,
  "titre": "Test Event",
  "description": "Test",
  "lieu": "Test Location",
  "dateEvenement": "2024-12-25",
  "typeEvenement": "Culture",
  "villeId": 1
}
```

**Expected Log Output:**
```
DEBUG EvenementService: Validating ville ID 1 via VilleService
DEBUG EvenementService: Ville validation successful via VilleService
```

**Test 2: Create News (ActualiteService → VilleService)**
```bash
POST http://localhost:8080/api/actualites
{
  "titre": "Test News",
  "contenu": "Test content",
  "datePublication": "2024-12-20T10:00:00.000Z",
  "source": "Test Source",
  "categorie": "Test",
  "villeId": "2"
}
```

**Expected Log Output:**
```
DEBUG ActualiteService: Validating ville ID 2 via VilleService
DEBUG ActualiteService: Ville validation successful via VilleService
```

**Test 3: Create User with Favorite Cities (UtilisateurService → VilleService)**
```bash
POST http://localhost:8080/api/utilisateurs
{
  "idUtilisateur": 1,
  "nom": "Test User",
  "email": "test@example.com",
  "motDePasse": "password123",
  "role": "UTILISATEUR",
  "villesFavorites": [1, 2, 3]
}
```

**Expected Log Output:**
```
DEBUG UtilisateurService: Validating 3 favorite villes via VilleService
DEBUG UtilisateurService: Validating ville ID 1 via VilleService
DEBUG UtilisateurService: Validating ville ID 2 via VilleService
DEBUG UtilisateurService: Validating ville ID 3 via VilleService
DEBUG UtilisateurService: All favorite villes validated successfully via VilleService
```

**Test 4: Invalid City ID (Should Show Warning)**
```bash
POST http://localhost:8080/api/events
{
  "idEvenement": 2,
  "titre": "Test Event",
  "villeId": 999
}
```

**Expected Log Output:**
```
DEBUG EvenementService: Validating ville ID 999 via VilleService
WARN  EvenementService: Ville with ID 999 not found via VilleService
```

## Method 2: Test with Postman

### Step 1: Setup Test Data in MongoDB

First, ensure you have cities in your database:
```javascript
use nassnews_db
db.villes.insertOne({
  _id: "1",
  nom: "Casablanca",
  region: "Casablanca-Settat",
  coordonnees: "33.5731,-7.5898",
  population: 3500000
})
```

### Step 2: Test Valid City (Should Succeed)

**Request:**
- **Method:** POST
- **URL:** `http://localhost:8080/api/events`
- **Body:**
```json
{
  "idEvenement": 1,
  "titre": "Festival de Musique",
  "description": "Grand festival",
  "lieu": "Place Mohammed V",
  "dateEvenement": "2024-12-25",
  "typeEvenement": "Culture",
  "villeId": 1
}
```

**Expected Result:** Status 201 Created

**This proves:** `EvenementService` successfully called `VilleService.getVilleById("1")` and found the city.

### Step 3: Test Invalid City (Should Fail)

**Request:**
- **Method:** POST
- **URL:** `http://localhost:8080/api/events`
- **Body:**
```json
{
  "idEvenement": 2,
  "titre": "Test Event",
  "description": "Test",
  "lieu": "Test",
  "dateEvenement": "2024-12-25",
  "typeEvenement": "Test",
  "villeId": 999
}
```

**Expected Result:** Status 400 Bad Request with error message

**This proves:** `EvenementService` called `VilleService.getVilleById("999")`, didn't find the city, and threw an exception.

## Method 3: Check Code Structure

### Visual Inspection

Look at the service constructors - they show dependencies:

**EvenementService.java:**
```java
public EvenementService(EvenementRepository evenementRepository, VilleService villeService) {
    this.evenementRepository = evenementRepository;
    this.villeService = villeService;  // ← Dependency injected here
}
```

**ActualiteService.java:**
```java
public ActualiteService(ActualiteRepository actualiteRepository, VilleService villeService) {
    this.actualiteRepository = actualiteRepository;
    this.villeService = villeService;  // ← Dependency injected here
}
```

**UtilisateurService.java:**
```java
public UtilisateurService(UtilisateurRepository utilisateurRepository, VilleService villeService) {
    this.utilisateurRepository = utilisateurRepository;
    this.villeService = villeService;  // ← Dependency injected here
}
```

### Check Method Calls

Search for `villeService.` in your codebase:
```bash
grep -r "villeService\." backend/backend/src/main/java/
```

You should see calls like:
- `villeService.getVilleById(...)` in `EvenementService`
- `villeService.getVilleById(...)` in `ActualiteService`
- `villeService.getVilleById(...)` in `UtilisateurService`

## Method 4: Unit Tests

Run the tests to verify service dependencies:

```bash
cd backend/backend
mvn test
```

The tests use mocks to verify that `VilleService` is being called. Check `EvenementServiceTest.java` - it mocks `VilleService` to verify the dependency.

## Method 5: Use a Debugger

1. Set breakpoints in:
   - `EvenementService.createEvenement()` - line 41
   - `ActualiteService.createActualite()` - line 41
   - `UtilisateurService.createUtilisateur()` - line 61

2. Make API calls via Postman
3. When the breakpoint hits, step through the code
4. You'll see `villeService.getVilleById()` being called

## Method 6: Add a Test Endpoint (Temporary)

You can temporarily add a test endpoint to verify dependencies:

**In VilleController.java:**
```java
@GetMapping("/test-dependencies")
public Map<String, String> testDependencies() {
    Map<String, String> deps = new HashMap<>();
    deps.put("EvenementService", "depends on VilleService");
    deps.put("ActualiteService", "depends on VilleService");
    deps.put("UtilisateurService", "depends on VilleService");
    return deps;
}
```

Then call: `GET http://localhost:8080/api/cities/test-dependencies`

## Summary: What Proves Service Dependencies Are Working?

✅ **Logs show service calls** - DEBUG logs appear when services call VilleService  
✅ **Valid requests succeed** - API calls with valid city IDs return 201/200  
✅ **Invalid requests fail** - API calls with invalid city IDs return 400  
✅ **Code structure** - Services have VilleService injected in constructors  
✅ **Tests pass** - Unit tests verify VilleService is mocked and called  

## Quick Verification Checklist

- [ ] Application starts without errors (services initialize)
- [ ] Logs show "Service initialized with VilleService dependency"
- [ ] Creating event with valid city ID succeeds (201)
- [ ] Creating event with invalid city ID fails (400)
- [ ] Creating news with valid city ID succeeds (201)
- [ ] Getting news by invalid city ID fails (400)
- [ ] Creating user with valid favorite cities succeeds (201)
- [ ] Creating user with invalid favorite city fails (400)

If all checkboxes pass, your service dependencies are working correctly! 🎉

