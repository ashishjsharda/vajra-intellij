# Vajra IntelliJ Plugin - Deployment Guide

## 🚀 Quick Deployment (< 1 Hour)

### Step 1: Upload to GitHub (5 minutes)

```bash
# Navigate to the project directory
cd vajra-intellij

# Initialize git (if not already done)
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit - Vajra IntelliJ Plugin v0.2.0"

# Create repo on GitHub, then:
git remote add origin https://github.com/ashishjsharda/vajra-intellij.git
git branch -M main
git push -u origin main
```

### Step 2: Build the Plugin (5 minutes)

```bash
# Make gradlew executable
chmod +x gradlew

# Build the plugin
./gradlew build

# The plugin ZIP will be in: build/distributions/vajra-intellij-0.2.0.zip
```

### Step 3: Test Locally (5 minutes)

```bash
# Run IntelliJ IDEA with the plugin installed
./gradlew runIde
```

This opens a new IntelliJ instance with Vajra installed. Test:
1. Open a project
2. Go to Settings > Tools > Vajra
3. Add an API key (or use Ollama)
4. Open View > Tool Windows > Vajra Chat
5. Try some code actions!

### Step 4: Publish to JetBrains Marketplace (15-30 minutes)

#### 4a. Create JetBrains Account
1. Go to https://plugins.jetbrains.com/
2. Sign in or create account
3. Go to https://plugins.jetbrains.com/author/me

#### 4b. Get Plugin Token
1. Click on your name > Edit Profile
2. Go to "API Tokens" section
3. Click "Generate Token"
4. Copy the token (save it - you won't see it again!)

#### 4c. Upload Plugin

**Option A: Manual Upload (Fastest for first time)**
1. Go to https://plugins.jetbrains.com/plugin/add
2. Click "Upload plugin"
3. Select `build/distributions/vajra-intellij-0.2.0.zip`
4. Fill in details:
   - **Plugin Name**: Vajra - AI Coding Assistant
   - **Description**: Copy from plugin.xml
   - **Category**: Code tools
   - **Tags**: ai, coding-assistant, gpt, claude, productivity
   - **License**: MIT
5. Click "Upload"
6. Wait for review (usually 1-3 hours)

**Option B: Automated Publishing (For updates)**
```bash
# Add your token to environment
export PUBLISH_TOKEN="your-jetbrains-marketplace-token"

# Publish
./gradlew publishPlugin
```

#### 4d. Add to GitHub Secrets (For CI/CD)
1. Go to GitHub repo > Settings > Secrets and variables > Actions
2. Click "New repository secret"
3. Name: `JETBRAINS_MARKETPLACE_TOKEN`
4. Value: Your JetBrains marketplace token
5. Click "Add secret"

Now every release will auto-publish!

### Step 5: Create GitHub Release (Optional but Recommended)

```bash
# Tag the release
git tag -a v0.2.0 -m "Initial release"
git push origin v0.2.0
```

Then on GitHub:
1. Go to Releases > Create a new release
2. Choose tag: v0.2.0
3. Title: Vajra v0.2.0 - Initial Release
4. Description: Copy from CHANGELOG.md
5. Attach: `build/distributions/vajra-intellij-0.2.0.zip`
6. Publish release

## 📋 Pre-Deployment Checklist

- [ ] All files created and committed to GitHub
- [ ] Plugin builds successfully (`./gradlew build`)
- [ ] Plugin runs in test IDE (`./gradlew runIde`)
- [ ] At least one provider tested (OpenAI, Claude, Qwen, or Ollama)
- [ ] README.md has correct links
- [ ] LICENSE file present
- [ ] JetBrains account created
- [ ] Marketplace token generated

## 🎯 Post-Deployment

### Update VSCode Extension README
Add this to your Vajra VSCode repo README:

```markdown
## Other IDEs

Using IntelliJ IDEA? Check out [Vajra for IntelliJ](https://github.com/ashishjsharda/vajra-intellij)

- [IntelliJ Plugin](https://plugins.jetbrains.com/plugin/XXXXX-vajra)
- [GitHub Repo](https://github.com/ashishjsharda/vajra-intellij)
```

### Marketing Checklist
- [ ] Post on LinkedIn about the launch
- [ ] Tweet about it
- [ ] Post in relevant subreddits (r/IntelliJIDEA, r/programming, r/artificial)
- [ ] Add to your Medium articles
- [ ] Update your resume/portfolio

## 🔧 Troubleshooting

### Build Fails
```bash
# Clean and rebuild
./gradlew clean build
```

### Gradle Not Found
```bash
# Install Gradle
brew install gradle  # macOS
# or
sudo apt-get install gradle  # Linux
# or download from https://gradle.org/install/
```

### Plugin Doesn't Load in Test IDE
- Check `plugin.xml` for errors
- Ensure `since-build` and `until-build` are correct
- Check IntelliJ IDEA logs

### JetBrains Marketplace Rejection
Common reasons:
- Missing description
- Icon too large/small
- Licensing issues
- Security concerns

Fix and re-upload!

## 📞 Need Help?

- **Documentation**: https://plugins.jetbrains.com/docs/intellij/
- **Discord**: https://discord.gg/vajra-ai
- **Email**: ashish@vajra-ai.com

## 🎉 Congratulations!

Once published, your plugin will be available to millions of IntelliJ IDEA users!

Share your plugin:
- Plugin page: https://plugins.jetbrains.com/plugin/XXXXX-vajra
- GitHub: https://github.com/ashishjsharda/vajra-intellij
- Documentation: https://vajra-ai.com/docs

---

Built with ❤️ by Ashish Sharda
