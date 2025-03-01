## Configuration Files

### AIConfigFactory

The `AIConfigFactory` class is responsible for providing the appropriate configuration for different AI providers. It retrieves the configuration based on the provider type.

### AIProviderConfig

The `AIProviderConfig` class holds common configuration properties for AI providers. These properties include:
- `temperature`: The temperature setting for the language model.
- `timeout`: The timeout duration for API requests.
- `maxTokens`: The maximum number of tokens for the language model.
- `frequencyPenalty`: The frequency penalty setting for the language model.
- `logRequests`: A flag to enable or disable logging of requests.
- `logResponses`: A flag to enable or disable logging of responses.

### AnthropicConfig

The `AnthropicConfig` class extends `AIProviderConfig` and includes additional properties specific to the Anthropic provider:
- `apiKey`: The API key for accessing the Anthropic service.

### GoogleConfig

The `GoogleConfig` class extends `AIProviderConfig` and includes additional properties specific to the Google provider:
- `apiKey`: The API key for accessing the Google service.
- `baseUrl`: The base URL for the Google API.

### MistralConfig

The `MistralConfig` class extends `AIProviderConfig` and includes additional properties specific to the Mistral provider:
- `apiKey`: The API key for accessing the Mistral service.
- `baseUrl`: The base URL for the Mistral API.

### OlamaConfig

The `OlamaConfig` class extends `AIProviderConfig` and includes additional properties specific to the Olama provider:
- `apiKey`: The API key for accessing the Olama service.
- `baseUrl`: The base URL for the Olama API.

### OpenAIConfig

The `OpenAIConfig` class extends `AIProviderConfig` and includes additional properties specific to the OpenAI provider:
- `apiKey`: The API key for accessing the OpenAI service.
- `organizationId`: The organization ID for the OpenAI service.

---

## Installing Ollama

Ollama is an AI model framework that can be installed and used locally. Follow these steps to install Ollama on your system:

### Linux & macOS Installation
1. Open a terminal.
2. Run the following command to install Ollama:
   ```sh
   curl -fsSL https://ollama.com/install.sh | sh
   ```
3. Once the installation completes, verify it by running:
   ```sh
   ollama --version
   ```

### Windows Installation
1. Download the installer from the official website: [Ollama Website](https://ollama.com)
2. Run the installer and follow the on-screen instructions.
3. Open a command prompt and verify the installation:
   ```sh
   ollama --version
   ```

### Running Ollama
To start using Ollama, you can run:
```sh
ollama run <model_name>
```
Replace `<model_name>` with the AI model you wish to use.

For more details and documentation, visit [Ollama's official page](https://ollama.com).

---

## Setting Up API Keys
To use the different AI providers, you need to store your API keys in environment variables. Add the following lines to your `.env` file:

```sh
# Anthropic API Key
ANTHROPIC_API_KEY=<your_api_key>

# Google API Key
GOOGLE_API_KEY=<your_api_key>
GOOGLE_BASE_URL=<your_base_url>

# Mistral API Key
MISTRAL_API_KEY=<your_api_key>
MISTRAL_BASE_URL=<your_base_url>

# Olama API Key
OLAMA_API_KEY=<your_api_key>
OLAMA_BASE_URL=<your_base_url>

# OpenAI API Key
OPENAI_API_KEY=<your_api_key>
OPENAI_ORG_ID=<your_organization_id>
```

Ensure your application loads environment variables from the `.env` file before accessing these keys.

