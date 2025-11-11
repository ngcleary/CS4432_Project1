import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public class BufferPool {
    private Frame[] slots;
    private int lastEvicted; //track the slot number of last evicted frame

     private static final int NUMBER_OF_RECORDS = 100;


    public BufferPool(int bufferSize){
        slots = new Frame[bufferSize];
         for(int i = 0; i < bufferSize; i++){
            slots[i] = new Frame();
        }
        lastEvicted = -1;
    }

    public Frame[] getSlots(){
        return slots;
    }

    public int getLastEvicted(){
        return lastEvicted;
    }

    public void setLastEvicted(int lastEvicted){
        this.lastEvicted = lastEvicted;
    }

    //search each slot in the bufferpool for blockid. return the slot number where the block is located, or -1 if not in bufferpool
    public int findBlock(int blockID){
        for(int i = 0; i < slots.length; i++){
            if(blockID == slots[i].getBlockID()){
                return i;
            }
        }
        return -1;
    }

    //findblockID and return the content (as string) if the block exists in the bufferpool
    public String getBlockContent(int blockID){
        int slotNumber = findBlock(blockID);
        if(slotNumber == -1){
            return "Block is not in the buffer";
        } else{
            return String.valueOf(slots[slotNumber].getContent());
        }
    }

    //search buffer pool for empty frame (blockID = -1) and return slot number. if no empty frame call findEvict to get frame to evict
    public int findEmpty(){
        for(int i = 0; i < slots.length; i++){
            System.out.println("slot block id: " + slots[i].getBlockID());
            if(slots[i].getBlockID() == -1){
                return i;
            }
        }
        //no empty frame found - find a frame to evict, evict and return empty frame number
        int emptyFrame = findEvict();
        System.out.println("in find empty, slot to evict: " + emptyFrame);
        evict(emptyFrame);
        return emptyFrame;
    }

    //use lastEvicted to find an unpinned frame to evict
    public int findEvict(){
        //no previous evictions - start at first frame and loop all
        if(lastEvicted == -1){
            for(int i = 0; i < slots.length; i++){
                System.out.println("in find evict, checking pinned: " + slots[i].getPinned());
                if(slots[i].getPinned() == false){
                    return i;
                }
            }
            //all frames are pinned - return -1
            return -1;
        } else{
            //start at frame after last evicted frame. search following frames until end of array reached. search begingin of array until lastevicted reached.
            for(int i = lastEvicted + 1; i != lastEvicted; i++){
                if(i > slots.length){
                    i = 0;
                }
                if(slots[i].getPinned() == false){
                    return i;
                }
            }
            return -1;
        }
    }

    public void evict(int slotNumber){
        try {
            if(slotNumber == -1){
            System.out.print("All frames are pinned. Cannot evict");
        } else{
            //write to disk if flag is dirty
            if(slots[slotNumber].getDirty() == true){
                //get file.txt to write to 
                String diskBlock = "F" + slots[slotNumber].getBlockID() + ".txt";
                FileWriter fWriter = new FileWriter(diskBlock);
                fWriter.write(slots[slotNumber].getContent());
                fWriter.close();
                slots[slotNumber].setDirty(false); //set frame dirty flag false

            }
            //dirty is false or has been written to block - evict the block from the frame - reset metadata
            slots[slotNumber].setBlockID(-1);
            slots[slotNumber].setContent(new char[4000]);
            lastEvicted = slotNumber;
        }
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }
    }
    
    public String readFromDisk(int readBlockID){
        try {
            char[] file = new char[4000];
            int fileIndex = 0;
            String diskBlock = "F" + readBlockID + ".txt";
            FileReader fr = new FileReader(diskBlock);

            int charInt;
            while((charInt = fr.read()) != -1){
                file[fileIndex] = (char) charInt;
                fileIndex++;
            }

            //System.out.println("block from disk: " + String.valueOf(file));
            return String.valueOf(file);

        } catch (IOException e) {
            System.out.print(e.getMessage());
            return "IO error";

        }
    }

    //print the content from record k from the file
    public String GET(int k){
        //find the block that has the record
        int blockID = (int) Math.ceil(k / NUMBER_OF_RECORDS) + 1;
        int slotNumber = findBlock(blockID);
        if(slotNumber == -1){
            //block is not in buffer - find empty slot (or evict one)
            int emptySlot = findEmpty();
            //all frames are pinned - cannot evict
            if(emptySlot == -1){
                return "The corresponding block " + blockID + " cannot be accessed from disk because the memory buffers are full";
            } 
            //There is an empty frame - read block from disk and set frame metadata
            else{
                Frame frame = slots[emptySlot];
                String diskBlock = readFromDisk(blockID);
                char[] diskBlockContent = diskBlock.toCharArray();
                frame.setContent(diskBlockContent);
                frame.setBlockID(blockID);
                frame.setDirty(false);

                //get record and return
                String recordContent = frame.getRecord(k);
                System.out.println("Record " + k + " from block " + blockID + ": " + recordContent);
                return recordContent;
                
            }
            
        } else{
            //block is in buffer - return the record content
            String recordContent = slots[slotNumber].getRecord(k);
            System.out.println("record content: " + recordContent);
            return recordContent;
        }
    }

}
