use colored::Colorize;

use crate::storage::load_notes;

pub fn run(tag_filter: Option<&str>) -> anyhow::Result<()> {
    let notes = load_notes()?;

    let filtered: Vec<_> = notes
        .iter()
        .filter(|n| {
            tag_filter
                .map(|t| n.tags.iter().any(|tag| tag.eq_ignore_ascii_case(t)))
                .unwrap_or(true)
        })
        .collect();

    if filtered.is_empty() {
        println!("{}", "No notes found.".yellow());
        return Ok(());
    }

    println!(
        "{} {}",
        "Notes".bold().underline(),
        format!("({} total)", filtered.len()).dimmed()
    );
    println!();

    for note in filtered {
        let tags_display = if note.tags.is_empty() {
            String::new()
        } else {
            format!(" [{}]", note.tags.join(", ")).dimmed().to_string()
        };

        println!(
            "  {} {} {}{}",
            note.id.cyan().bold(),
            "·".dimmed(),
            note.title.bold(),
            tags_display
        );
        println!(
            "       {}",
            note.updated_at.format("%Y-%m-%d %H:%M UTC").to_string().dimmed()
        );
    }

    Ok(())
}
