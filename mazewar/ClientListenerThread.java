import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Hashtable;
import java.util.HashMap;

public class ClientListenerThread implements Runnable {

    private MSocket mSocket  =  null;
    private Hashtable<String, Client> clientTable = null;
    private HashMap<Integer, MPacket> clientBuffer = null;
    private int globalSequenceNumber;

    public ClientListenerThread( MSocket mSocket,
                                Hashtable<String, Client> clientTable){
        this.mSocket = mSocket;
        this.clientTable = clientTable;
        this.clientBuffer = new HashMap<Integer, MPacket>();
        this.globalSequenceNumber = 0;
        if(Debug.debug) System.out.println("Instatiating ClientListenerThread");
    }

    public void run() {
        MPacket received = null;
        MPacket current = null;
        Client client = null;
        if(Debug.debug) System.out.println("Starting ClientListenerThread");
        while(true){
            try{
                received = (MPacket) mSocket.readObject();
                clientBuffer.put(received.sequenceNumber, received);

                System.out.println("ClientListener Received " + received);

                while (!clientBuffer.isEmpty()) {
                  try {
                    current = clientBuffer.get(globalSequenceNumber);
                    //Do stuff
                    client = clientTable.get(current.name);

                    System.out.println("ClientListener Starting " + current);

                    if(current.event == MPacket.UP){
                        client.forward();
                    }else if(current.event == MPacket.DOWN){
                        client.backup();
                    }else if(current.event == MPacket.LEFT){
                        client.turnLeft();
                    }else if(current.event == MPacket.RIGHT){
                        client.turnRight();
                    }else if(current.event == MPacket.FIRE){
                        client.fire();
                    }else if(current.event == MPacket.PROJECTILE_MOVE){
                        client.projectileMove(current);
                    }else{
                        throw new UnsupportedOperationException();
                    }
                    //Finish stuff
                    clientBuffer.remove(globalSequenceNumber);
                    globalSequenceNumber++;
                  }
                  catch (NullPointerException e) {
                    break;
                  }
                }

            }catch(IOException e){
                e.printStackTrace();
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }            
        }
    }
}
