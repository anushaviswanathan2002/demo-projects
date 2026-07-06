package quiz

// Question represents a single quiz question with multiple choice answers.
type Question struct {
	Text          string
	Options       []string
	CorrectIndex  int
	Explanation   string
}

// Questions is the full bank of quiz questions.
var Questions = []Question{
	{
		Text: "What is the zero value of a boolean in Go?",
		Options: []string{
			"true",
			"false",
			"nil",
			"0",
		},
		CorrectIndex: 1,
		Explanation:  "In Go, the zero value for a bool is false.",
	},
	{
		Text: "Which keyword is used to declare a constant in Go?",
		Options: []string{
			"var",
			"let",
			"const",
			"final",
		},
		CorrectIndex: 2,
		Explanation:  "The 'const' keyword declares a constant value in Go.",
	},
	{
		Text: "What does the 'defer' keyword do in Go?",
		Options: []string{
			"Terminates the program immediately",
			"Delays execution of a function until the surrounding function returns",
			"Creates a new goroutine",
			"Skips the current loop iteration",
		},
		CorrectIndex: 1,
		Explanation:  "'defer' schedules a function call to run after the surrounding function returns, commonly used for cleanup.",
	},
	{
		Text: "Which built-in function is used to append elements to a slice in Go?",
		Options: []string{
			"push()",
			"add()",
			"append()",
			"insert()",
		},
		CorrectIndex: 2,
		Explanation:  "The built-in 'append()' function adds elements to the end of a slice.",
	},
	{
		Text: "How do you start a goroutine in Go?",
		Options: []string{
			"async func()",
			"thread func()",
			"go func()",
			"spawn func()",
		},
		CorrectIndex: 2,
		Explanation:  "Prepending a function call with the 'go' keyword launches it as a goroutine.",
	},
	{
		Text: "What is the correct way to create a channel in Go?",
		Options: []string{
			"channel := new(chan int)",
			"channel := make(chan int)",
			"channel := chan(int)",
			"channel := create(chan int)",
		},
		CorrectIndex: 1,
		Explanation:  "Channels must be created with the built-in 'make()' function.",
	},
	{
		Text: "Which interface in Go represents the error type?",
		Options: []string{
			"type error struct { Message string }",
			"type error interface { Error() string }",
			"type error interface { String() string }",
			"type error struct { Code int }",
		},
		CorrectIndex: 1,
		Explanation:  "The built-in error type is an interface with a single method: Error() string.",
	},
	{
		Text: "What is the output of: fmt.Println(len(\"hello\"))?",
		Options: []string{
			"4",
			"6",
			"5",
			"Compilation error",
		},
		CorrectIndex: 2,
		Explanation:  "\"hello\" has 5 characters, so len() returns 5.",
	},
	{
		Text: "In Go, which data structure uses key-value pairs?",
		Options: []string{
			"slice",
			"array",
			"map",
			"struct",
		},
		CorrectIndex: 2,
		Explanation:  "A 'map' is Go's built-in key-value data structure.",
	},
	{
		Text: "Which package provides formatted I/O in Go?",
		Options: []string{
			"io",
			"os",
			"fmt",
			"log",
		},
		CorrectIndex: 2,
		Explanation:  "The 'fmt' package implements formatted I/O functions similar to C's printf and scanf.",
	},
}
