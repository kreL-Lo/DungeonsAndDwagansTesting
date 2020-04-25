import java.net.*;
import java.io.*;

import org.json.simple.JSONObject;
import org.msgpack.MessagePack;
import org.msgpack.annotation.Message;
import org.msgpack.packer.Packer;
import org.msgpack.type.Value;
import org.msgpack.unpacker.Converter;
import org.msgpack.unpacker.Unpacker;
public class Client
{
    // initialize socket and input output streams
    private Socket socket            = null;
    private BufferedReader  input   = null;
    private DataOutputStream out     = null;
    private DataInputStream in = null;
    private PrintWriter writer = null;
    byte [] payload = new byte[1024];
    // constructor to put ip address and port
    public Client(String address, int port)
    {

        // establish a connection
        try
        {
            socket = new Socket(address, port);
            System.out.println("Connected");

            // takes input from terminal
            input=new BufferedReader(new InputStreamReader(System.in));

            // sends output to the socket
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            writer = new PrintWriter(out,true);

        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
        MessagePack msgpack = new MessagePack();

        String line = "";
        int count =0;
        // keep reading until "Over" is input
        while (!line.equals("Over"))
        {
            try
            {

                line = input.readLine();
                JSONObject j=null;
                if(line.compareTo("create")==0) {
                    j = createRoom("test" + count);
                    ++count;
                }else if (line.compareTo("show")==0)
                {
                    j = showRooms();
                }else if (line.compareTo("join")==0){
                    j = joinRoom();
                }
                byte [] data = j.toString().getBytes();
                out.write(data);
                int cnt = in.read(payload);
                byte [] newData = new byte[cnt];
                for(int i =0 ;i<cnt;i++){
                    newData[i]= payload[i];
                }
                String strData = new String(newData, "UTF-8");
                System.out.println(strData);
            }
            catch(IOException i)
            {
                System.out.println(i);
            }
        }

        // close the connection
        try
        {
            input.close();
            out.close();
            socket.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }
    JSONObject createRoom(String name ){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("PROTOCOL","CREATE_LOBBY");
        jsonObject.put ("roomName",name);
        jsonObject.put("maxPlayersLimit",100);
        return  jsonObject;
    }
    JSONObject joinRoom(){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("PROTOCOL","JOIN_LOBBY");
        jsonObject.put("PLAYER_NAME","bigus");

        jsonObject.put("ROOM_ID","5ea2067d14b4e21ff4137d12");
        return  jsonObject;
    }
    JSONObject showRooms(){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("PROTOCOL","SHOW_LOBBIES");
        return  jsonObject;
    }

    public static void main(String args[])
    {
        Client client = new Client("127.0.0.1", 8000);
    }
}