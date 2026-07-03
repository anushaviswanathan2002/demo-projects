require 'tmpdir'
require_relative '../memory_store'

RSpec.describe MemoryStore do
  let(:tmpfile) { File.join(Dir.tmpdir, "memories_test_#{Process.pid}.json") }
  subject(:store) { MemoryStore.new(tmpfile) }

  after { File.delete(tmpfile) if File.exist?(tmpfile) }

  describe '#set and #get' do
    it 'stores and retrieves a value by key' do
      store.set('name', 'Alice')
      expect(store.get('name').value).to eq('Alice')
    end

    it 'normalizes keys to lowercase with underscores' do
      store.set('My Key', 'value')
      expect(store.get('my_key').value).to eq('value')
    end

    it 'updates an existing key preserving created_at' do
      store.set('x', 'first')
      created = store.get('x').created_at
      store.set('x', 'second')
      expect(store.get('x').value).to eq('second')
      expect(store.get('x').created_at).to eq(created)
    end

    it 'stores tags with a memory' do
      store.set('lang', 'Ruby', tags: ['programming', 'scripting'])
      expect(store.get('lang').tags).to contain_exactly('programming', 'scripting')
    end
  end

  describe '#delete' do
    it 'removes an existing memory and returns true' do
      store.set('temp', 'data')
      expect(store.delete('temp')).to be true
      expect(store.get('temp')).to be_nil
    end

    it 'returns false when key does not exist' do
      expect(store.delete('nonexistent')).to be false
    end
  end

  describe '#all' do
    before do
      store.set('a', '1', tags: ['alpha'])
      store.set('b', '2', tags: ['beta'])
      store.set('c', '3', tags: ['alpha'])
    end

    it 'returns all memories when no tag filter' do
      expect(store.all.size).to eq(3)
    end

    it 'filters memories by tag' do
      results = store.all(tag: 'alpha')
      expect(results.map(&:key)).to contain_exactly('a', 'c')
    end
  end

  describe '#search' do
    before do
      store.set('ruby', 'A dynamic language')
      store.set('python', 'Also dynamic')
      store.set('rust', 'A systems language')
    end

    it 'finds memories matching value content' do
      results = store.search('dynamic')
      expect(results.map(&:key)).to contain_exactly('ruby', 'python')
    end

    it 'finds memories matching key content' do
      results = store.search('ru')
      expect(results.map(&:key)).to contain_exactly('ruby', 'rust')
    end

    it 'returns empty array when nothing matches' do
      expect(store.search('javascript')).to be_empty
    end
  end

  describe '#tags' do
    it 'returns all unique tags sorted' do
      store.set('a', '1', tags: ['z', 'm'])
      store.set('b', '2', tags: ['a', 'm'])
      expect(store.tags).to eq(['a', 'm', 'z'])
    end
  end

  describe '#clear' do
    it 'removes all memories' do
      store.set('a', '1')
      store.set('b', '2')
      store.clear
      expect(store.size).to eq(0)
    end
  end

  describe 'persistence' do
    it 'reloads memories from disk on re-initialization' do
      store.set('persist_key', 'persist_value', tags: ['saved'])
      reloaded = MemoryStore.new(tmpfile)
      m = reloaded.get('persist_key')
      expect(m.value).to eq('persist_value')
      expect(m.tags).to include('saved')
    end
  end
end
