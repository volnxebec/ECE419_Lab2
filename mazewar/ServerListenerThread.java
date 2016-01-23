import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.HashMap;


public class ServerListenerThread implements Runnable {

    private MSocket mSocket =  null;
    private BlockingQueue eventQueue = null;
    private int localSequenceNumber;
    private HashMap<Integer, MPacket> serverBuffer = null;

    public ServerListenerThread( MSocket mSocket, BlockingQueue eventQueue){
        this.mSocket = mSocket;
        this.eventQueue = eventQueue;
        this.serverBuffer = new HashMap<Integer, MPacket>();
        this.localSequenceNumber = 0;

    }

    public void run() {
        MPacket received = null;
        MPacket current = null;

        if(Debug.debug) System.out.println("Starting a listener");
        while(true){
            try{
                received = (MPacket) mSocket.readObject();

                if (received.type == MPacket.HELLO) {
                  eventQueue.put(received);
                  continue;
                }

                serverBuffer.put(received.localSequenceNumber, received);
                if(Debug.debug) System.out.println("ServerListener Received: " + received);

                while (!serverBuffer.isEmpty()) {
                  try {
                    current = serverBuffer.get(localSequenceNumber);
                    //Do stuff
                   
                    System.out.println("ServerListener Starting " + current);

                    eventQueue.put(current);   

                    //Finish stuff
                    serverBuffer.remove(localSequenceNumber);
                    localSequenceNumber++;
                  }
                  catch (NullPointerException e) {
                    break;
                  }
                }



            }catch(InterruptedException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }
            
        }
    }
}
