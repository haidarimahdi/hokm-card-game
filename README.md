# 🃏 Hokm AI: Hybrid Neuro-Symbolic Card Game Engine

A Java 23 implementation of **Hokm** (حکم)—the classic 4-player Persian trick-taking card game—featuring an autonomous **Hybrid AI Engine** powered by **Google Cloud Vertex AI (Gemini)** and playability via **Telegram Bot** or **Interactive CLI**.

---

## ✨ Key Architectural Highlights

* **Dual-Process AI Architecture (System 1 & System 2):**
    * **System 1 (`System1ReflexEngine`):** Zero-latency deterministic heuristic engine that resolves forced moves, partner-winning "let it ride" plays, and 4th-player minimum-cost trick captures without making external API calls.
    * **System 2 (`System2LlmEngine`):** Deep strategic reasoning powered by Google Gemini via Vertex AI for complex leads, finessing, and trump conservation.
* **Symbolic Memory & Threat Tracking (`AiMemoryTracker`):** An event-driven card counter that tracks all unseen cards and deduces player suit voids in real time, injecting structured tactical intelligence into the LLM prompt.
* **Hallucination-Proof Guardrails:** Combines Vertex AI Structured Outputs (`ResponseSchema`), strict domain validation (`RuleEngine`), and multi-tier deterministic fallbacks so an LLM error can never corrupt the game state.
* **Event-Driven Core (`GameEventBus`):** Complete decoupling between the core state machine (`GameMaster`, `TrickEngine`) and presentation layers (`TelegramGameObserver`, `ConsoleGameObserver`) using the Observer and Template Method patterns.

---

## 📜 How Hokm is Played

**Hokm** is a 4-player partnership trick-taking game played with a standard 52-card deck.

### 1. Teams & Objective
* **4 Players** are split into two partnerships: **Team 1** (You & Bot 2, sitting opposite) vs. **Team 2** (Bot 1 & Bot 3).
* **Card Ranking (High to Low):** `A > K > Q > J > 10 > 9 > 8 > 7 > 6 > 5 > 4 > 3 > 2`.
* **Goal:** Be the first team to win **7 tricks** to score **1 Round Point**. The first team to reach **3 Round Points** wins the match.

### 2. The Hakem & Choosing Hokm (Trump Suit)
* At the start of the match, cards are dealt one by one until a player receives an **Ace**. That player becomes the initial **Hakem** (Ruler).
* The Dealer deals an initial batch of **5 cards** to the Hakem.
* Based on these 5 cards, the Hakem declares the **Hokm** (trump suit) for the round. The remaining cards are then dealt until all players hold **13 cards**.

### 3. Playing Tricks
* The Hakem leads the very first trick by playing any card.
* Play proceeds around the table. Players **must follow the lead suit** if they hold a card of that suit.
* If a player is **void** in the lead suit, they may play any card—either discarding an off-suit card or **cutting (trumping)** with a **Hokm** card.
* **Winning a Trick:** The highest card of the **Hokm** suit on the table wins the trick. If no Hokm was played, the highest card of the **lead suit** wins.
* The winner of a trick leads the next trick.

### 4. Hakem Rotation
* If the Hakem's team wins the round (7 tricks), the same player remains Hakem for the next round.
* If the defending team wins the round, the Hakem title rotates to the next player.

---

## 🚀 Prerequisites & Setup

### Requirements
* **Java 23** (JDK 23)
* **Apache Maven 3.9+**
* **Google Cloud Platform (GCP) Project** with the **Vertex AI API** enabled
* **Google Cloud CLI (`gcloud`)** installed for Application Default Credentials (ADC)
* *(Optional)* A **Telegram Bot Token** from [@BotFather](https://t.me/BotFather) for Telegram Mode

### 1. Authenticate with Google Cloud
Authenticate your local environment so Vertex AI can verify your credentials:
```bash
gcloud auth application-default login
```

### 2. Configure Environment Variables
Export your GCP and Telegram configuration in your terminal:

```bash
export GCP_PROJECT_ID="your-gcp-project-id"
export GCP_LOCATION="europe-west3"
export GEMINI_MODEL_NAME="gemini-2.5-flash"

# Required for Telegram Mode:
export TELEGRAM_BOT_TOKEN="your-telegram-bot-token"
export TELEGRAM_BOT_USERNAME="YourHokmBot"
```

### 3. Build and Run
Compile and execute the application using Maven:
```bash
mvn clean compile exec:java -Dexec.mainClass="MainApplication"
```
(Tip: To switch between Telegram Mode and Console Mode, toggle USE_TELEGRAM in src/main/java/MainApplication.java.)

💬 Playing on Telegram
Once the server boots in Telegram mode, open a chat with your bot and use the following commands:

/start — Deal a new match against the AI agents

/restart — Abort the current match and immediately deal a fresh game

/end — Terminate the current match

During your turn, tap the interactive inline buttons to declare the Hokm or play a valid card from your hand.

🗂️ Project Structure

```
src/main/java/
├── MainApplication.java          # Entry point & pre-flight environment checks
├── ai/
│   ├── AiClient.java             # Vertex AI Gemini client with JSON ResponseSchema
│   ├── AiMemoryTracker.java      # Card counting, suit exhaustion & void detection
│   ├── CognitiveEngine.java      # Strategy interface for AI decision engines
│   ├── HokmPromptBuilder.java    # Dynamic context & Threat Board prompt builder
│   ├── System1ReflexEngine.java  # Fast, rule-based tactical heuristics
│   └── System2LlmEngine.java     # Deliberative LLM reasoning engine
├── core/
│   ├── Dealer.java               # Initial Hakem draw & phased 5-4-4 card dealing
│   ├── GameEventBus.java         # Pub/Sub event dispatcher for game observers
│   ├── GameMaster.java           # Match & round lifecycle orchestrator
│   ├── RuleEngine.java           # Move validation & trick winner evaluation
│   └── TrickEngine.java          # 4-turn trick execution loop
├── model/                        # Immutable Card record, Trick, GameState & Player abstractions
├── player/                       # AiPlayer & DummyAIPlayer implementations
└── ui/                           # Console CLI and Telegram Bot adapters
```


