import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ServerThread extends Thread
{
    protected BufferedReader is;
    protected PrintWriter os;
    protected Socket s;
//    private String Data;

    /**
     * Creates a server thread on the input socket
     * @param s input socket to create a thread on
     */
    public ServerThread(Socket s)
    {
        this.s = s;
    }

//    public void setData(String data)
//    {
//        Data = data;
//    }

    /**
     * The server thread, echos the client until it receives the QUIT string from the client
     */
    public void run()
    {
        try
        {
            is = new BufferedReader(new InputStreamReader(s.getInputStream()));
            os = new PrintWriter(s.getOutputStream());

        }
        catch (IOException e)
        {
            System.err.println("Server Thread. Run. IO error in server thread");
        }

        try
        {
            /*
            The last broadcasted data to the client
             */
            String oldData = new String();
            while (true)
            {
                if(oldData != Data.getData())
                {
                    oldData = Data.getData();
                    os.println(oldData);
                    os.flush();
                    System.out.println("Broadcasted " + oldData + " to " + s.getRemoteSocketAddress());
                }
            }
        }

        catch (NullPointerException e)
        {
            System.err.println("Server Thread. Run.Client ");
        } finally
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
                if (s != null)
                {
                    s.close();
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

