# Testing Guide: Service Dependencies with MongoDB

This guide will help you test the newly added service dependencies in your NassNews backend application.

## Prerequisites

1. **MongoDB** must be installed and running
2. **Java 17** installed
3. **Maven** installed
4. **Environment variable** `MONGO_URI` set (or use default MongoDB connection)

## Step 1: Set Up MongoDB Connection

### Option A: Using Environment Variable (Recommended)

Create a `.env` file in the `backend/backend` directory or set the environment variable:

**Windows (PowerShell):**
```powershell
$env:MONGO_URI="mongodb://localhost:27017"
```

**Windows (CMD):**
```cmd
set MONGO_URI=mongodb://localhost:27017
```

**Linux/Mac:**
```bash
export MONGO_URI=mongodb://localhost:27017
```

### Option B: Update application.properties

If you prefer, you can directly set the MongoDB URI in `application.properties`:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017
spring.data.mongodb.database=nassnews_db
```

## Step 2: Start MongoDB

Make sure MongoDB is running:

**Windows:**
```cmd
# If MongoDB is installed as a service, it should start automatically
# Or start it manually:
"C:\Program Files\MongoDB\Server\<version>\bin\mongod.exe"
```

**Linux/Mac:**
```bash
# Start MongoDB service
sudo systemctl start mongod
# OR
mongod
```

## Step 3: Prepare Test Data in MongoDB

Before testing, you need to insert some test data. You can use MongoDB Compass, MongoDB Shell, or the REST API.

### Using MongoDB Shell (mongosh)

1. Open MongoDB Shell:
```bash
mongosh
```

2. Switch to your database:
```javascript
use nassnews_db
```

3. Insert test cities (villes):
```javascript
db.villes.insertMany([
  {
    _id: "1",
    nom: "Casablanca",
    region: "Casablanca-Settat",
    coordonnees: "33.5731,-7.5898",
    population: 3500000
  },
  {
    _id: "2",
    nom: "Rabat",
    region: "Rabat-Salé-Kénitra",
    coordonnees: "34.0209,-6.8416",
    population: 580000
  },
  {
    _id: "3",
    nom: "Marrakech",
    region: "Marrakech-Safi",
    coordonnees: "31.6295,-7.9811",
    population: 930000
  }
])
```

4. Verify the cities were inserted:
```javascript
db.villes.find().pretty()
```

## Step 4: Build and Run the Application

1. Navigate to the backend directory:
```bash
cd backend/backend
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

Or if you prefer to run the JAR:
```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

The application should start on `http://localhost:8080`

## Step 5: Test the Service Dependencies

### Test 1: Verify Cities Endpoint

**Get all cities:**
```bash
curl http://localhost:8080/api/cities
```

**Get a specific city:**
```bash
curl http://localhost:8080/api/cities/1
```

**Expected Response:**
```json
{
  "id": "1",
  "nom": "Casablanca",
  "region": "Casablanca-Settat",
  "coordonnees": "33.5731,-7.5898",
  "population": 3500000
}
```

### Test 2: Create Event with Valid City (EvenementService → VilleService)

**Create an event with a valid city ID:**
```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d "{
    \"idEvenement\": 1,
    \"titre\": \"Festival de Musique\",
    \"description\": \"Grand festival de musique à Casablanca\",
    \"lieu\": \"Place Mohammed V\",
    \"dateEvenement\": \"2024-12-25\",
    \"typeEvenement\": \"Culture\",
    \"villeId\": 1
  }"
```

**Expected Response:** Status 201 Created with the event object

**Test with invalid city ID (should fail):**
```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d "{
    \"idEvenement\": 2,
    \"titre\": \"Event Test\",
    \"description\": \"Test event\",
    \"lieu\": \"Test Location\",
    \"dateEvenement\": \"2024-12-25\",
    \"typeEvenement\": \"Test\",
    \"villeId\": 999
  }"
```

**Expected Response:** Status 400 Bad Request (because city ID 999 doesn't exist)

### Test 3: Create News with Valid City (ActualiteService → VilleService)

**Create news with a valid city ID:**
```bash
curl -X POST http://localhost:8080/api/actualites \
  -H "Content-Type: application/json" \
  -d "{
    \"titre\": \"Nouvelle infrastructure à Rabat\",
    \"contenu\": \"La ville de Rabat annonce la construction d'une nouvelle infrastructure\",
    \"datePublication\": \"2024-12-20T10:00:00.000Z\",
    \"source\": \"Agence de Presse\",
    \"categorie\": \"Politique\",
    \"villeId\": \"2\"
  }"
```

**Expected Response:** Status 201 Created with the news object

**Test with invalid city ID (should fail):**
```bash
curl -X POST http://localhost:8080/api/actualites \
  -H "Content-Type: application/json" \
  -d "{
    \"titre\": \"Test News\",
    \"contenu\": \"Test content\",
    \"datePublication\": \"2024-12-20T10:00:00.000Z\",
    \"source\": \"Test Source\",
    \"categorie\": \"Test\",
    \"villeId\": \"999\"
  }"
```

**Expected Response:** Status 400 Bad Request

### Test 4: Get News by City (ActualiteService → VilleService)

**Get news by valid city ID:**
```bash
curl http://localhost:8080/api/actualites?villeId=2
```

**Expected Response:** List of news items for that city

**Get news by invalid city ID (should fail):**
```bash
curl http://localhost:8080/api/actualites?villeId=999
```

**Expected Response:** Status 400 Bad Request

### Test 5: Create User with Favorite Cities (UtilisateurService → VilleService)

**Create user with valid favorite cities:**
```bash
curl -X POST http://localhost:8080/api/utilisateurs \
  -H "Content-Type: application/json" \
  -d "{
    \"idUtilisateur\": 1,
    \"nom\": \"Ahmed Benali\",
    \"email\": \"ahmed@example.com\",
    \"motDePasse\": \"password123\",
    \"role\": \"UTILISATEUR\",
    \"villesFavorites\": [1, 2, 3]
  }"
```

**Expected Response:** Status 201 Created with the user object

**Test with invalid city ID in favorites (should fail):**
```bash
curl -X POST http://localhost:8080/api/utilisateurs \
  -H "Content-Type: application/json" \
  -d "{
    \"idUtilisateur\": 2,
    \"nom\": \"Test User\",
    \"email\": \"test@example.com\",
    \"motDePasse\": \"password123\",
    \"role\": \"UTILISATEUR\",
    \"villesFavorites\": [1, 999]
  }"
```

**Expected Response:** Status 400 Bad Request (because city ID 999 doesn't exist)

### Test 6: Update Event with City Validation

**Update an event with a new valid city:**
```bash
curl -X PUT http://localhost:8080/api/events/1 \
  -H "Content-Type: application/json" \
  -d "{
    \"titre\": \"Festival de Musique Updated\",
    \"description\": \"Updated description\",
    \"lieu\": \"New Location\",
    \"dateEvenement\": \"2024-12-26\",
    \"typeEvenement\": \"Culture\",
    \"villeId\": 2
  }"
```

**Expected Response:** Status 200 OK with updated event

## Step 6: Verify Data in MongoDB

You can verify the data was created correctly:

```javascript
// In MongoDB Shell
use nassnews_db

// Check events
db.evenements.find().pretty()

// Check news
db.actualites.find().pretty()

// Check users
db.utilisateurs.find().pretty()

// Check cities
db.villes.find().pretty()
```

## Step 7: Test Error Handling

The services now validate city IDs. Test these scenarios:

1. **Create event with non-existent city** → Should return 400 Bad Request
2. **Create news with non-existent city** → Should return 400 Bad Request
3. **Create user with non-existent favorite city** → Should return 400 Bad Request
4. **Update event with non-existent city** → Should return 400 Bad Request
5. **Get news by non-existent city** → Should return 400 Bad Request

## Troubleshooting

### Issue: Application won't start
- **Check MongoDB is running:** `mongosh` should connect
- **Check MONGO_URI:** Verify the connection string is correct
- **Check port 8080:** Make sure nothing else is using port 8080

### Issue: 400 Bad Request when city exists
- **Check city ID format:** Ville uses String IDs, but Evenement uses Integer villeId
- **Verify city exists:** Use `db.villes.find()` in MongoDB shell

### Issue: Circular dependency error
- This shouldn't happen with the current setup, but if it does, check that services are properly injected via constructor

## Summary of Changes

1. **EvenementService** now validates city IDs before creating/updating events
2. **ActualiteService** now validates city IDs before creating news and when fetching by city
3. **UtilisateurService** now validates favorite city IDs before creating/updating users
4. All services use constructor injection for VilleService dependency

## Next Steps

- Add more comprehensive error messages
- Add logging for validation failures
- Consider adding DTOs (Data Transfer Objects) for better API design
- Add unit tests for the service dependencies

