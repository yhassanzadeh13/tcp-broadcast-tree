
public class Broadcaster
{
    /**
     * The server side of the broadcaster process
     */
    Server mServer;

    /**
     * The client side of the broadcaster process
     */
    Client mClient;

    /**
     * The data of interest that is broadcasted from parent
     * to childeren over this broadcast tree. Each node receives the data
     * from its parent node, and broadcasts it to its children
     */


    public Broadcaster(int myPort, int serverPort, String serverIP)
    {
        /*
        Establishes a server on the port determined by myPort
         */
        mServer = new Server(myPort);
        mServer.start();

        /*
        Initiates a connection to the specified server on (serverIP and serverPort) if the node is not
        root
         */
        if ((!serverIP.equalsIgnoreCase(Main.ROOT) && serverPort != -1))
        {
            mClient = new Client(serverIP, serverPort);
            mClient.start();
        }


    }


//    public void setData(String data)
//    {
//        mData = data;
//        mServer.setData(data);
//    }
}
