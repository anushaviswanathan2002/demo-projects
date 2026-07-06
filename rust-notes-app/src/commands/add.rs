use colored::Colorize;

use crate::models::Note;
use crate::storage::{load_notes, save_notes};

pub fn run(title: &str, body: &str, tags: Option<&[String]>) -> anyhow::Result<()> {
    let mut notes = load_notes()?;
    let note = Note::new(title, body, tags.unwrap_or(&[]));
    let id = note.id.clone();
    notes.push(note);
    save_notes(&notes)?;
    println!("{} Note created with ID: {}", "✓".green().bold(), id.cyan().bold());
    Ok(())
}
