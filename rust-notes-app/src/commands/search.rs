use colored::Colorize;

use crate::storage::load_notes;

pub fn run(query: &str) -> anyhow::Result<()> {
    let notes = load_notes()?;
    let query_lower = query.to_lowercase();

    let matches: Vec<_> = notes
        .iter()
        .filter(|n| {
            n.title.to_lowercase().contains(&query_lower)
                || n.body.to_lowercase().contains(&query_lower)
                || n.tags.iter().any(|t| t.to_lowercase().contains(&query_lower))
        })
        .collect();

    if matches.is_empty() {
        println!("{}", format!("No notes matching '{query}'.").yellow());
        return Ok(());
    }

    println!(
        "{} {} for \"{}\"",
        "Search results".bold().underline(),
        format!("({} found)", matches.len()).dimmed(),
        query.cyan()
    );
    println!();

    for note in matches {
        let tags_display = if note.tags.is_empty() {
            String::new()
        } else {
            format!(" [{}]", note.tags.join(", ")).dimmed().to_string()
        };

        // Highlight the match in the body excerpt
        let excerpt = build_excerpt(&note.body, &query_lower, 80);

        println!(
            "  {} {} {}{}",
            note.id.cyan().bold(),
            "·".dimmed(),
            note.title.bold(),
            tags_display
        );
        println!("       {}", excerpt.dimmed());
    }

    Ok(())
}

/// Returns a short excerpt from `text` centred around the first occurrence of `keyword`.
fn build_excerpt(text: &str, keyword: &str, max_len: usize) -> String {
    let lower = text.to_lowercase();
    let Some(pos) = lower.find(keyword) else {
        let end = text.len().min(max_len);
        return format!("{}…", &text[..end]);
    };

    let start = pos.saturating_sub(20);
    let end = (pos + keyword.len() + 60).min(text.len());
    let snippet = &text[start..end];
    let prefix = if start > 0 { "…" } else { "" };
    let suffix = if end < text.len() { "…" } else { "" };
    format!("{prefix}{snippet}{suffix}")
}
