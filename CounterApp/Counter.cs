namespace CounterApp;

public class Counter
{
    public int Value { get; private set; }

    public void Increment() => Value++;

    public void Decrement() => Value--;

    public void Reset() => Value = 0;
}
