# Autoposter

A Spring Boot bot that posts AI-generated livestream announcements to Telegram and Discord.

## How it works
 
Send `/post message | link` to the Telegram bot → 3 LLM models generate announcements → a Judge LLM picks the best → posts to Telegram channel + Discord within 60 seconds.

## Requirements

- Java 21
- Maven
- MySQL 8+
- Telegram Bot Token (from @BotFather)
- LangChain4j (Gemini + GPT-OSS 120B + Nemotron via OpenRouter)

## Setup

1. Clone the repo
2. Create the database:
```sql
   CREATE DATABASE autoposter;
```
3. Copy `.env.example` to `.env` and fill in your values:

4. Run:
```bash
./mvnw spring-boot:run
```

## Bot Commands
 
| Command | Description |
|---|---|
| `/start` | Show help menu |
| `/post message \| link` | Queue an announcement |
| `/status` | Show pending queue count |
| `/clear` | Clear pending queue |