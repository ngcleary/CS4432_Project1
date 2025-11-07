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
        

        //first frame
        Frame frame = new Frame(0);
        char[] content = Arrays.copyOfRange(file, 0, 4000);
        frame.setContent(content);
        String record = frame.getRecord(92);
        System.out.println("record: " + record);

        String newContent = "F07-Rec601, Name601, address601, age601.";
        System.out.println("update Record");
        frame.updateRecord(92, newContent);


	}

}
