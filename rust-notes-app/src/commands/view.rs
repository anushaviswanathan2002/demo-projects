use anyhow::bail;
use colored::Colorize;

use crate::storage::load_notes;

pub fn run(id: &str) -> anyhow::Result<()> {
    let notes = load_notes()?;

    let note = notes
        .iter()
        .find(|n| n.id == id)
        .ok_or_else(|| anyhow::anyhow!("Note with ID '{}' not found.", id))?;

    println!();
    println!("  {} {}", "ID:".dimmed(), note.id.cyan().bold());
    println!("  {} {}", "Title:".dimmed(), note.title.bold());

    if !note.tags.is_empty() {
        println!("  {} {}", "Tags:".dimmed(), note.tags.join(", ").yellow());
    }

    println!(
        "  {} {}",
        "Created:".dimmed(),
        note.created_at.format("%Y-%m-%d %H:%M UTC").to_string().dimmed()
    );
    println!(
        "  {} {}",
        "Updated:".dimmed(),
        note.updated_at.format("%Y-%m-%d %H:%M UTC").to_string().dimmed()
    );
    println!();
    println!("{}", "─".repeat(50).dimmed());
    println!("{}", note.body);
    println!("{}", "─".repeat(50).dimmed());
    println!();

    Ok(())
}
