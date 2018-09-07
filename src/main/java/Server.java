import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;


public class Server extends Thread
{
    private ServerSocket mServerSocket;
    ServerThread mServerThread;
    /**
     * The data to be broadcasted
     */
//    private String mData;


    public static final int DEFAULT_SERVER_PORT = 4444;
    /**
     * Initiates a server socket on the input port, listens to the line, on receiving an incoming
     * connection creates and starts a ServerThread on the client
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

    public void run()
    {
        while (true)
        {
            ListenAndAccept();
        }
    }

//    public void setData(String data)
//    {
//        mData = data;
//        if(mServerThread != null)
//            mServerThread.setData(data);
//    }

    /**
     * Listens to the line and starts a connection on receiving a request from the client
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
