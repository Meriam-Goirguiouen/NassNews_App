# JMeter Quick Start Guide

## Quick Setup (5 minutes)

### 1. Install JMeter
- **Windows**: Download from [Apache JMeter](https://jmeter.apache.org/download_jmeter.cgi) and extract
- **Linux/Mac**: 
  ```bash
  # Using Homebrew (Mac)
  brew install jmeter
  
  # Or download and extract manually
  wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.6.3.tgz
  tar -xzf apache-jmeter-5.6.3.tgz
  ```

### 2. Set JMETER_HOME (Optional but Recommended)
- **Windows**: Add environment variable `JMETER_HOME` pointing to your JMeter installation
- **Linux/Mac**: 
  ```bash
  export JMETER_HOME=/path/to/apache-jmeter-5.6.3
  ```

### 3. Start Your Backend
```bash
cd backend/backend
mvn spring-boot:run
```

### 4. Run Your First Test

#### Option A: Using GUI (Easiest)
```bash
# Windows
jmeter.bat

# Linux/Mac
jmeter
```
Then: File → Open → `nassnews-api-test-plan.jmx` → Click Run (▶)

#### Option B: Using Command Line (Fast)
```bash
# Windows
cd backend/backend/jmeter
run-tests.bat

# Linux/Mac
cd backend/backend/jmeter
./run-tests.sh
```

#### Option C: Custom Parameters
```bash
# Windows
run-tests.bat 50 30 5 http://localhost:8080
# Parameters: threads rampUp loops baseUrl

# Linux/Mac
./run-tests.sh 50 30 5 http://localhost:8080
```

## Test Plans Available

1. **nassnews-api-test-plan.jmx** - Comprehensive test covering all endpoints
2. **auth-test-plan.jmx** - Focused on authentication and favorites

## View Results

After running tests, HTML reports are generated in the `reports/` directory. Open `reports/report_YYYYMMDD_HHMMSS/index.html` in your browser.

## Common Issues

### "JMeter not found"
- Set `JMETER_HOME` environment variable
- Or edit `run-tests.bat`/`run-tests.sh` and update the `JMETER_HOME` path

### "Connection refused"
- Make sure your backend is running on `http://localhost:8080`
- Or update `baseUrl` in the test plan

### "High error rate"
- Check backend logs
- Verify database is running
- Ensure test data exists in database

## Next Steps

- Read the full [README.md](README.md) for detailed documentation
- Customize test plans for your specific needs
- Integrate with CI/CD pipelines
- Set up distributed testing for high-load scenarios

