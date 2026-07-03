import typer
from rich.console import Console
from rich.table import Table
from rich import box
from tinydb import TinyDB, Query
from pydantic import BaseModel, field_validator
from datetime import datetime
from typing import Optional

app = typer.Typer(
    name="todo",
    help="A simple CLI Todo App powered by Typer, Rich, TinyDB & Pydantic.",
    add_completion=False,
)
console = Console()
db = TinyDB("todos.json")
todos_table = db.table("todos")
TodoQuery = Query()


# ---------------------------------------------------------------------------
# Pydantic model
# ---------------------------------------------------------------------------

class TodoItem(BaseModel):
    id: int
    title: str
    priority: str = "medium"
    done: bool = False
    created_at: str = ""

    @field_validator("priority")
    @classmethod
    def validate_priority(cls, v: str) -> str:
        allowed = {"low", "medium", "high"}
        if v.lower() not in allowed:
            raise ValueError(f"Priority must be one of: {', '.join(allowed)}")
        return v.lower()

    @field_validator("title")
    @classmethod
    def validate_title(cls, v: str) -> str:
        v = v.strip()
        if not v:
            raise ValueError("Title cannot be empty.")
        return v


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def _next_id() -> int:
    all_records = todos_table.all()
    return max((r["id"] for r in all_records), default=0) + 1


PRIORITY_COLOR = {"high": "red", "medium": "yellow", "low": "green"}
PRIORITY_EMOJI = {"high": "🔴", "medium": "🟡", "low": "🟢"}


def _build_table(todos: list[dict], title: str) -> Table:
    table = Table(
        title=title,
        box=box.ROUNDED,
        header_style="bold cyan",
        show_lines=True,
    )
    table.add_column("ID", style="bold", justify="center", width=4)
    table.add_column("Title", min_width=24)
    table.add_column("Priority", justify="center", width=10)
    table.add_column("Status", justify="center", width=10)
    table.add_column("Created At", justify="center", width=20)

    for t in todos:
        priority = t.get("priority", "medium")
        color = PRIORITY_COLOR.get(priority, "white")
        emoji = PRIORITY_EMOJI.get(priority, "")
        status = "[green]✓ Done[/green]" if t["done"] else "[yellow]○ Pending[/yellow]"
        table.add_row(
            str(t["id"]),
            t["title"],
            f"[{color}]{emoji} {priority.capitalize()}[/{color}]",
            status,
            t.get("created_at", "—"),
        )
    return table


# ---------------------------------------------------------------------------
# Commands
# ---------------------------------------------------------------------------

@app.command("add")
def add(
    title: str = typer.Argument(..., help="Title of the todo item."),
    priority: str = typer.Option("medium", "--priority", "-p", help="Priority: low | medium | high"),
):
    """Add a new todo item."""
    try:
        item = TodoItem(
            id=_next_id(),
            title=title,
            priority=priority,
            created_at=datetime.now().strftime("%Y-%m-%d %H:%M"),
        )
    except Exception as e:
        console.print(f"[red]Error:[/red] {e}")
        raise typer.Exit(code=1)

    todos_table.insert(item.model_dump())
    console.print(
        f"[green]✓ Added:[/green] [bold]#{item.id}[/bold] — {item.title} "
        f"([{PRIORITY_COLOR[item.priority]}]{item.priority}[/{PRIORITY_COLOR[item.priority]}])"
    )


@app.command("list")
def list_todos(
    all_todos: bool = typer.Option(True, "--all/--pending", help="Show all or only pending todos."),
):
    """List todo items."""
    records = todos_table.all()
    if not all_todos:
        records = [r for r in records if not r["done"]]

    if not records:
        console.print("[dim]No todos found.[/dim]")
        return

    label = "All Todos" if all_todos else "Pending Todos"
    console.print(_build_table(records, label))


@app.command("done")
def complete(
    todo_id: int = typer.Argument(..., help="ID of the todo to mark as complete."),
):
    """Mark a todo as complete."""
    updated = todos_table.update({"done": True}, TodoQuery.id == todo_id)
    if not updated:
        console.print(f"[red]Todo #{todo_id} not found.[/red]")
        raise typer.Exit(code=1)
    console.print(f"[green]✓ Marked #{todo_id} as done.[/green]")


@app.command("delete")
def delete(
    todo_id: int = typer.Argument(..., help="ID of the todo to delete."),
    force: bool = typer.Option(False, "--force", "-f", help="Skip confirmation prompt."),
):
    """Delete a todo item."""
    record = todos_table.get(TodoQuery.id == todo_id)
    if not record:
        console.print(f"[red]Todo #{todo_id} not found.[/red]")
        raise typer.Exit(code=1)

    if not force:
        confirm = typer.confirm(f"Delete todo #{todo_id} — \"{record['title']}\"?")
        if not confirm:
            console.print("[dim]Aborted.[/dim]")
            raise typer.Exit()

    todos_table.remove(TodoQuery.id == todo_id)
    console.print(f"[red]✗ Deleted #{todo_id}.[/red]")


@app.command("clear")
def clear(
    force: bool = typer.Option(False, "--force", "-f", help="Skip confirmation prompt."),
):
    """Delete all completed todos."""
    if not force:
        confirm = typer.confirm("Delete all completed todos?")
        if not confirm:
            console.print("[dim]Aborted.[/dim]")
            raise typer.Exit()

    todos_table.remove(TodoQuery.done == True)  # noqa: E712
    console.print("[green]✓ Cleared all completed todos.[/green]")


if __name__ == "__main__":
    app()
