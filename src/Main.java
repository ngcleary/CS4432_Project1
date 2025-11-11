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

        System.out.print("The program is ready for the next command: ");
        String functionInput = scanner.nextLine();
        String[] splitInput = functionInput.split("\\s+");
        String command = splitInput[0];
        String arg = splitInput.length > 1 ? splitInput[1] : null;

        switch (command){
             case "GET":
             bufferPool.GET(Integer.parseInt(arg));
             break;
              default:
              System.out.println("Unknown command: " + command);
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
