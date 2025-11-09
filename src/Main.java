import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
	
	public static void main(String[] args)  throws IOException {
       
        char[] file = new char[4000];
        int fileIndex = 0;
        FileReader fr = new FileReader("F1.txt");

        //int representation of current char
        int charInt;
        while((charInt = fr.read()) != -1){
            file[fileIndex] = (char) charInt;
            fileIndex++;
        }
        
        char[] file2 = new char[4000];
        int fileIndex2 = 0;
        FileReader fr2 = new FileReader("F2.txt");

        //int representation of current char
        
        while((charInt = fr2.read()) != -1){
            file2[fileIndex2] = (char) charInt;
            fileIndex2++;
        }

        char[] file3 = new char[4000];
        int fileIndex3 = 0;
        FileReader fr3 = new FileReader("F3.txt");

        //int representation of current char
        
        while((charInt = fr3.read()) != -1){
            file3[fileIndex3] = (char) charInt;
            fileIndex3++;
        }
        
        //first frame
        Frame frame = new Frame();
        char[] content = Arrays.copyOfRange(file, 0, 4000);
        frame.setContent(content);
        char[] content2 = Arrays.copyOfRange(file2, 0, 4000);
        Frame frame2 = new Frame();
        frame2.setContent(content2);
        char[] content3 = Arrays.copyOfRange(file3, 0, 4000);
        Frame frame3 = new Frame();
        frame3.setContent(content3);
        frame.setBlockID(1);
        frame2.setBlockID(2);
        frame3.setBlockID(3);
  
        //String record = frame.getRecord(92);
        // System.out.println("slot #: " + frame.getBlockID());

        // String newContent = "F07-Rec601, Name601, address601, age601.";
        // System.out.println( "update Record");
        // frame.updateRecord(92, newContent);
        Scanner scanner = new Scanner(System.in);  // Create a Scanner object
        System.out.print("Enter buffer size: ");
        int bufferSize = scanner.nextInt();

        //create bufferpool
        BufferPool bufferPool = new BufferPool(bufferSize);
        bufferPool.getSlots()[0] = frame;
        // System.out.println("slots number of block 3, expecting 0: " + bufferPool.findBlock(3));
        // System.out.println("content of block id 3: " + bufferPool.getBlockContent(2));
        System.out.print("The program is ready for the next command: ");
        BufferPool bufferPoolTest = new BufferPool(3);
        bufferPoolTest.getSlots()[0] = frame;
        bufferPoolTest.getSlots()[1] = frame2;
        bufferPoolTest.getSlots()[2] = frame3;
        bufferPoolTest.GET(119);
	}
}
