import java.util.Scanner;
import java.util.Stack;
import java.io.File;

public class ListTester {

    private static Scanner scanner = new Scanner(System.in);
    private static Stack<Scanner> stack = new Stack<>();

    private static String readLine(String prompt) {
        while (!stack.isEmpty() && !scanner.hasNextLine()) {
            scanner = stack.pop();
        }
        if (stack.isEmpty()) {
            System.out.print(prompt);
            System.out.flush();
        }
        String line = "";
        if (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (!stack.isEmpty()) {
                System.out.println(prompt + line);
            }
        }
        return line;
    }

    private static String[] getItems(String[] fields) {
        String[] result = new String[fields.length - 1];
        for (int i = 1; i < fields.length; i++) {
            result[i-1] = fields[i];
        }
        return result;
    }

    private static CircularList<String> create(String[] items) {
        switch (items.length) {
            case 0:  return new CircularList<>();
            case 1:  return new CircularList<>(items[0]);
            default: return new CircularList<>(items);
        }
    }

    public static void help() {
        System.out.println("empty             Print list.isEmpty()");
        System.out.println("size              Print list.size()");
        System.out.println("find <item>       Set cursor =  list.find(item)");
        System.out.println("contains <item>   Print list.contains(item)");
        System.out.println("next              Set cursor = cursor.next()");
        System.out.println("prev              Set cursor = cursor.prev()");
        System.out.println("get               Print cursor.get())");
        System.out.println("set <item>        Call cursor.set(item))");
        System.out.println("add <item>        Call list.add(item)");
        System.out.println("after <item>      Call list.isertAfter(cursor, item)");
        System.out.println("after <x> <item>  Call list.isertAfter(list.find(x), item)");
        System.out.println("before <item>     Call list.isertBefore(cursor, item)");
        System.out.println("before <x> <item> Call list.isertBefore(list.find(x), item)");
        System.out.println("remove            Call list.remove(cursor)");
        System.out.println("remove <item>     Call list.remove(list.find(item))");
        System.out.println("clear             Call list.clear()");
        System.out.println("print             Print list.toString()");
        System.out.println("list              Print the list using Iterator");
        System.out.println("hash              Print list.hashCode()");
        System.out.println("equals <items>    Print list.equals(new CircularList(items))");
        System.out.println("new <items>       Create a new list with these items");
        System.out.println("read <filename>   Read commands from a file");
        System.out.println("help              Print this help message");
        System.out.println("quit              Exit the program");
        System.out.println("exit              Exit the program");
    }

    public static void main(String[] args) {
        CircularList<String> list = create(args);
        CircularList.Cursor cursor = null;

        String prompt = "Command: ";
        String line = readLine(prompt);
        outer: while (line.trim().length() > 0) {
            String[] fields = line.split("[ \t]+");
            String command = (fields.length > 0) ? fields[0] : null;
            String arg = (fields.length > 1) ? fields[1] : null;
            String arg2 = (fields.length > 2) ? fields[2] : null;
            int count = fields.length - 1;

            try {
                switch (command.toLowerCase()) {
                    case "empty":
                        System.out.println(list.isEmpty());
                        break;

                    case "size":
                        System.out.println(list.size());
                        break;

                    case "contains":
                        System.out.println(list.contains(arg));
                        break;

                    case "find":
                        cursor = list.find(arg);
                        break;

                    case "next":
                        cursor = cursor.next();
                        break;

                    case "prev":
                        cursor = cursor.prev();
                        break;

                    case "get":
                        System.out.println(cursor.get());
                        break;

                    case "set":
                        cursor.set(arg);
                        break;

                    case "add":
                        list.add(arg);
                        break;

                    case "remove":
                        if (count > 0) {
                            list.remove(list.find(arg));
                        } else {
                            list.remove(cursor);
                        }
                        break;

                    case "after":
                        if (count > 1) {
                            list.insertAfter(list.find(arg), arg2);
                        } else {
                            list.insertAfter(cursor, arg);
                        }
                        break;

                    case "before":
                        if (count > 1) {
                            list.insertBefore(list.find(arg), arg2);
                        } else {
                            list.insertBefore(cursor, arg);
                        }
                        break;

                    case "print":
                        System.out.println(list.toString());
                        break;

                    case "list":
                        for (String s : list) {
                            System.out.println(s);
                        }
                        break;

                    case "equals":
                        CircularList<String> other = create(getItems(fields));
                        System.out.println(list.equals(other));
                        break;

                    case "hash":
                        System.out.println(list.hashCode());
                        break;

                    case "new":
                        list = create(getItems(fields));
                        break;

                    case "clear":
                        list = new CircularList<>();
                        break;

                    case "read":
                        stack.push(scanner);
                        scanner = new Scanner(new File(arg));
                        break;

                    case "help":
                        help();
                        break;

                    case "quit":
                    case "exit":
                        break outer;

                    default:
                        System.err.println("Invalid command: " + command);
                        break;
                }

            } catch (Exception e ) {
                e.printStackTrace();
            }

            line = readLine(prompt);
        }
    }
}
