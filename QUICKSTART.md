# ⚡ Vajra IntelliJ Plugin - Quick Start

## 📦 Deploy in 3 Commands

### 1. Push to GitHub
```bash
cd vajra-intellij
git init
git add .
git commit -m "Initial commit - Vajra IntelliJ Plugin"
git remote add origin https://github.com/ashishjsharda/vajra-intellij.git
git push -u origin main
```

### 2. Build the Plugin
```bash
chmod +x gradlew
./gradlew build
```

✅ Plugin ZIP created at: `build/distributions/vajra-intellij-0.2.0.zip`

### 3. Upload to JetBrains Marketplace
1. Go to https://plugins.jetbrains.com/plugin/add
2. Click "Upload plugin"
3. Select `build/distributions/vajra-intellij-0.2.0.zip`
4. Fill in the form and submit

**DONE!** Your plugin is now under review (usually 1-3 hours).

## 🧪 Test Locally First

```bash
./gradlew runIde
```

This launches IntelliJ IDEA with your plugin installed.

## 🎯 What You Get

- ✅ Full source code ready to build
- ✅ GitHub repo structure
- ✅ CI/CD with GitHub Actions
- ✅ Support for 10+ AI providers
- ✅ Chat interface + Code actions
- ✅ Professional README and docs
- ✅ MIT License
- ✅ Marketplace-ready

## 🔑 Next Steps

1. **Get API Keys** (pick one or more):
   - OpenAI: https://platform.openai.com/api-keys
   - Anthropic: https://console.anthropic.com/
   - Qwen: https://dashscope.aliyun.com/
   - Or use Ollama (local, free): https://ollama.ai

2. **Test the Plugin**:
   - Open Settings > Tools > Vajra
   - Add your API key
   - Open Vajra Chat (View > Tool Windows > Vajra Chat)
   - Try it out!

3. **Publish**:
   - Upload to JetBrains Marketplace
   - Create GitHub release
   - Share with the world!

## 📝 Files Included

```
vajra-intellij/
├── build.gradle.kts          # Build configuration
├── src/main/
│   ├── kotlin/com/vajra/
│   │   ├── actions/          # Code actions
│   │   ├── config/           # Settings
│   │   ├── providers/        # AI providers
│   │   ├── ui/               # Chat interface
│   │   └── utils/            # Utilities
│   └── resources/
│       ├── META-INF/
│       │   └── plugin.xml    # Plugin configuration
│       └── icons/            # Plugin icon
├── .github/workflows/        # CI/CD
├── README.md                 # Main documentation
├── DEPLOYMENT.md             # Detailed deployment guide
├── CHANGELOG.md              # Version history
└── LICENSE                   # MIT License
```

## 🚀 Ready to Ship!

Everything is production-ready. Just build, test, and publish!

Questions? Check DEPLOYMENT.md for detailed instructions.

---

Happy coding! 🎉
