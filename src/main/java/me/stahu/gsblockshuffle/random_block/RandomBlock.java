package me.stahu.gsblockshuffle.random_block;

import java.util.Random;
import java.util.ArrayList;

public class RandomBlock {
    //
    public static ArrayList<String> getRandomBlock(ArrayList<ArrayList<ArrayList<String>>> blockList) {
        Random random = new Random();

        if(blockList.isEmpty()) {
            throw new IllegalArgumentException("Block list is empty");
        }

        ArrayList<ArrayList<String>> block = blockList.get(random.nextInt(blockList.size()));

        // Return random variant of the block
        return block.get(random.nextInt(block.size()));
    }
}
