import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class models the TCP-Client part of the broadcaster, where the client establishes a TCP socket connection with
 * its parent server in the broadcast tree, and keeps the connection alive by frequently sending HEART_BEAT messages, and
 * on the response receives the HEART_BEAT message echo, or updated state of the broadcast tree'tcpSocket root data upon an update
 * was taken place.
 */

public class Client
{

    /**
     * The TCP socket client establishes with the parent node in the broadcast tree
     */
    private Socket tcpSocket;

    /**
     * The TCP buffer reader that reads messages from the TCP connection
     */
    private BufferedReader is;

    /**
     * The TCP printer writer that sends message to the parent from client
     */
    private PrintWriter os;

    /**
     * Parent's IP address on the broadcast tree
     */
    private String parentAddress;

    /**
     * Parent's port number on the broadcast three
     */
    private int parentPort;

    /**
     * @param address IP address of the parent node on the broadcast tree that client aims to connect to, put the address as "localhost"
     * @param port    port number of the parent node on the broadcast tree that the client aims to conenct to
     */
    public Client(String address, int port)
    {
        parentAddress = address;
        parentPort = port;

        /*
        Establishes a TCP connection to the server
         */
        Connect();

        interactWithParet();

    }

    /**
     * Establishes a socket connection to the server that is identified by the parentAddress and the parentPort
     */
    public void Connect()
    {
        try
        {
            tcpSocket = new Socket(parentAddress, parentPort);
            //tcpSocket.setKeepAlive(true);
            //br= new BufferedReader(new InputStreamReader(System.in));
            /*
            Read and write buffers on the socket
             */
            is = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
            os = new PrintWriter(tcpSocket.getOutputStream());

            System.out.println("Successfully connected to " + parentAddress + " on port " + parentPort);
        }
        catch (IOException e)
        {
            //e.printStackTrace();
            System.err.println("Error: no server has been found on " + parentAddress + "/" + parentPort);
        }
    }

    /**
     * sends HEART_BEAT messages to keep the TCP connection alive, and on the answer anything rather than HEART_BEAT, considers as
     * the updated and broadcasted root's state value, and hence updates its own local view of the root's broadcasted data accordingly
     * @return the received server answer
     */
    public String interactWithParet()
    {
        String response = new String();

        try
        {
            while(true)
            {
                /*
                Send the heartbeat message to keep the TCP connection alive
                 */
                os.println(Data.HEAT_BEAT_MESSAGE);
                os.flush();

                /*
                Reads a line from the server via Buffer Reader
                 */
                response = is.readLine();
                if(!response.equalsIgnoreCase(Data.HEAT_BEAT_MESSAGE))
                {
                    /*
                    Data is not a heart_beat message, so it notifies a change over
                    the data variable at the root, which corresponds to a broadcast
                     */
                    Data.setData(response);
                }

            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Client.java: interactWithParet. Socket read Error");
        }
        return response;
    }


    /**
     * Disconnects the socket and closes the buffers
     */
    public void Disconnect()
    {
        try
        {
            is.close();
            os.close();
            tcpSocket.close();
            System.out.println("ConnectionToServer. SendForAnswer. Connection Closed");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
