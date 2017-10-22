package vandyhacks.com.songstalgia;

import java.util.HashMap;

/**
 * Created by rishabh on 22/10/17.
 */

public class MoodHashes {

    private HashMap<Integer,Integer> revengeHash  = new HashMap<Integer,Integer>();
    private HashMap<Integer,Integer> helpHash  = new HashMap<Integer,Integer>();

    MoodHashes(){

        //revenge hashes init
        revengeHash.put(0,5);
        revengeHash.put(-1,5);
        revengeHash.put(1,3); //anger to annoyed
        revengeHash.put(2,7); //contempt to sadness
        revengeHash.put(3,3); //annoy to annoy
        revengeHash.put(4,4); //fear to fear
        revengeHash.put(5,7); //happy to sad
        revengeHash.put(6,1); //neutral/peace to angry
        revengeHash.put(7,7); //sad to sad
        revengeHash.put(8,1); //surprise to anger

        //help hashes init
        helpHash.put(0,5);
        helpHash.put(-1,5);
        helpHash.put(1,6); //angry to peace
        helpHash.put(2,5); //contempt to happiness
        helpHash.put(3,6); //annoy/disgust to peace
        helpHash.put(4,5); //fear to happy
        helpHash.put(5,5); //happy to happy
        helpHash.put(6,6); //peace/neutral to peace/neutral
        helpHash.put(7,5); //sadness to happy
        helpHash.put(8,6); //surprise to peace

    }

    public HashMap<Integer, Integer> getRevengeHash() {
        return revengeHash;
    }

    public void setRevengeHash(HashMap<Integer, Integer> revengeHash) {
        this.revengeHash = revengeHash;
    }

    public HashMap<Integer, Integer> getHelpHash() {
        return helpHash;
    }

    public void setHelpHash(HashMap<Integer, Integer> helpHash) {
        this.helpHash = helpHash;
    }

    public void addRevengeHashPair(int key, int value){
        revengeHash.put(key,value);
    }

    public void addHelpHashPair(int key, int value){
        helpHash.put(key,value);
    }

    public int getrevengeHashValue(int key){
        return revengeHash.get(key);
    }

    public int getHelpHashValue(int key){
        return revengeHash.get(key);
    }
}
