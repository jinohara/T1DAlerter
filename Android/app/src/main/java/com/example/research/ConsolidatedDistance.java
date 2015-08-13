package com.example.research;

/**
 * Created by fishy on 8/5/2015.
 */
public class ConsolidatedDistance implements Comparable<ConsolidatedDistance> {
    public double distance;
    public int chunksindex;
    public int[] actualChunk;
    public ConsolidatedDistance()
    {
    }
    public int compareTo(ConsolidatedDistance o) {
        if (this.distance > o.distance)
        {
            return 1;
        }
        else if (this.distance < o.distance)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}