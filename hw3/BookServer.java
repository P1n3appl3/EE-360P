import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class BookServer {
    public static void main(String[] args) {
        final int tcpPort = 7000;
        final int udpPort = 8000;
        if (args.length != 1) {
            System.out.println(
                "ERROR: Provide 1 argument: input file containing initial inventory");
            System.exit(-1);
        }
        HashMap<String, Integer> books = new HashMap<>();

        // parse the inventory file
        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String line = reader.readLine();
            while (line != null) {
                int quote = line.lastIndexOf('"');
                books.put(line.substring(0, quote + 1),
                          Integer.parseInt(line.substring(quote + 2)));
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) { e.printStackTrace(); }

        System.out.println("Initial book count:");
        for (Entry e : books.entrySet()) { System.out.println("  " + e); }

        // TODO: handle request from clients
    }
}
