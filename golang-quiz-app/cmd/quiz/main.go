package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"

	"github.com/demo/golang-quiz-app/internal/quiz"
)

func main() {
	quiz.PrintBanner()

	session := quiz.NewSession(quiz.Questions, true)
	reader := bufio.NewReader(os.Stdin)

	for !session.Done() {
		questionNumber := session.Current + 1
		q := session.Questions[session.Current]

		quiz.PrintQuestion(questionNumber, len(session.Questions), q)

		chosenIndex := promptAnswer(reader, len(q.Options))

		result := session.Answer(chosenIndex)
		quiz.PrintResult(result)
	}

	quiz.PrintSummary(session)
}

// promptAnswer reads and validates the user's choice, re-prompting on invalid input.
func promptAnswer(reader *bufio.Reader, optionCount int) int {
	for {
		fmt.Print("  Your answer: ")
		input, err := reader.ReadString('\n')
		if err != nil {
			fmt.Fprintln(os.Stderr, "  Error reading input, please try again.")
			continue
		}

		input = strings.TrimSpace(input)
		num, err := strconv.Atoi(input)
		if err != nil || num < 1 || num > optionCount {
			fmt.Printf("  ⚠  Please enter a number between 1 and %d.\n", optionCount)
			continue
		}

		return num - 1 // convert to zero-based index
	}
}
