#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <vector>

using namespace std;

#define PORT_BASE 4444

// Port number of the parent of the current process
int parentPort;
int parent_socket  = 0;

// The server port of the current process to serve the children
int myPort;
// List of the children
vector<int> children;

// Thread function that listen messages comming from the parent process.
// It print the parent message and also broadcast it to its own children.
void *parentListener(void *arg)
{
    cout << "Parent Listener created" << endl;
    int valread;
    char parent_msg[1024] = {0};

    while(1)
    {
        // clear previous message from buffer
        memset(&parent_msg, 0, sizeof(parent_msg));

        // read from parent - Note! read() is a blocking function
        valread = read( parent_socket  , parent_msg, 1024);

        // If read function return 0, that means and it cannot connect the parent.
        // In a situation like this, stop listening the parent.
        if(valread <= 0)
        {
            printf("Parent is gone\n");
            break;
        }

        printf("Parent says: %s \n",parent_msg);

        // broadcast the parent message to children
        if(children.size() > 0)
        {
            for(int i=0; i<children.size(); i++)
            {
                int child = children[i];
                send(child , parent_msg , strlen(parent_msg) , 0 );
            }
            printf("The parrent message (%s) is sent to all %d children\n", parent_msg, children.size());
        }

    }
    pthread_exit(NULL);
}

// Thread function that listen the new processes that want to connect.
void *childAccepter(void *arg)
{
    cout << "Child Accepter created" << endl;

    int welcome_socket, child_socket, valread;
    struct sockaddr_in server_address;
    int opt = 1;
    int addrlen = sizeof(server_address);

    // Creating socket file descriptor
    if ((welcome_socket = socket(AF_INET, SOCK_STREAM, 0)) == 0)
    {
        perror("Welcome socket failed");
        exit(EXIT_FAILURE);
    }

    // Attaching the socket to the port forcefully, even if the port in use.
    if (setsockopt(welcome_socket, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt))) //SO_REUSEPORT commented
    {
        perror("Socket cannot be attached");
        exit(EXIT_FAILURE);
    }

    // Define spesifications of the server address
    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = INADDR_ANY;
    server_address.sin_port = htons( myPort );

    // Bind socket to the port
    if (::bind(welcome_socket, (struct sockaddr *)&server_address,sizeof(server_address)) < 0)
    {
        perror("bind failed");
        exit(EXIT_FAILURE);
    }
    // Wait the connections of children - listen() is a blocking function
    if (listen(welcome_socket, 3) < 0)
    {
        perror("listen failed");
        exit(EXIT_FAILURE);
    }

    while(1){
        if ((child_socket = accept(welcome_socket, (struct sockaddr *)&server_address,(socklen_t*)&addrlen))<0)
        {
            perror("Error while accepting a new child");
            exit(EXIT_FAILURE);
        }
        else
        {
            printf("Child %d connected\n", child_socket);
            // Add the socket id of the new child to the children list
            children.push_back(child_socket);
            printf("Now, there are %d children\n", children.size());
        }
    }

    pthread_exit(NULL);
}

int main(int argc, char const *argv[])
{
    // 1-Ask id
    printf("What is your id? Type 0 if you are root.\n");
    int id;
    cin >> id;

    // Ports are determined by adding the specified id of the process to 8080
    myPort = PORT_BASE + id;

    // If the current process is not root, ask id of the parent process
    if(id != 0)
    {
        printf("Who is your parent?\n");
        int parent;
        cin >> parent;
        parentPort = PORT_BASE + parent;
    }

    // 2- Open a port for children as a server.
    // Since listen() function of a server is a blocking command,
    // listening part should be handled by a different thread.
    pthread_t acceptor;
    if( pthread_create(&acceptor, NULL, childAccepter , NULL) < 0)
    {
        perror("Child acceptor cannot be created");
        return -1;
    }
    // Now, the process wait for children in a different thread in port (8080+ its id)

    // 3- Connect to parent (if it is not root)
    if(id != 0)
    {
        // Create a socket
        if ((parent_socket  = socket(AF_INET, SOCK_STREAM, 0)) < 0)
        {
            perror("Parent socket creation error");
            return -1;
        }

        struct sockaddr_in parent_address;
        memset(&parent_address, '0', sizeof(parent_address));

        // Set properties of the parent address
        parent_address.sin_family = AF_INET;
        parent_address.sin_port = htons(parentPort);

        // Convert addresses from text to binary form
        if(inet_pton(AF_INET, "127.0.0.1", &parent_address.sin_addr)<=0)
        {
            perror("Invalid address - Address not supported");
            return -1;
        }
        // It connects the local host (127.0.0.1) as stated in above
        if (connect(parent_socket, (struct sockaddr *)&parent_address, sizeof(parent_address)) < 0)
        {
            perror("Connection to parent failed");
            return -1;
        }
    }
    // Now, It is connected succsessfully to its parent

    // 4-Listen parent (if it is not root) and notify children upon parent message
    // Since read() function is a blocking command, messages of the parent
    // should be read in a new thread.
    if(id !=0)
    {
        pthread_t parent_th;
        if( pthread_create(&parent_th, NULL, parentListener , NULL) < 0)
        {
            perror("Parent listener cannot be created");
            return -1;
        }
    }
    // Now, it can print messages from parent and broadcast it to its children

    //5- listen the user (main thread) -> notify children upon user message
    string user_msg = "";
    while(true)
    {
        // get user input
        printf("Enter smt to broadcast to your children set or exist to terminate\n");
        getline(cin, user_msg);
        // If the user type "exit", stop the program
        if(user_msg == "exit")
        {
            break;
        }

        // broadcast user message to its children
        if(children.size() > 0)
        {
            for(int i=0; i<children.size(); i++)
            {
                int child = children[i];
                send(child , user_msg.c_str() , strlen(user_msg.c_str()) , 0 );
            }
            printf("The user message (%s) is sent to all %d children\n", user_msg.c_str(), children.size());
        }
    }

    return 0;
}
