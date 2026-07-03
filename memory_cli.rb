#!/usr/bin/env ruby
# frozen_string_literal: true

require_relative 'memory_store'

# Simple interactive CLI for the MemoryStore.
class MemoryCLI
  COMMANDS = {
    'set'    => 'set <key> <value> [tags: tag1,tag2]  — store a memory',
    'get'    => 'get <key>                             — retrieve a memory',
    'delete' => 'delete <key>                          — remove a memory',
    'list'   => 'list [tag:<tag>]                      — list all memories',
    'search' => 'search <query>                        — full-text search',
    'tags'   => 'tags                                  — show all tags',
    'clear'  => 'clear                                 — delete everything',
    'help'   => 'help                                  — show this help',
    'exit'   => 'exit                                  — quit'
  }.freeze

  def initialize(store = MemoryStore.new)
    @store = store
  end

  def run
    puts banner
    loop do
      print '> '
      input = $stdin.gets&.strip
      break if input.nil?

      args = input.split(/\s+/, 2)
      command = args.shift&.downcase
      rest = args.first.to_s

      case command
      when 'set'    then cmd_set(rest)
      when 'get'    then cmd_get(rest)
      when 'delete' then cmd_delete(rest)
      when 'list'   then cmd_list(rest)
      when 'search' then cmd_search(rest)
      when 'tags'   then cmd_tags
      when 'clear'  then cmd_clear
      when 'help'   then puts help_text
      when 'exit', 'quit', 'q' then puts 'Goodbye.'; break
      when ''       then next
      else puts "Unknown command: '#{command}'. Type 'help' for usage."
      end
    end
  end

  private

  # Parses: <key> <value> [tags: t1,t2]
  def cmd_set(rest)
    match = rest.match(/\A(\S+)\s+(.+?)(?:\s+tags:\s*(.+))?\z/i)
    unless match
      puts 'Usage: set <key> <value> [tags: tag1,tag2]'
      return
    end

    key   = match[1]
    value = match[2].strip
    tags  = match[3]&.split(',')&.map(&:strip) || []

    memory = @store.set(key, value, tags: tags)
    puts "Stored: #{format_memory(memory)}"
  end

  def cmd_get(rest)
    if rest.empty?
      puts 'Usage: get <key>'
      return
    end

    memory = @store.get(rest)
    memory ? puts(format_memory(memory)) : puts("No memory found for key: '#{rest}'")
  end

  def cmd_delete(rest)
    if rest.empty?
      puts 'Usage: delete <key>'
      return
    end

    @store.delete(rest) ? puts("Deleted: '#{rest}'") : puts("No memory found for key: '#{rest}'")
  end

  def cmd_list(rest)
    tag = rest.match(/\Atag:(\S+)\z/i)&.[](1)
    entries = @store.all(tag: tag)

    if entries.empty?
      puts tag ? "No memories tagged '#{tag}'." : 'No memories stored yet.'
      return
    end

    puts "#{entries.size} memor#{entries.size == 1 ? 'y' : 'ies'}#{tag ? " tagged '#{tag}'" : ''}:"
    entries.sort_by(&:updated_at).reverse.each { |m| puts "  #{format_memory(m)}" }
  end

  def cmd_search(query)
    if query.empty?
      puts 'Usage: search <query>'
      return
    end

    results = @store.search(query)
    if results.empty?
      puts "No memories match '#{query}'."
      return
    end

    puts "#{results.size} result#{results.size == 1 ? '' : 's'} for '#{query}':"
    results.each { |m| puts "  #{format_memory(m)}" }
  end

  def cmd_tags
    t = @store.tags
    t.empty? ? puts('No tags used yet.') : puts("Tags: #{t.join(', ')}")
  end

  def cmd_clear
    print 'Are you sure you want to clear ALL memories? (yes/no): '
    confirm = $stdin.gets&.strip&.downcase
    if confirm == 'yes'
      @store.clear
      puts 'All memories cleared.'
    else
      puts 'Aborted.'
    end
  end

  def format_memory(m)
    tag_str = m.tags.any? ? "  [#{m.tags.join(', ')}]" : ''
    "#{m.key}: #{m.value}#{tag_str}  (updated: #{m.updated_at})"
  end

  def banner
    <<~BANNER
      ╔══════════════════════════════════╗
      ║       Ruby Memory App  v1.0      ║
      ╚══════════════════════════════════╝
      Type 'help' for available commands.
    BANNER
  end

  def help_text
    COMMANDS.values.map { |line| "  #{line}" }.join("\n")
  end
end

MemoryCLI.new.run if __FILE__ == $PROGRAM_NAME
