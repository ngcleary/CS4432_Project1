import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
	
	public static void main(String[] args)  throws IOException {
        //Scanner for command line input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter buffer size: ");
        int bufferSize = scanner.nextInt();
        scanner.nextLine();
        
        //create bufferpool
        BufferPool bufferPool = new BufferPool(bufferSize);
        Frame[] slots = bufferPool.getSlots();
        
        while (true) {
        System.out.print("The program is ready for the next command: ");
        String functionInput = scanner.nextLine().trim();
        
        int firstQuote = functionInput.indexOf('"');
        int lastQuote = functionInput.lastIndexOf('"');
        String k = null;
        String command = null;
        String newRecordInput = null;

        // find the quoted section
        if (firstQuote != -1 && lastQuote != -1 && lastQuote > firstQuote) {
            newRecordInput = functionInput.substring(firstQuote + 1, lastQuote); // inside quotes
            String beforeQuote = functionInput.substring(0, firstQuote).trim();
            String[] parts = beforeQuote.split("\\s+");
            command = parts[0].toLowerCase();
            if (parts.length > 1) {
                k = parts[1];
            }
        } else {
            // no quotes, normal command
            String[] parts = functionInput.split("\\s+");
            command = parts[0];
            if (parts.length > 1) {
                k = parts[1];
            }
        }
        //String newRecordInput = splitInput.length > 2 ? splitInput[2] : null;

        if (functionInput.equalsIgnoreCase("exit")) {
            System.out.println("Exiting program...");
            break;
        }
        switch (command) {
            case "get":
                if (k == null) {
                    System.out.println("Error: GET command requires an argument.");
                } else {
                    //get the frames stored in the buffer
                    int frameSlot = bufferPool.GET(Integer.parseInt(k));
                    

                    //no frame holds the block calc the blockID and return
                    if(frameSlot == -1){
                        int calcBlockID = (int) Math.ceil(Integer.parseInt(k) / 100) + 1;
                        System.out.println("The corresponding block " + calcBlockID + " cannot be accessed from disk because the memory buffers are full");
                    } 
                    //frame is in buffer - return record, frame, and id
                    else{
                        String recordContent = slots[frameSlot].getRecord(Integer.parseInt(k));
                        int blockID = slots[frameSlot].getBlockID();
                        //print
                        System.out.println(recordContent);
                        System.out.println("File " + blockID + " located in frame " + (frameSlot + 1));
                    }

                }
                break;

            case "set":
                if (k == null || newRecordInput == null) {
                    System.out.println("Error: SET command requires two arguments.");
                } else {
                    //call set
                    int frameSlot = bufferPool.SET(Integer.parseInt(k), newRecordInput);

                    //no frame holds the block calc the blockID and return
                    if(frameSlot == -1){
                        int calcBlockID = (int) Math.ceil(Integer.parseInt(k) / 100) + 1;
                        System.out.println("Write was unsuccessful, the corresponding block " + calcBlockID + " cannot be accessed from disk because the memory buffers are full");
                    } 
                    //frame is in buffer and contenet updated
                    else{
                        String recordContent = slots[frameSlot].getRecord(Integer.parseInt(k));
                        int blockID = slots[frameSlot].getBlockID();
                        //print
                        System.out.println("Write was successful");
                        // System.out.println("Record " + k + " from block " + blockID + ": " + recordContent);
                        System.out.println("File " + blockID + " located in frame " + (frameSlot + 1));
                    }

                }
                break;

            //k == BID in terms of input reading
            case "pin":
                if (k == null) {
                    System.out.println("Error: PIN command requires an argument.");
                } else {
                    //get the frames stored in the buffer
                    int frameSlot = bufferPool.PIN(Integer.parseInt(k));
                    
                    //no frame holds the block calc the blockID and return
                    if(frameSlot == -1){
                        System.out.println("The corresponding block " + k + " cannot be pinned because the memory buffers are full");
                    } 
                    //frame is in buffer - return record, frame, and id
                    else{
                        //print
                        System.out.println("File " + k + " located in frame " + (frameSlot + 1));
                    }

                }
                break;
                
                //k == BID in terms of input reading
                case "unpin":
                if (k == null) {
                    System.out.println("Error: UNPIN command requires an argument.");
                } else {
                    //get the frames stored in the buffer
                    int frameSlot = bufferPool.UNPIN(Integer.parseInt(k));
                    

                    //no frame holds the block calc the blockID and return
                    if(frameSlot == -1){
                        System.out.println("The corresponding block " + k + " cannot be unpinned because it is not in memory");
                    } 
                    //frame is in buffer - return record, frame, and id
                    else{
                        //print
                        System.out.println("File " + k + " located in frame " + (frameSlot + 1));
                    }

                }
                break;

            default:
                System.out.println("Unknown command: " + command);
                break;
        }

        System.out.println();
    }
        
       

        //pin all 
        // for(int i = 0; i < bufferSize; i++){
        //     bufferPool.getSlots()[i].setPinned(true);
        //     System.out.println("pinned: " + (bufferPool.getSlots()[i].getPinned()));
        // }

        // bufferPool.getSlots()[1].setPinned(true);
        // bufferPool.setLastEvicted(0);
        
        // bufferPool.GET(19);
        // bufferPool.GET(119);
        // bufferPool.GET(219);
        // bufferPool.GET(619);

        //bufferPool.readFromDisk(6);
        // for(int i = 0; i < bufferSize; i++){
        //     System.out.println("bufferpool: " + String.valueOf(bufferPool.getSlots()[i].getContent()));
        // }
        
	}
}
