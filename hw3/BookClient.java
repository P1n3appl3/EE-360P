import java.io.*;
import java.util.*;
import java.util.Scanner;
public class BookClient {
    public static void main(String[] args) {
        String hostAddress;
        final int tcpPort = 7000;
        final int udpPort = 8000;
        final int clientId;

        if (args.length != 2) {
            System.out.println(
                "ERROR: Provide 2 arguments: commandFile, clientId");
            System.out.println(
                "\t(1) <command-file>: file with commands to the server");
            System.out.println("\t(2) client id: an integer between 1..9");
            System.exit(-1);
        }

        String commandFile = args[0];
        clientId = Integer.parseInt(args[1]);
        hostAddress = "localhost";

        try {
            Scanner sc = new Scanner(new FileReader(commandFile));

            while (sc.hasNextLine()) {
                String cmd = sc.nextLine();
                String[] tokens = cmd.split(" ");

                if (tokens[0].equals("setmode")) {
                    char mode = tokens[1].charAt(0);
                    System.out.println("SetMode: " + mode);
                    // TODO
                } else if (tokens[0].equals("borrow")) {
                    String student = tokens[1];
                    String book = cmd.substring(cmd.indexOf('"') - 1);
                    System.out.println("Borrow: Student=" + student +
                                       " Book=" + book);
                    // TODO
                } else if (tokens[0].equals("return")) {
                    int id = Integer.parseInt(tokens[1]);
                    System.out.println("Return: " + id);
                    // TODO
                } else if (tokens[0].equals("inventory")) {
                    System.out.println("Inventory");
                    // TODO
                } else if (tokens[0].equals("list")) {
                    String student = tokens[1];
                    System.out.println("List: " + student);
                    // TODO
                } else if (tokens[0].equals("exit")) {
                    // TODO
                    break;
                } else {
                    System.out.println("ERROR: No such command");
                }
            }
        } catch (FileNotFoundException e) { e.printStackTrace(); }
    }
}
