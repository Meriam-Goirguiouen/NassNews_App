# JMeter Performance Testing for NassNews Backend

This directory contains JMeter test plans for load and performance testing of the NassNews Backend API.

> 📚 **For a comprehensive guide based on performance testing workshop best practices, see [WORKSHOP_GUIDE.md](WORKSHOP_GUIDE.md)**

## Prerequisites

1. **Install JMeter**
   - Download from [Apache JMeter](https://jmeter.apache.org/download_jmeter.cgi)
   - Extract to a directory (e.g., `C:\apache-jmeter-5.6.3` on Windows)
   - Add JMeter bin directory to your PATH, or use full path to `jmeter.bat` (Windows) or `jmeter` (Linux/Mac)

2. **Start Your Backend Server**
   ```bash
   cd backend/backend
   mvn spring-boot:run
   ```
   Ensure the server is running on `http://localhost:8080` (or update the baseUrl in test plans)

## Test Plans

### 1. `nassnews-api-test-plan.jmx`
Comprehensive test plan covering all major API endpoints:
- **Cities API**: GET all cities
- **News API**: GET all news, GET news by city
- **Events API**: GET all events, GET events by city
- **User API**: Register, Login (with JWT token extraction)

## Running Tests

### Option 1: Using JMeter GUI (Recommended for Development)

1. **Open JMeter GUI**
   ```bash
   # Windows
   jmeter.bat
   
   # Linux/Mac
   jmeter
   ```

2. **Load Test Plan**
   - File → Open → Select `nassnews-api-test-plan.jmx`

3. **Configure Test Parameters**
   - In the "User Defined Variables" section, you can modify:
     - `baseUrl`: Backend server URL (default: `http://localhost:8080`)
     - `threads`: Number of concurrent users (default: 10)
     - `rampUp`: Time in seconds to ramp up all threads (default: 10)
     - `loops`: Number of iterations per thread (default: 1)

4. **Run Test**
   - Click the green "Run" button (▶) or press `Ctrl+R`
   - View results in real-time using the listeners:
     - View Results Tree: Detailed request/response
     - Summary Report: Statistics summary
     - Aggregate Report: Aggregated statistics

### Option 2: Command Line (Recommended for CI/CD)

#### Basic Command
```bash
# Windows
jmeter.bat -n -t nassnews-api-test-plan.jmx -l results.jtl -e -o reports/

# Linux/Mac
jmeter -n -t nassnews-api-test-plan.jmx -l results.jtl -e -o reports/
```

#### With Custom Parameters
```bash
# Windows
jmeter.bat -n -t nassnews-api-test-plan.jmx -l results.jtl -e -o reports/ -JbaseUrl=http://localhost:8080 -Jthreads=50 -JrampUp=30 -Jloops=5

# Linux/Mac
jmeter -n -t nassnews-api-test-plan.jmx -l results.jtl -e -o reports/ -JbaseUrl=http://localhost:8080 -Jthreads=50 -JrampUp=30 -Jloops=5
```

**Parameters:**
- `-n`: Non-GUI mode
- `-t`: Test plan file
- `-l`: Results file (JTL format)
- `-e`: Generate HTML report
- `-o`: HTML report output directory
- `-J`: Set JMeter property (e.g., `-Jthreads=50`)

## Test Scenarios

### Load Test
Test normal expected load:
```bash
jmeter -n -t nassnews-api-test-plan.jmx -l load-test-results.jtl -e -o load-test-reports/ -Jthreads=20 -JrampUp=10 -Jloops=10
```

### Stress Test
Test system limits:
```bash
jmeter -n -t nassnews-api-test-plan.jmx -l stress-test-results.jtl -e -o stress-test-reports/ -Jthreads=100 -JrampUp=30 -Jloops=5
```

### Endurance Test
Test system stability over time:
```bash
jmeter -n -t nassnews-api-test-plan.jmx -l endurance-test-results.jtl -e -o endurance-test-reports/ -Jthreads=10 -JrampUp=5 -Jloops=100
```

## Understanding Results

### Key Metrics

1. **Response Time**: Time taken to receive a response
   - Average: Mean response time
   - Median: 50th percentile
   - 90th/95th/99th Percentile: Response time for X% of requests

2. **Throughput**: Requests per second (higher is better)

3. **Error Rate**: Percentage of failed requests (should be 0% or very low)

4. **Sample Count**: Total number of requests sent

### HTML Report

After running tests with `-e -o` flags, open `reports/index.html` in a browser to view:
- Dashboard with key metrics
- Charts and graphs
- Request statistics
- Error analysis

## API Endpoints Tested

### Cities (`/api/cities`)
- `GET /api/cities` - Get all cities
- `GET /api/cities/{id}` - Get city by ID
- `GET /api/cities/nom/{nom}` - Get city by name
- `POST /api/cities` - Create city
- `POST /api/cities/detect-city` - Detect city by coordinates

### News (`/api/actualites`)
- `GET /api/actualites` - Get all news
- `GET /api/actualites?villeId={id}` - Get news by city
- `GET /api/actualites?categorie={cat}` - Get news by category
- `GET /api/actualites/{id}` - Get news by ID
- `POST /api/actualites` - Create news

### Events (`/api/events`)
- `GET /api/events` - Get all events
- `GET /api/events?villeId={id}` - Get events by city
- `GET /api/events?categorie={cat}` - Get events by category
- `GET /api/events/{id}` - Get event by ID
- `POST /api/events` - Create event
- `PUT /api/events/{id}` - Update event
- `DELETE /api/events/{id}` - Delete event

### Users (`/api/utilisateurs`)
- `GET /api/utilisateurs` - Get all users
- `GET /api/utilisateurs/{id}` - Get user by ID
- `POST /api/utilisateurs` - Create user
- `POST /api/utilisateurs/register` - Register new user
- `POST /api/utilisateurs/login` - Login (returns JWT token)
- `PUT /api/utilisateurs/{id}` - Update user
- `DELETE /api/utilisateurs/{id}` - Delete user
- `POST /api/utilisateurs/{userId}/favorites/news/{newsId}` - Add favorite news
- `GET /api/utilisateurs/{userId}/favorites/news` - Get favorite news
- `POST /api/utilisateurs/{userId}/favorites/events/{eventId}` - Add favorite event
- `GET /api/utilisateurs/{userId}/favorites/events` - Get favorite events
- `POST /api/utilisateurs/{userId}/favorites/cities/{cityId}` - Add favorite city
- `GET /api/utilisateurs/{userId}/favorites/cities` - Get favorite cities

### Admin Logs (`/api/admin/logs`)
- `GET /api/admin/logs` - Get all error logs
- `GET /api/admin/logs?level={level}` - Get logs by level
- `GET /api/admin/logs?service={service}` - Get logs by service

## Tips

1. **Start Small**: Begin with low thread counts (5-10) and gradually increase
2. **Monitor Resources**: Watch CPU, memory, and database connections during tests
3. **Check Logs**: Review backend logs for errors or warnings
4. **Database State**: Ensure your test database can handle the load
5. **Network**: Run tests from the same network or use distributed testing for remote servers

## Troubleshooting

### Connection Refused
- Ensure backend server is running
- Check if port 8080 is correct
- Verify firewall settings

### High Error Rate
- Check backend logs for errors
- Verify database connectivity
- Ensure sufficient resources (CPU, memory)

### Slow Response Times
- Check database query performance
- Monitor backend resource usage
- Consider database indexing

## Advanced: Distributed Testing

For high-load testing, you can run JMeter in distributed mode:

1. **Start JMeter Server on Remote Machines**
   ```bash
   jmeter-server.bat  # Windows
   jmeter-server      # Linux/Mac
   ```

2. **Run Test from Master**
   ```bash
   jmeter -n -t nassnews-api-test-plan.jmx -R remote1,remote2,remote3 -l results.jtl
   ```

## Integration with CI/CD

Example GitHub Actions workflow:
```yaml
name: Performance Tests
on: [push]
jobs:
  performance:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Setup JMeter
        run: |
          wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.6.3.tgz
          tar -xzf apache-jmeter-5.6.3.tgz
      - name: Run Performance Tests
        run: |
          ./apache-jmeter-5.6.3/bin/jmeter -n -t backend/backend/jmeter/nassnews-api-test-plan.jmx -l results.jtl -e -o reports/
      - name: Upload Results
        uses: actions/upload-artifact@v2
        with:
          name: jmeter-results
          path: reports/
```

## Resources

- [JMeter Official Documentation](https://jmeter.apache.org/usermanual/)
- [JMeter Best Practices](https://jmeter.apache.org/usermanual/best-practices.html)
- [JMeter Performance Testing Guide](https://jmeter.apache.org/usermanual/get-started.html)

