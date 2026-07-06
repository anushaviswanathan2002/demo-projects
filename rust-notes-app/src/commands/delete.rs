use colored::Colorize;

use crate::storage::{load_notes, save_notes};

pub fn run(id: &str) -> anyhow::Result<()> {
    let mut notes = load_notes()?;

    let initial_len = notes.len();
    notes.retain(|n| n.id != id);

    if notes.len() == initial_len {
        anyhow::bail!("Note with ID '{}' not found.", id);
    }

    save_notes(&notes)?;

    println!("{} Note {} deleted successfully.", "✓".green().bold(), id.cyan().bold());
    Ok(())
}
