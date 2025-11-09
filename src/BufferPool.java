import java.io.FileWriter;
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
            if(slots[i].getBlockID() == -1){
                return i;
            }
        }
        //no empty frame found - find a frame to evict
        int emptyFrame = findEvict();
        evict(emptyFrame);
        return emptyFrame;
    }

    //use lastEvicted to find an unpinned frame to evict
    public int findEvict(){
        //no previous evictions - start at first frame and loop all
        if(lastEvicted == -1){
            for(int i = 0; i < slots.length; i++){
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

    //print the content from record k from the file
    public String GET(int k){
        //find the block that has the record
        int blockID = (int) Math.ceil(k / NUMBER_OF_RECORDS) + 1;
        int slotNumber = findBlock(blockID);
        if(slotNumber == -1){
            //block is not in buffer
            System.out.println("not in buffer");
            return "not in buffer";
        } else{
            //block is in buffer - return the record content
            String recordContent = slots[slotNumber].getRecord(k);
            System.out.println("record content: " + recordContent);
            return recordContent;
        }
    }

}
