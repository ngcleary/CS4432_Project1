public class Main {
	
	public static void main(String[] args) {
        //array of chars 1-40
        char[] testContent = new char[4000];
        for (int i = 0; i < 4000; i++) {
            testContent[i] = (char) ('0' + ((i + 1) % 10)); // +1 so pattern starts at 1
        }
        
        Frame frame = new Frame(0);
        frame.setContent(testContent);
        char[] content= frame.getContent();
        for (int i = 0; i < 4000; i += 40) {
            System.out.println(new String(testContent, i, 40));
        }
        String record = frame.getRecord(1);
        System.out.println("record: " + record);

	}

}
