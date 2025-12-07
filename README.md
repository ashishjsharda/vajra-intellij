# Vajra - AI Coding Assistant for IntelliJ IDEA

> Enterprise-grade multi-provider AI coding assistant supporting GPT-5, Claude 4, Qwen3-Coder, and 10+ cutting-edge AI models

[![JetBrains Plugin](https://img.shields.io/badge/JetBrains-Plugin-blue.svg)](https://plugins.jetbrains.com/plugin/XXXXX-vajra)
[![GitHub Stars](https://img.shields.io/github/stars/ashishjsharda/vajra-intellij)](https://github.com/ashishjsharda/vajra-intellij)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 🚀 Features

- **10+ AI Providers**: OpenAI GPT-5, Claude 4, Qwen3-Coder, DeepSeek-R1, Mistral, Gemini, and more
- **Local Models**: Full Ollama support for privacy-first development
- **Smart Code Actions**: Explain, Refactor, Debug, Optimize, Generate Tests
- **Interactive Chat**: Context-aware conversations with your codebase
- **Intelligent Routing**: Automatically selects the best model for each task
- **Enterprise Security**: Local deployment, audit trails, SOC 2 ready

## 📦 Installation

### From JetBrains Marketplace
1. Open IntelliJ IDEA
2. Go to `File > Settings > Plugins`
3. Search for "Vajra"
4. Click Install

### Manual Installation
1. Download the latest release from [GitHub Releases](https://github.com/ashishjsharda/vajra-intellij/releases)
2. Go to `File > Settings > Plugins`
3. Click ⚙️ > Install Plugin from Disk
4. Select the downloaded `.zip` file

## ⚙️ Quick Start

### 1. Configure API Keys
1. Open `File > Settings > Tools > Vajra`
2. Add API keys for your desired providers:
   - **OpenAI**: [Get API Key](https://platform.openai.com/api-keys)
   - **Anthropic**: [Get API Key](https://console.anthropic.com/)
   - **Qwen**: [Get API Key](https://dashscope.aliyun.com/)
   - **DeepSeek**: [Get API Key](https://platform.deepseek.com/)

### 2. Or Use Local Models (Free!)
```bash
# Install Ollama
curl -fsSL https://ollama.ai/install.sh | sh

# Pull recommended coding model
ollama pull qwen2.5-coder:7b

# Verify
ollama list
```

### 3. Start Coding with AI
- **Open Chat**: `View > Tool Windows > Vajra Chat`
- **Code Actions**: Select code > Right-click > Vajra > [Action]
- **Keyboard Shortcuts**:
  - `Ctrl+Alt+E`: Explain Code
  - `Ctrl+Alt+R`: Refactor Code
  - `Ctrl+Alt+D`: Debug Code
  - `Ctrl+Alt+T`: Generate Tests

## 🎯 Usage Examples

### Interactive Chat
Ask questions about your codebase, get debugging help, or generate code:
```
User: How do I optimize this database query?
Vajra: Here are 3 ways to optimize your query...
```

### Code Actions
1. Select any code block
2. Right-click > Vajra > [Choose action]
3. Get instant AI-powered assistance

## 📊 Model Performance

| Model | HumanEval | MBPP | Best For |
|-------|-----------|------|----------|
| Qwen2.5-Coder-7B | 85.7% | 88.4% | Coding (Most Efficient) |
| GPT-5 Codex | 91.2% | 89.7% | Overall Performance |
| Claude 4 Sonnet | 84.9% | 82.3% | Large Context |
| DeepSeek-R1 | 90.2% | 87.6% | Best Value |

## 🏢 Enterprise Features

- **Privacy-First**: Run models locally with Ollama
- **Cost Tracking**: Monitor API usage and costs
- **Team Management**: Centralized configuration
- **Audit Trails**: Complete usage logging
- **Multi-Provider**: Avoid vendor lock-in

## 🔗 Other IDEs

- **VSCode**: [Vajra for VSCode](https://github.com/ashishjsharda/Vajra)


## 🤝 Contributing

Contributions welcome! Please check our [Contributing Guide](CONTRIBUTING.md).

## 📄 License

MIT License - See [LICENSE](LICENSE) for details.

## 🆘 Support

- **Issues**: [GitHub Issues](https://github.com/ashishjsharda/vajra-intellij/issues)


---

**⭐ Star this repo if Vajra helps your development workflow!**

Built with ❤️ by [Ashish Sharda](https://github.com/ashishjsharda)
