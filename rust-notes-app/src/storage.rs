use std::fs;
use std::path::PathBuf;

use crate::models::Note;

fn notes_file_path() -> PathBuf {
    let mut path = dirs::data_local_dir()
        .unwrap_or_else(|| PathBuf::from("."));
    path.push("rust-notes-app");
    fs::create_dir_all(&path).expect("Failed to create data directory");
    path.push("notes.json");
    path
}

pub fn load_notes() -> anyhow::Result<Vec<Note>> {
    let path = notes_file_path();
    if !path.exists() {
        return Ok(Vec::new());
    }
    let content = fs::read_to_string(&path)?;
    let notes: Vec<Note> = serde_json::from_str(&content)?;
    Ok(notes)
}

pub fn save_notes(notes: &[Note]) -> anyhow::Result<()> {
    let path = notes_file_path();
    let content = serde_json::to_string_pretty(notes)?;
    fs::write(&path, content)?;
    Ok(())
}
