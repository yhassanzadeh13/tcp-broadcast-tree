import java.util.Scanner;

public class Data extends Thread
{
    private static String data;
    public static String QUIT = "QUIT";
    public static String getData()
    {
        return data;
    }

    public static void setData(String data)
    {
        Data.data = data;
        System.out.println("The broadcasted data from server is " + data);
    }

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
