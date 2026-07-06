package quiz

import (
	"fmt"
	"strings"

	"github.com/fatih/color"
)

var (
	titleStyle   = color.New(color.FgCyan, color.Bold)
	successStyle = color.New(color.FgGreen, color.Bold)
	errorStyle   = color.New(color.FgRed, color.Bold)
	infoStyle    = color.New(color.FgYellow)
	dimStyle     = color.New(color.FgWhite)
	optionStyle  = color.New(color.FgHiWhite)
)

const separator = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

// PrintBanner prints the quiz welcome banner.
func PrintBanner() {
	titleStyle.Println("\n" + separator)
	titleStyle.Println("         🐹  G O L A N G  Q U I Z  A P P")
	titleStyle.Println(separator)
	dimStyle.Println("  Test your Go knowledge — 10 questions, multiple choice.")
	dimStyle.Println("  Type the number of your answer and press Enter.\n")
}

// PrintQuestion prints a formatted question with its options.
func PrintQuestion(index, total int, q Question) {
	fmt.Printf("\n")
	infoStyle.Printf("  Question %d / %d\n", index, total)
	fmt.Println(separator)
	optionStyle.Printf("  %s\n\n", q.Text)

	for i, opt := range q.Options {
		fmt.Printf("    %s  %s\n",
			infoStyle.Sprintf("[%d]", i+1),
			opt,
		)
	}
	fmt.Println()
}

// PrintResult prints whether the last answer was correct and shows the explanation.
func PrintResult(r Result) {
	if r.Correct {
		successStyle.Println("  ✔  Correct!")
	} else {
		errorStyle.Printf("  ✘  Wrong!  The correct answer was: %s\n",
			r.Question.Options[r.Question.CorrectIndex])
	}
	dimStyle.Printf("  💡 %s\n", r.Question.Explanation)
}

// PrintSummary prints the final score summary.
func PrintSummary(s *Session) {
	correct, total := s.Score()
	pct := s.Percentage()

	fmt.Printf("\n%s\n", separator)
	titleStyle.Println("  🏁  QUIZ COMPLETE!")
	fmt.Println(separator)

	fmt.Printf("\n  Your score: ")
	scoreColor := successStyle
	if pct < 50 {
		scoreColor = errorStyle
	} else if pct < 80 {
		scoreColor = infoStyle
	}
	scoreColor.Printf("%d / %d  (%.0f%%)\n\n", correct, total, pct)

	printGrade(pct)

	fmt.Println("\n  Review:")
	for i, r := range s.Results {
		marker := successStyle.Sprint("✔")
		if !r.Correct {
			marker = errorStyle.Sprint("✘")
		}
		short := r.Question.Text
		if len(short) > 55 {
			short = short[:52] + "..."
		}
		fmt.Printf("    %s  Q%-2d  %s\n", marker, i+1, short)
	}
	fmt.Printf("\n%s\n\n", separator)
}

func printGrade(pct float64) {
	var grade, msg string
	switch {
	case pct == 100:
		grade = "S"
		msg = "Perfect score! You are a Go master! 🎉"
	case pct >= 80:
		grade = "A"
		msg = "Excellent work! You know Go very well."
	case pct >= 60:
		grade = "B"
		msg = "Good job! A bit more practice and you'll ace it."
	case pct >= 40:
		grade = "C"
		msg = "Keep studying — the gopher believes in you! 🐹"
	default:
		grade = "D"
		msg = "Don't give up! Review the Go docs and try again."
	}

	gradeColor := successStyle
	if pct < 40 {
		gradeColor = errorStyle
	} else if pct < 60 {
		gradeColor = infoStyle
	}

	gradeColor.Printf("  Grade: %s  —  %s\n", strings.Repeat("★", int(pct/20)), grade)
	dimStyle.Printf("  %s\n", msg)
}
