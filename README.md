# Ruby Memory App

A simple, persistent key-value memory store with a CLI interface, tagging, and full-text search — all in pure Ruby.

## Features

- **Store** memories as key-value pairs
- **Tag** memories for easy grouping
- **Search** memories by key or value content
- **Persist** memories to disk as JSON
- **Interactive CLI** for managing memories

## Project Structure

```
.
├── memory_store.rb          # Core MemoryStore library
├── memory_cli.rb            # Interactive CLI entry point
├── spec/
│   ├── spec_helper.rb
│   └── memory_store_spec.rb # RSpec test suite
├── Gemfile
└── .rspec
```

## Setup

```bash
bundle install
```

## Usage

Run the interactive CLI:

```bash
ruby memory_cli.rb
```

### Available Commands

| Command | Description |
|---|---|
| `set <key> <value> [tags: t1,t2]` | Store or update a memory |
| `get <key>` | Retrieve a memory by key |
| `delete <key>` | Remove a memory |
| `list [tag:<tag>]` | List all memories, optionally filtered by tag |
| `search <query>` | Full-text search across keys and values |
| `tags` | Show all unique tags |
| `clear` | Delete all memories |
| `help` | Show available commands |
| `exit` | Quit the app |

### Example Session

```
> set name Alice
Stored: name: Alice  (updated: 2024-01-01T10:00:00+00:00)

> set language Ruby tags: programming,scripting
Stored: language: Ruby  [programming, scripting]  (updated: ...)

> list
2 memories:
  language: Ruby  [programming, scripting]  (updated: ...)
  name: Alice  (updated: ...)

> search ruby
1 result for 'ruby':
  language: Ruby  [programming, scripting]  (updated: ...)

> list tag:programming
1 memory tagged 'programming':
  language: Ruby  [programming, scripting]  (updated: ...)

> delete name
Deleted: 'name'

> exit
Goodbye.
```

## Running Tests

```bash
bundle exec rspec
```

## Persistence

Memories are saved to `memories.json` in the current directory. Pass a custom path to `MemoryStore.new`:

```ruby
store = MemoryStore.new('/path/to/custom.json')
```
