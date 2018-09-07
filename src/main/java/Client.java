import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client extends Thread
{
    public static final String DEFAULT_SERVER_ADDRESS = "localhost";
    public static final int DEFAULT_SERVER_PORT = 4444;
    private Socket s;
    //private BufferedReader br;
    protected BufferedReader is;
    protected PrintWriter os;

    protected String serverAddress;
    protected int serverPort;

    /**
     * @param address IP address of the server, if you are running the server on the same computer as client, put the address as "localhost"
     * @param port    port number of the server
     */
    public Client(String address, int port)
    {
        serverAddress = address;
        serverPort = port;
        Connect();

        //readFromServer();

    }

    /**
     * Establishes a socket connection to the server that is identified by the serverAddress and the serverPort
     */
    public void Connect()
    {
        try
        {
            s = new Socket(serverAddress, serverPort);
            s.setKeepAlive(true);
            //br= new BufferedReader(new InputStreamReader(System.in));
            /*
            Read and write buffers on the socket
             */
            is = new BufferedReader(new InputStreamReader(s.getInputStream()));
            os = new PrintWriter(s.getOutputStream());

            System.out.println("Successfully connected to " + serverAddress + " on port " + serverPort);
        }
        catch (IOException e)
        {
            //e.printStackTrace();
            System.err.println("Error: no server has been found on " + serverAddress + "/" + serverPort);
        }
    }

    /**
     * sends the message String to the server and retrives the answer
     *
     * @return the received server answer
     */
    public String readFromServer()
    {
        String response = new String();
        try
        {

            /*
            Reads a line from the server via Buffer Reader
             */
            response = is.readLine();
            Data.setData(response);

        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Client.java: readFromServer. Socket read Error");
        }
        return response;
    }

    @Override
    public void run()
    {
        while (true)
        {
            readFromServer();
        }
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
            //br.close();
            s.close();
            System.out.println("ConnectionToServer. SendForAnswer. Connection Closed");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
