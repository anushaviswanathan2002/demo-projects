use chrono::{DateTime, Utc};
use serde::{Deserialize, Serialize};
use uuid::Uuid;

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Note {
    pub id: String,
    pub title: String,
    pub body: String,
    pub tags: Vec<String>,
    pub created_at: DateTime<Utc>,
    pub updated_at: DateTime<Utc>,
}

impl Note {
    pub fn new(title: &str, body: &str, tags: &[String]) -> Self {
        let now = Utc::now();
        Self {
            id: Uuid::new_v4().to_string()[..8].to_string(), // short 8-char ID
            title: title.to_string(),
            body: body.to_string(),
            tags: tags.to_vec(),
            created_at: now,
            updated_at: now,
        }
    }
}
