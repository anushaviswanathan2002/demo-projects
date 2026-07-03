require 'json'
require 'time'

# MemoryStore manages a persistent key-value memory with tagging and search.
class MemoryStore
  Memory = Struct.new(:key, :value, :tags, :created_at, :updated_at, keyword_init: true)

  def initialize(storage_path = 'memories.json')
    @storage_path = storage_path
    @memories = {}
    load_from_disk
  end

  # Stores or updates a memory entry by key.
  def set(key, value, tags: [])
    key = normalize_key(key)
    now = Time.now.iso8601
    existing = @memories[key]

    @memories[key] = Memory.new(
      key: key,
      value: value,
      tags: Array(tags).map(&:to_s).uniq,
      created_at: existing ? existing.created_at : now,
      updated_at: now
    )

    save_to_disk
    @memories[key]
  end

  # Retrieves a memory entry by key. Returns nil if not found.
  def get(key)
    @memories[normalize_key(key)]
  end

  # Deletes a memory entry by key. Returns true if deleted, false if not found.
  def delete(key)
    key = normalize_key(key)
    return false unless @memories.key?(key)

    @memories.delete(key)
    save_to_disk
    true
  end

  # Returns all memory entries, optionally filtered by tag.
  def all(tag: nil)
    entries = @memories.values
    tag ? entries.select { |m| m.tags.include?(tag.to_s) } : entries
  end

  # Searches memories whose key or value contains the query string (case-insensitive).
  def search(query)
    q = query.to_s.downcase
    @memories.values.select do |m|
      m.key.downcase.include?(q) || m.value.to_s.downcase.include?(q)
    end
  end

  # Returns all unique tags across all memories.
  def tags
    @memories.values.flat_map(&:tags).uniq.sort
  end

  # Removes all memory entries.
  def clear
    @memories = {}
    save_to_disk
  end

  def size
    @memories.size
  end

  private

  def normalize_key(key)
    key.to_s.strip.downcase.gsub(/\s+/, '_')
  end

  def save_to_disk
    data = @memories.transform_values do |m|
      { key: m.key, value: m.value, tags: m.tags, created_at: m.created_at, updated_at: m.updated_at }
    end
    File.write(@storage_path, JSON.pretty_generate(data))
  rescue Errno::EACCES => e
    warn "Warning: Could not save memories to disk — #{e.message}"
  end

  def load_from_disk
    return unless File.exist?(@storage_path)

    raw = JSON.parse(File.read(@storage_path), symbolize_names: true)
    raw.each_value do |entry|
      m = Memory.new(**entry)
      @memories[m.key] = m
    end
  rescue JSON::ParserError => e
    warn "Warning: Could not parse memory file — #{e.message}. Starting fresh."
    @memories = {}
  end
end
