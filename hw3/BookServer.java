import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

public class BookServer {
    static LinkedHashMap<String, AtomicInteger> books;
    static HashMap<Integer, String> checkedOut;
    static HashMap<String, HashSet<Integer>> students;
    static ServerSocket tcpSocket;
    static DatagramSocket udpSocket;
    static ReentrantLock lock;
    static int bookID = 1;

    public static void main(String[] args) throws Exception {
        final int tcpPort = 7000;
        final int udpPort = 8000;
        if (args.length != 1) {
            System.out.println(
                "ERROR: Provide 1 argument: input file containing initial inventory");
            System.exit(-1);
        }
        books = new LinkedHashMap<>();
        checkedOut = new LinkedHashMap<>();
        students = new LinkedHashMap<>();
        lock = new ReentrantLock();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String line = reader.readLine();
            while (line != null) {
                int quote = line.lastIndexOf('"');
                books.put(line.substring(0, quote + 1),
                          new AtomicInteger(
                              Integer.parseInt(line.substring(quote + 2))));
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) { e.printStackTrace(); }

        System.out.println("Initial book count:");
        for (Entry e : books.entrySet()) { System.out.println("  " + e); }

        tcpSocket = new ServerSocket(tcpPort);
        udpSocket = new DatagramSocket(udpPort);

        new Thread(() -> {
            while (true) {
                try {
                    new Thread(new TcpHandler(tcpSocket.accept())).start();
                } catch (Exception e) {}
            }
        }).start();

        new Thread(() -> {
            byte[] buf = new byte[1 << 15];
            DatagramPacket packet;
            while (true) {
                try {
                    packet = new DatagramPacket(buf, buf.length);
                    udpSocket.receive(packet);
                    new Thread(new UdpHandler(udpSocket, packet)).start();
                } catch (Exception e) {}
            }
        }).start();
    }

    static String handleCommand(String s) {
        String[] parts = s.split("\\|");
        switch (parts[0].charAt(0)) {
        case 's':
            return "The communication mode is set to " +
                (parts[1].charAt(0) == 'T' ? "TCP" : "UDP");
        case 'b':
            String student = parts[1];
            String book = parts[2];
            AtomicInteger count = books.get(book);
            if (count == null) {
                return "Request Failed - We do not have this book";
            }
            lock.lock();
            if (count.get() == 0) {
                lock.unlock();
                return "Request Failed - Book not available";
            }
            count.getAndDecrement();
            HashSet<Integer> ids = students.get(student);
            if (ids == null) {
                ids = new HashSet<>();
                students.put(student, ids);
            }
            ids.add(bookID);
            checkedOut.put(bookID, book);
            lock.unlock();
            return "Your request has been approved, " + bookID++ + " " +
                student + " " + book;
        case 'r':
            int id = Integer.parseInt(parts[1]);
            lock.lock();
            book = checkedOut.remove(id);
            if (book == null) {
                lock.unlock();
                return "" + id + " not found, no such borrow record";
            }
            for (HashSet<Integer> temp : students.values()) { temp.remove(id); }
            books.get(book).getAndIncrement();
            lock.unlock();
            return "" + id + " is returned";
        case 'l':
            student = parts[1];
            lock.lock();
            ids = students.get(student);
            if (ids == null) {
                lock.unlock();
                return "No record found for " + student;
            }
            String result = "";
            for (Integer i : ids) {
                if (result.length() > 0) { result += "|"; }
                result += i + " " + checkedOut.get(i);
            }
            lock.unlock();
            return result;
        case 'i':
            result = "";
            lock.lock();
            for (Entry e : books.entrySet()) {
                if (result.length() > 0) { result += "|"; }
                result += e.getKey() + " " + e.getValue();
            }
            lock.unlock();
            return result;
        case 'e': 
            PrintWriter out = null;
            try { out = new PrintWriter("inventory.txt");
            } catch (Exception e) {e.printStackTrace();}
            for (Entry e : books.entrySet()) {
                out.println(e.getKey() + " " + e.getValue());
            }
            out.close();
        }
        return "RIP";
    }

    static class TcpHandler implements Runnable {
        Socket socket;
        public TcpHandler(Socket socket) { this.socket = socket; }
        public void run() {
            try {
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
                PrintWriter out =
                    new PrintWriter(socket.getOutputStream(), true);
                String command = in.readLine();
                while (command != null) {
                    out.println(handleCommand(command));
                    System.out.println();
                    System.out.println("Books: " + books);
                    System.out.println("Students: " + students);
                    System.out.println("Checkedout: " + checkedOut);
                    command = in.readLine();
                }
            } catch (Exception e) { // e.printStackTrace();
            }
        }
    }

    static class UdpHandler implements Runnable {
        DatagramSocket socket;
        DatagramPacket packet;
        public UdpHandler(DatagramSocket socket, DatagramPacket packet) {
            this.socket = socket;
            this.packet = packet;
        }
        public void run() {
            try {
                packet.getData();
                String command =
                    new String(packet.getData(), 0, packet.getLength());
                packet.setData(handleCommand(command).getBytes("UTF-8"));
                udpSocket.send(packet);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}
