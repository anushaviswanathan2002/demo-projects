# Python Todo App

A simple command-line todo application built with Python. Todos are persisted to a local `todos.json` file.

## Features

- Add todos
- List all todos or only pending ones
- Mark todos as complete
- Delete todos
- Persistent storage via JSON

## Requirements

- Python 3.10+

## Usage

```bash
python todo.py
```

### Menu Options

| Option | Action |
|--------|--------|
| 1 | List all todos |
| 2 | List pending todos only |
| 3 | Add a new todo |
| 4 | Mark a todo as complete |
| 5 | Delete a todo |
| 0 | Exit |

## Data Storage

Todos are saved to `todos.json` in the same directory. Each todo has the following structure:

```json
{
  "id": 1,
  "title": "Buy groceries",
  "done": false,
  "created_at": "2024-01-01T10:00:00.000000"
}
```
