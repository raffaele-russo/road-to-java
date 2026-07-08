import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Command: turn a request into an object, so it can be queued, logged, or undone
 * instead of being an immediate direct method call.
 * Java-specific angle: a fire-and-forget command with no undo can often just be a
 * Runnable/lambda — no need for a class per action. Undo needs a class here because
 * it must capture the state to reverse (the deleted text), which a bare lambda can't
 * hold onto cleanly. Good contrast with Strategy, which swaps a whole algorithm.
 * Run:  java 11-design-patterns/Command.java
 */
public class Command {

    interface EditorCommand {
        void execute();
        void undo();
    }

    static class TextEditor {
        private final StringBuilder text = new StringBuilder();
        String contents() { return text.toString(); }
    }

    static class InsertCommand implements EditorCommand {
        private final TextEditor editor;
        private final String insertedText;

        InsertCommand(TextEditor editor, String insertedText) {
            this.editor = editor;
            this.insertedText = insertedText;
        }

        @Override
        public void execute() { editor.text.append(insertedText); }

        @Override
        public void undo() {
            editor.text.setLength(editor.text.length() - insertedText.length()); // reverse exactly what was appended
        }
    }

    // Invoker: keeps a history so any executed command can be undone later,
    // without knowing anything about what each command actually does.
    static class CommandHistory {
        private final Deque<EditorCommand> history = new ArrayDeque<>();

        void run(EditorCommand command) {
            command.execute();
            history.push(command);
        }

        void undoLast() {
            if (!history.isEmpty()) history.pop().undo();
        }
    }

    public static void main(String[] args) {
        System.out.println("== command ==");
        TextEditor editor = new TextEditor();
        CommandHistory history = new CommandHistory();

        history.run(new InsertCommand(editor, "Hello, "));
        history.run(new InsertCommand(editor, "world!"));
        System.out.println("  after edits: \"" + editor.contents() + "\"");

        history.undoLast();
        System.out.println("  after undo:  \"" + editor.contents() + "\"");
    }
}
