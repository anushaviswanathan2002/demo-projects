package quiz

import (
	"math/rand"
	"time"
)

// Result holds the outcome of a single answered question.
type Result struct {
	Question      Question
	ChosenIndex   int
	Correct       bool
}

// Session manages a quiz session's state.
type Session struct {
	Questions []Question
	Results   []Result
	Current   int
}

// NewSession creates a new quiz session. If shuffle is true the question order is randomised.
func NewSession(questions []Question, shuffle bool) *Session {
	pool := make([]Question, len(questions))
	copy(pool, questions)

	if shuffle {
		r := rand.New(rand.NewSource(time.Now().UnixNano()))
		r.Shuffle(len(pool), func(i, j int) { pool[i], pool[j] = pool[j], pool[i] })
	}

	return &Session{Questions: pool}
}

// Answer records the user's answer for the current question and advances the session.
func (s *Session) Answer(chosenIndex int) Result {
	q := s.Questions[s.Current]
	correct := chosenIndex == q.CorrectIndex
	result := Result{
		Question:    q,
		ChosenIndex: chosenIndex,
		Correct:     correct,
	}
	s.Results = append(s.Results, result)
	s.Current++
	return result
}

// Done reports whether all questions have been answered.
func (s *Session) Done() bool {
	return s.Current >= len(s.Questions)
}

// Score returns the number of correct answers and total questions.
func (s *Session) Score() (correct, total int) {
	total = len(s.Results)
	for _, r := range s.Results {
		if r.Correct {
			correct++
		}
	}
	return
}

// Percentage returns the score as a percentage string label.
func (s *Session) Percentage() float64 {
	correct, total := s.Score()
	if total == 0 {
		return 0
	}
	return float64(correct) / float64(total) * 100
}
