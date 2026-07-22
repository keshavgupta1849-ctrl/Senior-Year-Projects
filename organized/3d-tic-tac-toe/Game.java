public class Game {

    public static class QuitException extends Exception {
        public QuitException() {
            super("User quit the game");
        }
    }

    public static class UndoException extends Exception {
        public UndoException() {
            super("User wants to undo");
        }
    }
}