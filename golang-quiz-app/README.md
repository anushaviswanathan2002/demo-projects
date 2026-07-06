# 🐹 Golang Quiz App

A colorful, interactive CLI quiz application written in Go that tests your Go language knowledge.

## Features

- 10 multiple-choice questions covering core Go concepts
- Randomised question order each run
- Instant feedback with explanations after every answer
- Final score summary with a grade and review of all answers
- Coloured terminal output via [`fatih/color`](https://github.com/fatih/color)

## Project Structure

```
golang-quiz-app/
├── cmd/
│   └── quiz/
│       └── main.go          # Entry point — I/O loop
├── internal/
│   └── quiz/
│       ├── question.go      # Question bank
│       ├── session.go       # Session state & scoring
│       └── renderer.go      # Terminal output / colours
├── go.mod
├── go.sum
└── README.md
```

## Getting Started

### Prerequisites

- [Go 1.21+](https://go.dev/dl/)

### Install & Run

```bash
# Clone the repository (or navigate into it)
cd golang-quiz-app

# Download dependencies
go mod tidy

# Run the quiz
go run ./cmd/quiz
```

### Build a Binary

```bash
go build -o quiz ./cmd/quiz
./quiz
```

## How to Play

1. The app presents one question at a time with numbered options.
2. Type the number of your chosen answer and press **Enter**.
3. Instant feedback tells you if you were right, plus a brief explanation.
4. After all 10 questions, your final score and grade are displayed.

## Grading

| Score | Grade |
|-------|-------|
| 100%  | S – Perfect |
| 80–99% | A – Excellent |
| 60–79% | B – Good |
| 40–59% | C – Keep studying |
| < 40%  | D – Review the docs |

## Dependencies

| Package | Purpose |
|---------|---------|
| [`github.com/fatih/color`](https://github.com/fatih/color) | Coloured terminal output |
