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
            // System.out.println("slot block id: " + slots[i].getBlockID());
            if(slots[i].getBlockID() == -1){
                return i;
            }
        }
        //no empty frame found - find a frame to evict, evict and return empty frame number
        int emptyFrame = findEvict();
        // System.out.println("in find empty, slot to evict: " + emptyFrame);
        int evictedBlockID = evict(emptyFrame);
        if(evictedBlockID != -1){
            System.out.println("Evicted File " + evictedBlockID + " from Frame " + (emptyFrame + 1));
        }
        return emptyFrame;
    }

    //use lastEvicted to find an unpinned frame to evict
    public int findEvict(){
        int frameToEvict = -1;
        //no previous evictions - start at first frame and loop all
        if(lastEvicted == -1){
            for(int i = 0; i < slots.length; i++){
                if(slots[i].getPinned() == false){
                    frameToEvict = i;
                    break;
                }
            }

        //frame has been evicted - 'circular search'
        } else{
            //start at frame after last evicted frame. search following frames until end of array reached. search begingin of array until lastevicted reached.
                for (int offset = 1; offset <= slots.length; offset++) {
                int i = (lastEvicted + offset) % slots.length;
                if (!slots[i].getPinned()) {
                    frameToEvict = i;
                    break;
                }
            }
            
        }
        return frameToEvict;
    }

    public int evict(int slotNumber){
        int blockEvicted = -1;
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
                System.out.println("Block " + slots[slotNumber].getBlockID() + " written to disk");
            }
            //store evicted blockID
            blockEvicted = slots[slotNumber].getBlockID();

            //dirty is false and has been written to block - evict the block from the frame - reset metadata
            slots[slotNumber].setBlockID(-1);
            slots[slotNumber].setContent(new char[4000]);
            lastEvicted = slotNumber;
        }
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }
        return blockEvicted;
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

            System.out.println("Brought File " + readBlockID + " from disk");
            return String.valueOf(file);

        } catch (IOException e) {
            System.out.print(e.getMessage());
            return "IO error";

        }
    }

    //print the content from record k from the file
    public int GET(int k){
        //find the block that has the record
        int blockID = (int) Math.ceil(k / NUMBER_OF_RECORDS) + 1;
        int slotNumber = findBlock(blockID);
        if(slotNumber == -1){
            //block is not in buffer - find empty slot (or evict one)
            int emptySlot = findEmpty();
            //all frames are pinned - cannot evict
            if(emptySlot == -1){
                return -1;
            } 
            //There is an empty frame - read block from disk and set frame metadata
            else{
                Frame frame = slots[emptySlot];
                String diskBlock = readFromDisk(blockID);
                char[] diskBlockContent = diskBlock.toCharArray();
                frame.setContent(diskBlockContent);
                frame.setBlockID(blockID);
                frame.setDirty(false);

                return emptySlot;
                
            }
            
        } else{
            //block is in buffer - return the record content
            System.out.println("File " + blockID + " already in memory");
            return slotNumber;
        }
    }

    public int SET(int k, String newRecordContent){
        //Find the block in the buffer or read it to buffer
        int blockFrame = GET(k);
        //all frames pinned - cannot fetch record/block
        if (blockFrame == -1){
            return -1;
        }
        //block is in buffer - change the content 
        else {
            slots[blockFrame].updateRecord(k, newRecordContent);
            return blockFrame;
        }
    }

    public int PIN(int BID){
        int slotNumber = findBlock(BID);
        if(slotNumber == -1){
            //block is not in buffer - find empty slot (or evict one)
            int emptySlot = findEmpty();
            //all frames are pinned - cannot evict, cannot pin
            if(emptySlot == -1){
                return -1;
            } 
            //There is an empty frame - read block from disk and set pin true
            else{
                Frame frame = slots[emptySlot];
                String diskBlock = readFromDisk(BID);
                char[] diskBlockContent = diskBlock.toCharArray();
                frame.setContent(diskBlockContent);
                frame.setBlockID(BID);
                frame.setDirty(false);

                if(frame.getPinned() != true){
                    System.out.println("Not already pinned.");

                }
                else{
                    System.out.println("Already pinned.");
                }
                frame.setPinned(true);
                return emptySlot;
                
            }
            
        } else{
            //block is in buffer - return the record content
            if(slots[slotNumber].getPinned() != true){
                    System.out.println("Not already pinned.");
                }
                else{
                    System.out.println("Already pinned.");
                }
            slots[slotNumber].setPinned(true);
            return slotNumber;
        }
    }

    public int UNPIN(int BID){
        int slotNumber = findBlock(BID);
        if(slotNumber == -1){
            //block is not in buffer - cannot unpin
            return slotNumber;
        } else{
            //block is in buffer - check if already false
            if(slots[slotNumber].getPinned() == true){
                    System.out.println("Not already unpinned.");
                }
                else{
                    System.out.println("Already unpinned.");
                }
            slots[slotNumber].setPinned(false);
            System.out.println("slotNumber of unpinned block: " + slotNumber);
            System.out.println("pin value after pin set tp false : " + slots[slotNumber].getPinned());
            System.out.println("lasteEvicted: " + lastEvicted);
            return slotNumber;
        }
    }

}