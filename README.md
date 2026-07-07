# Python Todo App

A feature-rich command-line todo application built with modern Python packages.

## Tech Stack

| Package | Purpose |
|---------|---------|
| [Typer](https://typer.tiangolo.com/) | CLI commands, arguments & options |
| [Rich](https://rich.readthedocs.io/) | Beautiful terminal output with tables & colors |
| [TinyDB](https://tinydb.readthedocs.io/) | Lightweight JSON document database |
| [Pydantic v2](https://docs.pydantic.dev/) | Data validation & models |

## Requirements

- Python 3.10+

## Installation

```bash
pip install -r requirements.txt
```

## Usage

```bash
python todo.py [COMMAND] [OPTIONS]
```

### Commands

#### Add a todo
```bash
python todo.py add "Buy groceries"
python todo.py add "Fix critical bug" --priority high
python todo.py add "Read a book" -p low
```

#### List todos
```bash
# All todos
python todo.py list

# Pending todos only
python todo.py list --pending
```

#### Mark as complete
```bash
python todo.py done 1
```

#### Delete a todo
```bash
python todo.py delete 2
python todo.py delete 2 --force   # skip confirmation
```

#### Clear completed todos
```bash
python todo.py clear
python todo.py clear --force      # skip confirmation
```

#### Help
```bash
python todo.py --help
python todo.py add --help
```

## Priority Levels

| Priority | Color |
|----------|-------|
| 🔴 High  | Red   |
| 🟡 Medium | Yellow |
| 🟢 Low   | Green |

## Data Storage

Todos are persisted in `todos.json` (TinyDB document store) in the project directory.
