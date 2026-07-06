mod commands;
mod models;
mod storage;

use clap::{Parser, Subcommand};

#[derive(Parser)]
#[command(
    name = "notes",
    about = "A simple CLI notes-taking application",
    version = "0.1.0"
)]
struct Cli {
    #[command(subcommand)]
    command: Command,
}

#[derive(Subcommand)]
enum Command {
    /// Add a new note
    Add {
        /// Note title
        #[arg(short, long)]
        title: String,

        /// Note body content
        #[arg(short, long)]
        body: String,

        /// Comma-separated tags (e.g. work,ideas)
        #[arg(long, value_delimiter = ',')]
        tags: Option<Vec<String>>,
    },

    /// List all notes (optionally filter by tag)
    List {
        /// Filter by tag
        #[arg(short, long)]
        tag: Option<String>,
    },

    /// View a specific note by ID
    View {
        /// Note ID
        id: String,
    },

    /// Update an existing note by ID
    Update {
        /// Note ID
        id: String,

        /// New title
        #[arg(short, long)]
        title: Option<String>,

        /// New body
        #[arg(short, long)]
        body: Option<String>,

        /// Replacement tags (comma-separated)
        #[arg(long, value_delimiter = ',')]
        tags: Option<Vec<String>>,
    },

    /// Delete a note by ID
    Delete {
        /// Note ID
        id: String,
    },

    /// Search notes by keyword
    Search {
        /// Search query
        query: String,
    },
}

fn main() {
    let cli = Cli::parse();

    let result = match &cli.command {
        Command::Add { title, body, tags } => {
            commands::add::run(title, body, tags.as_deref())
        }
        Command::List { tag } => {
            commands::list::run(tag.as_deref())
        }
        Command::View { id } => {
            commands::view::run(id)
        }
        Command::Update { id, title, body, tags } => {
            commands::update::run(id, title.as_deref(), body.as_deref(), tags.as_deref())
        }
        Command::Delete { id } => {
            commands::delete::run(id)
        }
        Command::Search { query } => {
            commands::search::run(query)
        }
    };

    if let Err(e) = result {
        eprintln!("Error: {e}");
        std::process::exit(1);
    }
}
