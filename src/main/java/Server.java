import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class models an instance of the server part of the broadcaster process, that listens to the line for
 * reception of a child node on the broadcast tree via a server
 * socket, and casts EACH incoming TCP connection as an independent ServerThread object
 */
public class Server extends Thread
{
    /**
     * Instance of the server socket that listens to the line and accepts incomming conenctions
     */
    private ServerSocket mServerSocket;

    /**
     * Instance of the server thread that hosts an incoming received connection from a child node
     */
    private ServerThread mServerThread;

    /**
     * Initiates a server socket on the input port, listens to the line, on receiving an incoming
     * connection creates and starts a ServerThread on the child node
     * @param port
     */
    public Server(int port)
    {
        mServerThread = null;
        try
        {
            mServerSocket = new ServerSocket(port);
            System.out.println("Oppened up a server socket on " + Inet4Address.getLocalHost());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.err.println("Server class.Constructor exception on oppening a server socket");
        }
    }

    /**
     * Instantly listions to the line to accept a new connection on an independent thread
     */
    public void run()
    {
        while (true)
        {
            ListenAndAccept();
        }
    }


    /**
     * Listens to the line and starts a connection on receiving a request from the client (i.e., child node on the broadcast tree)
     * The connection is started and initiated as a ServerThread object
     */
    private void ListenAndAccept()
    {
        Socket s;
        try
        {
            s = mServerSocket.accept();
            System.out.println("A connection was established with a client on the address of " + s.getRemoteSocketAddress());
            mServerThread = new ServerThread(s);
            mServerThread.start();

        }

        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("Server Class.Connection establishment error inside listen and accept function");
        }
    }

}
