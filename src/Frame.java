import java.util.*;

public class Frame {
    private char[] content;
    private Boolean dirty;
    private Boolean pinned;
    private int blockID;

    private static final int FILE_SIZE = 4000;
    private static final int RECORD_SIZE = 40;

    // public Frame(char[] content, Boolean dirty, Boolean pinned, int blockID){
    //     this.content = content;
    //     this.dirty = dirty;
    //     this.pinned = pinned;
    //     this.blockID = blockID;
    // }
      public Frame(int blockID){
        content = new char[4000];
        dirty = false;
        pinned = false;
        this.blockID = blockID;
    }

    //getters and setters
    public char[] getContent(){
        return content;
    }
    public void setContent(char[] newContent){
        this.content = newContent;
    }

    public Boolean getDirty(){
        return dirty;
    }
    public void setDirty(Boolean dirty){
        this.dirty = dirty;
    }

    public Boolean getPinned(){
        return pinned;
    }
    public void setPinned(Boolean pinned){
        this.pinned = pinned;
    }

    public int getBlockID(){
        return blockID;
    }
    public void setBlockID(int newBlockID){
        this.blockID = newBlockID;
    }

    //return a specific record (string of 40 bytes) in the block given the record number (k)
    public String getRecord(int k){
        int recStart = (RECORD_SIZE * k) - RECORD_SIZE;
        char[] content = getContent();
        char[] record = Arrays.copyOfRange(content, recStart, recStart + 40);
        
        //convert char array to string
        String contentString = new String(record);
        return contentString;
    }

    //take in record number and new content (40 bytes). set dirty byte (pin record??)
    public void updateRecord(int k, String newRecordContent){
         int recStart = (RECORD_SIZE * k) - RECORD_SIZE;
         char[] content = getContent();
         char[] FileContentArr = Arrays.copyOfRange(content, 0, FILE_SIZE);
         char[] newRecordContentArr = newRecordContent.toCharArray();
         System.arraycopy(newRecordContentArr, 0, FileContentArr, recStart, 40);

         //set dirty flag
         setDirty(true);
    }
}
