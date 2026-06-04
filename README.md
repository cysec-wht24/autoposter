# Autoposter 🤖

A Spring Boot bot that posts AI-generated livestream announcements to Telegram and Discord.

## Demo

[![Autoposter Demo](https://img.shields.io/badge/Watch%20Demo-YouTube-red?style=for-the-badge&logo=youtube)](https://youtu.be/8oqcWnGwauw)

## How it works

Send `/post message | link` to the Telegram bot → 3 LLM models generate announcements → a Judge LLM picks the best → posts to Telegram channel + Discord within 60 seconds.

```
/post message | link
        ↓
Gemini + GPT-OSS 120B + Nemotron generate announcements
        ↓
Judge LLM picks the best per platform
        ↓
Telegram Channel + Discord Announcements
```

## Tech Stack

- Java 21 + Spring Boot 3.5
- LangChain4j — multi-model orchestration + LLM-as-a-Judge
- Gemini 2.5 Flash Lite + GPT-OSS 120B + Nvidia Nemotron (via OpenRouter)
- MySQL 8 — message queue with per-platform status tracking
- Telegram Bot API + Discord JDA

## Setup

**Requirements:** Java 21, Maven, MySQL 8+

1. Clone the repo:
```bash
git clone https://github.com/cysec-wht24/autoposter.git
cd autoposter
```

2. Create the database:
```sql
CREATE DATABASE autoposter;
```

3. Copy `.env.example` to `.env` and fill in your values:
```env
DB_USERNAME=
DB_PASSWORD=

TELEGRAM_BOT_TOKEN=
TELEGRAM_BOT_USERNAME=
TELEGRAM_CHANNEL=
TELEGRAM_OWNER_ID=

DISCORD_BOT_TOKEN=
DISCORD_CHANNEL_ID=

GEMINI_API_KEY=
OPENROUTER_API_KEY=
```

4. Run:
```bash
./mvnw spring-boot:run
```

## Bot Commands

| Command | Description |
|---|---|
| `/start` | Show help menu |
| `/post message \| link` | Queue a livestream announcement |
| `/status` | Show pending queue count |
| `/clear` | Clear pending queue |