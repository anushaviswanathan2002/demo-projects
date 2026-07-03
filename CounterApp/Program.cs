using CounterApp;

var counter = new Counter();
bool running = true;

Console.WriteLine("=== .NET Counter App ===");
Console.WriteLine("Commands: [+] Increment  [-] Decrement  [r] Reset  [q] Quit");
Console.WriteLine();

while (running)
{
    Console.WriteLine($"Current Count: {counter.Value}");
    Console.Write("Enter command: ");

    string? input = Console.ReadLine()?.Trim().ToLower();

    switch (input)
    {
        case "+":
            counter.Increment();
            Console.WriteLine($"  ↑ Incremented to {counter.Value}");
            break;

        case "-":
            counter.Decrement();
            Console.WriteLine($"  ↓ Decremented to {counter.Value}");
            break;

        case "r":
            counter.Reset();
            Console.WriteLine("  ↺ Counter reset to 0");
            break;

        case "q":
            running = false;
            Console.WriteLine("Goodbye!");
            break;

        default:
            Console.WriteLine("  Unknown command. Use +, -, r, or q.");
            break;
    }

    Console.WriteLine();
}
