# Setup Guide - Quality Gateway CI/CD

This guide will help you set up and run the Quality Gateway CI/CD project from scratch.

## Prerequisites

### Required Software
- **Java Development Kit (JDK) 17 or higher**
  - Download: https://adoptium.net/
  - Verify: `java -version`
  
- **Apache Maven 3.8+**
  - Download: https://maven.apache.org/download.cgi
  - Verify: `mvn -version`
  
- **Git**
  - Download: https://git-scm.com/downloads
  - Verify: `git --version`

### Optional (for full CI/CD experience)
- GitHub account
- Slack workspace (for notifications)

## Local Setup

### Step 1: Clone the Repository

```bash
git clone <your-repository-url>
cd QualityGatewayCI_CD
```

### Step 2: Verify Project Structure

```bash
ls -la
# You should see:
# - pom.xml
# - src/
# - .github/
# - scripts/
# - README.md
```

### Step 3: Build the Project

```bash
mvn clean compile
```

Expected output:
```
[INFO] BUILD SUCCESS
[INFO] Total time: X.XXX s
```

### Step 4: Run Tests

#### Option A: Run Smoke Tests (Fast - ~30 seconds)
```bash
./scripts/run-smoke-tests.sh
```

Or using Maven directly:
```bash
mvn test -DtestGroups=smoke
```

#### Option B: Run Regression Tests (Full - ~1-2 minutes)
```bash
./scripts/run-regression-tests.sh
```

Or using Maven directly:
```bash
mvn test -DtestGroups=regression
```

#### Option C: Run All Tests
```bash
mvn test
```

### Step 5: Check Code Coverage

```bash
./scripts/check-coverage.sh
```

This will:
1. Run all tests with coverage tracking
2. Generate coverage report
3. Check if coverage meets 80% threshold
4. Display results in terminal

**View HTML Report:**
```bash
# macOS
open target/site/jacoco/index.html

# Linux
xdg-open target/site/jacoco/index.html

# Windows
start target/site/jacoco/index.html
```

## GitHub Actions Setup

### Step 1: Push to GitHub

```bash
git init
git add .
git commit -m "Initial commit - Quality Gateway CI/CD"
git branch -M main
git remote add origin <your-github-repo-url>
git push -u origin main
```

### Step 2: Verify Workflow

1. Go to your GitHub repository
2. Click on **Actions** tab
3. You should see the workflow running automatically
4. Click on the workflow run to see details

### Step 3: Configure Slack Notifications (Optional)

#### Create Slack Webhook

1. Go to https://api.slack.com/apps
2. Click **Create New App** → **From scratch**
3. Name: "CI/CD Notifications"
4. Select your workspace
5. Click **Incoming Webhooks** → Enable
6. Click **Add New Webhook to Workspace**
7. Select channel (e.g., #ci-cd-alerts)
8. Copy the webhook URL

#### Add to GitHub Secrets

1. Go to your GitHub repository
2. Settings → Secrets and variables → Actions
3. Click **New repository secret**
4. Name: `SLACK_WEBHOOK_URL`
5. Value: Paste your webhook URL
6. Click **Add secret**

#### Test Notification

Push a commit to trigger the pipeline:
```bash
git commit --allow-empty -m "Test notification"
git push
```

Check your Slack channel for the notification!

## Understanding the Pipeline

### Pipeline Flow

```
1. Code Push/PR
   ↓
2. Checkout Code
   ↓
3. Build Application
   ↓
4. Run Smoke Tests ← Quality Gate 1
   ↓ (if pass)
5. Run Code Coverage ← Quality Gate 2
   ↓ (if pass)
6. [Main branch only] Run Regression Tests
   ↓
7. Upload Reports
   ↓
8. Send Notifications
```

### Quality Gates Explained

#### Gate 1: Critical Test Failure
- **What**: Smoke tests must pass
- **Why**: Ensures basic functionality works
- **When**: Every PR and push
- **Impact**: Pipeline stops immediately if fails

#### Gate 2: Code Coverage Threshold
- **What**: Minimum 80% code coverage
- **Why**: Ensures adequate test coverage
- **When**: After smoke tests pass
- **Impact**: Pipeline fails if coverage < 80%

## Troubleshooting

### Issue: Tests Fail Locally

**Solution 1: Clean build**
```bash
mvn clean install
```

**Solution 2: Check Java version**
```bash
java -version
# Should be 17 or higher
```

**Solution 3: View detailed logs**
```bash
mvn test -X  # Debug mode
```

### Issue: Coverage Below Threshold

**View coverage report:**
```bash
open target/site/jacoco/index.html
```

**Focus on:**
- Red/yellow highlighted code (not covered)
- Service layer (business logic)
- Edge cases and error handling

**Add tests for uncovered code:**
```java
@Test(groups = {"regression"})
public void testNewScenario() {
    // Add test for uncovered code
}
```

### Issue: Pipeline Fails on GitHub

**Check workflow logs:**
1. Go to Actions tab
2. Click on failed workflow
3. Click on failed job
4. Expand failed step
5. Read error message

**Common issues:**
- Missing secrets (SLACK_WEBHOOK_URL)
- Syntax error in YAML
- Test failures (check test logs)
- Coverage below threshold

### Issue: Scripts Not Executable

```bash
chmod +x scripts/*.sh
```

## Running Specific Test Classes

```bash
# Run only ProductServiceTest
mvn test -Dtest=ProductServiceTest

# Run only ShoppingCartTest
mvn test -Dtest=ShoppingCartTest

# Run only OrderServiceTest
mvn test -Dtest=OrderServiceTest

# Run multiple test classes
mvn test -Dtest=ProductServiceTest,ShoppingCartTest
```

## Running Specific Test Methods

```bash
# Run single test method
mvn test -Dtest=ProductServiceTest#testGetProductById_Success

# Run multiple test methods
mvn test -Dtest=ProductServiceTest#testGetProductById_Success+testGetAllProducts_ReturnsProducts
```

## Customizing Coverage Threshold

### In pom.xml
```xml
<properties>
    <coverage.threshold>0.80</coverage.threshold>  <!-- Change to 0.70 for 70% -->
</properties>
```

### In scripts/check-coverage.sh
```bash
THRESHOLD=80  # Change to 70 for 70%
```

### In .github/workflows/ci-pipeline.yml
```yaml
env:
  COVERAGE_THRESHOLD: 80  # Change to 70 for 70%
```

## Viewing Reports

### Test Reports
```bash
# Location
target/surefire-reports/

# Files
- TEST-*.xml (JUnit XML format)
- *.txt (Text summary)
- index.html (HTML report - if configured)
```

### Coverage Reports
```bash
# Location
target/site/jacoco/

# Files
- index.html (Main report - open this)
- jacoco.xml (XML format)
- jacoco.csv (CSV format)
```

## Next Steps

1. **Explore the Code**
   - Review model classes in `src/main/java/com/ecommerce/model/`
   - Review service classes in `src/main/java/com/ecommerce/service/`
   - Review tests in `src/test/java/com/ecommerce/tests/`

2. **Add New Features**
   - Add a new product category
   - Implement discount functionality
   - Add user authentication

3. **Add New Tests**
   - Write tests for new features
   - Improve coverage for existing code
   - Add edge case tests

4. **Customize Pipeline**
   - Add deployment stage
   - Add security scanning
   - Add performance tests

## Resources

- [TestNG Documentation](https://testng.org/doc/documentation-main.html)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)

## Support

If you encounter issues:
1. Check this guide first
2. Review the main README.md
3. Check GitHub Actions logs
4. Review test output in `target/surefire-reports/`

---

**Happy Testing! 🚀**

