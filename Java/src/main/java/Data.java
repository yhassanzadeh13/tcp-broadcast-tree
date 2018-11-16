import java.util.Scanner;


/**
 * This class models the static shared preference of the data, which is the local view of this process of the broadcasted
 * state of the root
 */
public class Data extends Thread
{
    /**
     * Content of the heart_beat message
     */
    public static final String HEAT_BEAT_MESSAGE = "heart_beat_message";

    /**
     * The local view of this process of the broadcasted
     */
    private static String data;

    public static String QUIT = "QUIT";

    public static String getData()
    {
        return data;
    }

    /**
     * Sets the value of the data as well as prints the updated value
     * @param data updated value of the data
     */
    public static void setData(String data)
    {
        Data.data = data;
        System.out.println("The broadcasted data from server is " + data);
    }

    /**
     * If this process registers itself as the root process, this function is invoked on a separate thread and keeps
     * asking root user the updated value of the state, if any.
     */
    public void run()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the data to broadcast as the root:");
        data = scanner.nextLine();
        while (!data.equalsIgnoreCase(QUIT))
        {
            System.out.println(data + " has been placed in buffer to broadcast, you can either enter a new string to broadcast or QUIT to termination");
            data = scanner.nextLine();
        }
    }
}
