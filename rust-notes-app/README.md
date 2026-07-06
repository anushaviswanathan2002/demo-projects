# rust-notes-app

A simple, fast CLI notes-taking application written in Rust.

Notes are stored as JSON in your local data directory:
- **Linux/macOS**: `~/.local/share/rust-notes-app/notes.json`
- **Windows**: `%LOCALAPPDATA%\rust-notes-app\notes.json`

---

## Installation

```bash
cd rust-notes-app
cargo build --release
# Optionally install globally
cargo install --path .
```

---

## Usage

```
notes <COMMAND> [OPTIONS]
```

### Commands

| Command  | Description                          |
|----------|--------------------------------------|
| `add`    | Create a new note                    |
| `list`   | List all notes (optionally by tag)   |
| `view`   | View a note's full content           |
| `update` | Edit an existing note                |
| `delete` | Remove a note                        |
| `search` | Full-text search across all notes    |

---

### Add a note

```bash
notes add --title "My first note" --body "Hello, Rust notes!" --tags rust,cli
```

### List all notes

```bash
notes list
```

### List notes filtered by tag

```bash
notes list --tag rust
```

### View a note

```bash
notes view <ID>
```

### Update a note

```bash
notes update <ID> --title "Updated title"
notes update <ID> --body "New content" --tags work,important
```

### Delete a note

```bash
notes delete <ID>
```

### Search notes

```bash
notes search "hello"
```

---

## Project Structure

```
src/
├── main.rs              # CLI entry point & argument parsing (clap)
├── models.rs            # Note data model
├── storage.rs           # JSON persistence layer
└── commands/
    ├── mod.rs
    ├── add.rs
    ├── list.rs
    ├── view.rs
    ├── update.rs
    ├── delete.rs
    └── search.rs
```

## Dependencies

| Crate        | Purpose                            |
|--------------|------------------------------------|
| `clap`       | CLI argument parsing               |
| `serde`      | Serialization/deserialization      |
| `serde_json` | JSON storage format                |
| `chrono`     | Timestamps                         |
| `uuid`       | Unique note IDs                    |
| `colored`    | Terminal color output              |
| `dirs`       | Cross-platform data directory      |
| `anyhow`     | Ergonomic error handling           |
