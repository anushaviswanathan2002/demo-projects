import json
import os
from datetime import datetime

DATA_FILE = "todos.json"


def load_todos() -> list[dict]:
    if not os.path.exists(DATA_FILE):
        return []
    with open(DATA_FILE, "r") as f:
        return json.load(f)


def save_todos(todos: list[dict]) -> None:
    with open(DATA_FILE, "w") as f:
        json.dump(todos, f, indent=2)


def add_todo(title: str) -> dict:
    todos = load_todos()
    todo = {
        "id": (max((t["id"] for t in todos), default=0) + 1),
        "title": title,
        "done": False,
        "created_at": datetime.now().isoformat(),
    }
    todos.append(todo)
    save_todos(todos)
    return todo


def list_todos(show_all: bool = True) -> list[dict]:
    todos = load_todos()
    if not show_all:
        todos = [t for t in todos if not t["done"]]
    return todos


def complete_todo(todo_id: int) -> bool:
    todos = load_todos()
    for todo in todos:
        if todo["id"] == todo_id:
            todo["done"] = True
            save_todos(todos)
            return True
    return False


def delete_todo(todo_id: int) -> bool:
    todos = load_todos()
    updated = [t for t in todos if t["id"] != todo_id]
    if len(updated) == len(todos):
        return False
    save_todos(updated)
    return True


def print_todos(todos: list[dict]) -> None:
    if not todos:
        print("  No todos found.")
        return
    for todo in todos:
        status = "✓" if todo["done"] else "○"
        print(f"  [{status}] #{todo['id']} — {todo['title']}")


def print_menu() -> None:
    print("\n=== Todo App ===")
    print("  1. List all todos")
    print("  2. List pending todos")
    print("  3. Add todo")
    print("  4. Complete todo")
    print("  5. Delete todo")
    print("  0. Exit")


def main() -> None:
    while True:
        print_menu()
        choice = input("\nChoice: ").strip()

        if choice == "1":
            todos = list_todos(show_all=True)
            print()
            print_todos(todos)

        elif choice == "2":
            todos = list_todos(show_all=False)
            print()
            print_todos(todos)

        elif choice == "3":
            title = input("Todo title: ").strip()
            if not title:
                print("  Title cannot be empty.")
                continue
            todo = add_todo(title)
            print(f"  Added: #{todo['id']} — {todo['title']}")

        elif choice == "4":
            try:
                todo_id = int(input("Todo ID to complete: ").strip())
            except ValueError:
                print("  Invalid ID.")
                continue
            if complete_todo(todo_id):
                print(f"  Marked #{todo_id} as done.")
            else:
                print(f"  Todo #{todo_id} not found.")

        elif choice == "5":
            try:
                todo_id = int(input("Todo ID to delete: ").strip())
            except ValueError:
                print("  Invalid ID.")
                continue
            if delete_todo(todo_id):
                print(f"  Deleted #{todo_id}.")
            else:
                print(f"  Todo #{todo_id} not found.")

        elif choice == "0":
            print("  Goodbye!")
            break

        else:
            print("  Invalid choice. Please try again.")


if __name__ == "__main__":
    main()
