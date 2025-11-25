# Changelog

All notable changes to the Vajra IntelliJ plugin will be documented in this file.

## [0.2.1] - 2025-11-25

### Fixed
- Fixed OpenAI API 404 error by updating to correct model names
- Changed default OpenAI model from `gpt-5` to `gpt-4o` (actual API-accessible model)
- Updated Anthropic Claude model names to proper API format (`claude-sonnet-4-20250514` instead of `claude-4-sonnet`)
- Improved error handling to show detailed API error messages instead of just status codes
- Added missing `Content-Type` headers to all API requests

### Changed
- Default provider changed to OpenAI (from Qwen) for better out-of-box experience
- Default model now uses `gpt-4o` for guaranteed API compatibility
- Updated model lists to reflect November 2025 availability

### Technical Details
- OpenAI Provider now uses: `gpt-4o`, `gpt-4o-mini`, `gpt-4-turbo`, `o1-preview`, `gpt-3.5-turbo`
- Anthropic Provider now uses: `claude-sonnet-4-20250514`, `claude-3-5-sonnet-20241022`, `claude-3-opus-20240229`
- Enhanced error messages extract and display the actual error from API responses

## [0.2.0] - 2025-01-15

### Added
- Initial release of Vajra for IntelliJ IDEA
- Support for 10+ AI providers (OpenAI, Anthropic, Qwen, DeepSeek, Mistral, Gemini, Groq, Ollama)
- Interactive chat interface with code context awareness
- Code actions: Explain, Refactor, Debug, Optimize, Add Comments, Generate Tests
- Local model support via Ollama
- Configurable API keys and provider selection
- Keyboard shortcuts for quick actions
- Multi-provider intelligent routing

### Features
- **Chat Interface**: Ask questions, get debugging help, generate code
- **Code Actions**: Right-click context menu with AI-powered actions
- **Settings Panel**: Easy configuration of API keys and preferences
- **Model Status**: View available models and their configurations
- **Enterprise-Ready**: Local deployment options, SOC 2 ready

### Supported Providers
- OpenAI (GPT-4o, GPT-4 Turbo, O1)
- Anthropic (Claude 4 Sonnet, Claude 3.5 Sonnet, Claude 3 Opus)
- Qwen (Qwen2.5-Coder models)
- Ollama (Local models)

## [Unreleased]

### Planned Features
- Inline code suggestions
- Advanced code completion
- Multi-file context awareness
- Team collaboration features
- Usage analytics and cost tracking
- Custom model fine-tuning
- Support for GPT-5 when available via API
- Enhanced local model integration