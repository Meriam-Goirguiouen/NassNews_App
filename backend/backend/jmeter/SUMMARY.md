# JMeter Setup Summary - NassNews Backend

## ✅ What Has Been Set Up

### 1. Test Plans
- ✅ **nassnews-api-test-plan.jmx** - Main comprehensive test plan (FIXED: loops variable issue)
- ✅ **auth-test-plan.jmx** - Authentication and favorites focused test plan

### 2. Test Data
- ✅ **test-data/users.csv** - User test data (15 users)
- ✅ **test-data/cities.csv** - City test data (10 cities)

### 3. Scripts
- ✅ **run-tests.bat** - Main test runner (supports environment variables)
- ✅ **run-tests.sh** - Linux/Mac test runner
- ✅ **scripts/start-full-test.bat** - Full performance test (50 users, 5 min)
- ✅ **scripts/quick-test.bat** - Quick development test (10 users, 1 min)
- ✅ **scripts/analyze-results.bat** - Results analysis tool

### 4. Documentation
- ✅ **README.md** - Main documentation
- ✅ **QUICKSTART.md** - Quick start guide
- ✅ **WORKSHOP_GUIDE.md** - Comprehensive guide based on workshop best practices
- ✅ **SUMMARY.md** - This file

### 5. Configuration
- ✅ **jmeter.properties** - JMeter configuration
- ✅ **.gitignore** - Excludes test results from git

## 🔧 Fixed Issues

1. **Loops Variable Issue** ✅
   - Changed `${loops}` to `${__P(loops,1)}` in test plan
   - Now properly reads from JMeter properties

2. **JMETER_HOME Detection** ✅
   - Scripts now check environment variable first
   - Falls back to default path if not set
   - Works with your installation at `C:\JMeter\apache-jmeter-5.6.3`

## 🚀 Quick Start

### 1. Start Backend
```cmd
cd backend/backend
mvn spring-boot:run
```

### 2. Run Quick Test
```cmd
cd backend/backend/jmeter/scripts
quick-test.bat
```

### 3. View Results
Open the HTML report that opens automatically, or check:
```
backend/backend/jmeter/reports/[timestamp]/index.html
```

## 📊 Test Scenarios Available

### Quick Test (Development)
```cmd
scripts\quick-test.bat
```
- 10 users, 30s ramp-up, 5 loops
- ~1-2 minutes duration

### Full Test (Performance)
```cmd
scripts\start-full-test.bat
```
- 50 users, 60s ramp-up, 5 minutes duration
- Comprehensive endpoint coverage

### Custom Test
```cmd
run-tests.bat [threads] [rampUp] [loops] [baseUrl]
```
Example:
```cmd
run-tests.bat 20 30 10
```

## 📁 Directory Structure

```
backend/backend/jmeter/
├── nassnews-api-test-plan.jmx    # Main test plan (FIXED)
├── auth-test-plan.jmx            # Auth test plan
├── test-data/
│   ├── users.csv                 # User test data
│   └── cities.csv                # City test data
├── scripts/
│   ├── start-full-test.bat       # Full performance test
│   ├── quick-test.bat            # Quick test
│   └── analyze-results.bat       # Results analyzer
├── results/                       # JTL result files
├── reports/                       # HTML reports
├── README.md                      # Main docs
├── QUICKSTART.md                  # Quick start
├── WORKSHOP_GUIDE.md              # Workshop-based guide
└── SUMMARY.md                     # This file
```

## 🎯 Next Steps

1. **Run Your First Test:**
   ```cmd
   cd backend\backend\jmeter\scripts
   quick-test.bat
   ```

2. **Review Results:**
   - Check the HTML report
   - Look for error rates < 1%
   - Verify response times < 500ms average

3. **Run Full Test:**
   ```cmd
   start-full-test.bat
   ```

4. **Analyze Performance:**
   - Use `analyze-results.bat`
   - Review metrics in HTML reports
   - Identify bottlenecks

5. **Optimize Based on Results:**
   - See WORKSHOP_GUIDE.md for optimization tips
   - Focus on endpoints with high response times
   - Check database queries

## 📚 Documentation

- **Quick Start:** See [QUICKSTART.md](QUICKSTART.md)
- **Full Guide:** See [WORKSHOP_GUIDE.md](WORKSHOP_GUIDE.md)
- **Main Docs:** See [README.md](README.md)

## ⚙️ Configuration

### Environment Variable (Optional)
Set `JMETER_HOME` to your JMeter installation:
```cmd
set JMETER_HOME=C:\JMeter\apache-jmeter-5.6.3
```

Scripts will automatically detect this, or use the default path.

### Backend URL
Default: `http://localhost:8080`

To test a different server:
```cmd
run-tests.bat 10 10 5 http://192.168.1.100:8080
```

## 🐛 Troubleshooting

### Issue: "JMeter not found"
**Solution:** Set `JMETER_HOME` environment variable or update scripts

### Issue: "Connection refused"
**Solution:** Ensure backend is running on port 8080

### Issue: "NumberFormatException: ${loops}"
**Solution:** ✅ FIXED - Now uses `${__P(loops,1)}`

### Issue: High error rate
**Solution:** 
- Check backend logs
- Reduce number of threads
- Verify database is accessible

## ✨ Features

- ✅ Environment variable support
- ✅ Automatic HTML report generation
- ✅ Multiple test scenarios
- ✅ Test data CSV files
- ✅ Results analysis tools
- ✅ Comprehensive documentation
- ✅ Workshop-based best practices

## 📈 Metrics to Monitor

- **Response Time:** Average < 500ms
- **90th Percentile:** < 1000ms
- **Error Rate:** < 1%
- **Throughput:** > 50 req/s

---

**Ready to test!** Start with `quick-test.bat` and work your way up to full performance tests.

