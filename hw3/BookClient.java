import java.io.*;
import java.util.*;
import java.util.Scanner;

import java.net.*;

public class BookClient {
    static boolean tcp = false;
    static DatagramSocket udpSocket;
    static Socket tcpSocket;
    static int clientId;

    static String hostAddress;

    final static int tcpPort = 7000;
    final static int udpPort = 8000;

    static void sendData(String data) throws IOException {
        if (tcp) {
            PrintStream out = new PrintStream(tcpSocket.getOutputStream());
            out.println(data);
        } else {
            InetAddress address = InetAddress.getByName(hostAddress);
            DatagramPacket packet = new DatagramPacket(data.getBytes("UTF-8"), data.getBytes("UTF-8").length, address, udpPort);
            udpSocket.send(packet);
        }
    }

    static String getData() throws IOException {
        if (tcp) {
            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
            return in.readLine();
        } else {
            byte[] data = new byte[1<<15];
            InetAddress address = InetAddress.getByName(hostAddress);
            DatagramPacket packet = new DatagramPacket(data, data.length, address, udpPort);
            udpSocket.receive(packet);
            return new String(data, "UTF-8");
        }
    }

    public static void main(String[] args) throws SocketException, FileNotFoundException, IOException {
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

        udpSocket = new DatagramSocket();

        Scanner sc = new Scanner(new FileReader(commandFile));
        PrintWriter writer = new PrintWriter("out_" + clientId, "UTF-8");

        while (sc.hasNextLine()) {
            String cmd = sc.nextLine();
            String[] tokens = cmd.split(" ");

            if (tokens[0].equals("setmode")) {
                char mode = tokens[1].charAt(0);
                System.out.println("SetMode: " + mode);

                if (mode == 'T') {
                    sendData("setmode|T");
                    writer.println(getData());
                    tcpSocket = new Socket(hostAddress, tcpPort);
                    tcp = true;
                } else if (mode == 'U') {
                    sendData("setmode|U");
                    writer.println(getData());
                    tcpSocket.close();
                    tcp = false;
                } else {
                    System.out.println("Invalid mode");
                }
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
    }
}
