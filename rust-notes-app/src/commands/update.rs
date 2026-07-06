use chrono::Utc;
use colored::Colorize;

use crate::storage::{load_notes, save_notes};

pub fn run(
    id: &str,
    title: Option<&str>,
    body: Option<&str>,
    tags: Option<&[String]>,
) -> anyhow::Result<()> {
    let mut notes = load_notes()?;

    let note = notes
        .iter_mut()
        .find(|n| n.id == id)
        .ok_or_else(|| anyhow::anyhow!("Note with ID '{}' not found.", id))?;

    if let Some(t) = title {
        note.title = t.to_string();
    }
    if let Some(b) = body {
        note.body = b.to_string();
    }
    if let Some(tg) = tags {
        note.tags = tg.to_vec();
    }
    note.updated_at = Utc::now();

    save_notes(&notes)?;

    println!("{} Note {} updated successfully.", "✓".green().bold(), id.cyan().bold());
    Ok(())
}
