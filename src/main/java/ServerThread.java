import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class hosts a SINGLE connected client (i.e., child on the broadcast tree), listens to the request of the child,
 * which is by default of the protocol a HEART_BEAT message, echos back the received message to the client in the case
 * that there is no update on the local copy of the root'tcpSocket state i.e., Data class, and in the case of an update, returns
 * back to the client the update view of the local Data
 */
class ServerThread extends Thread
{
    /**
     * The TCP buffer reader that reads messages from the TCP connection
     */
    private BufferedReader is;

    /**
     * The TCP printer writer that sends message to the parent from client
     */
    private PrintWriter os;

    /**
     * The TCP socket client establishes with the parent node in the broadcast tree
     */
    private Socket tcpSocket;

    /**
     * Creates a server thread on the input socket
     *
     * @param s input socket to create a thread on
     */
    public ServerThread(Socket s)
    {
        this.tcpSocket = s;
    }


    /**
     * The server thread, echos the client with the received message from the client, unless there is an update on the
     * local value of the root broadcasted state (i.e., Data), which in that case returns back the updated value to the
     * client, the entire function is executed on an independent thread.
     */
    public void run()
    {
        try
        {
            is = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
            os = new PrintWriter(tcpSocket.getOutputStream());

        }
        catch (IOException e)
        {
            System.err.println("Server Thread. Run. IO error in server thread");
        }

        String heart_beat;
        try
        {
            /*
            oldData keeps the last broadcasted data to the client
             */
            String oldData = new String();
            while (true)
            {
                heart_beat = is.readLine();

                /*
                The state of root data has been changed since the last broadcast
                 */
                if (oldData != Data.getData())
                {
                    oldData = Data.getData();
                    os.println(oldData);
                    os.flush();
                    System.out.println("Broadcasted " + oldData + " to " + tcpSocket.getRemoteSocketAddress());
                }
                else
                {
                    /*
                    The state has not been change, hence the heart_beat message is replied back
                     */
                    os.println(heart_beat);
                    os.flush();
                }

            }
        }
        catch (IOException ex)
        {

            heart_beat = this.getName(); //reused String line for getting thread name
            System.err.println("Server Thread. Run. IO Error/ Client " + heart_beat + " terminated abruptly");

        }
        catch (NullPointerException e)
        {
            System.err.println("Server Thread. Run.Client ");
        }
        finally
        {
            try
            {
                System.out.println("Closing the connection");
                if (is != null)
                {
                    is.close();
                    System.err.println(" Socket Input Stream Closed");
                }

                if (os != null)
                {
                    os.close();
                    System.err.println("Socket Out Closed");
                }
                if (tcpSocket != null)
                {
                    tcpSocket.close();
                    System.err.println("Socket Closed");
                }

            }
            catch (IOException ie)
            {
                System.err.println("Socket Close Error");
            }
        }//end finally
    }
}

