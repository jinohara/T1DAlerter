package com.example.research;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by Fishy on 6/30/2015.
 */
public class PredictionMethods {

    public static int[] predict() {
        /*
        Steps to predict:
        0. Put all the old results in an int[] array.
        1. Vectorize main input data (break into overlapping chunks
           that are longer than the current chunk by the amount you want to predict)
           Q: How long should the chunks be?
        2. Find the beginning of the chunks that best match the current chunk by
           calculating the distance sqrt((a2-a1)^2 + (b2-b1)^2 + (c2-c1)^2 + ...)
        3. Then you'll end up with a data set where you can find:
            a. An average value (graph as a midpoint)
            b. An upper and lower extreme (disregard)
            c. 1st and 3rd quartile values (we'll use these as an error)
        4. The method will then return the
            a. average,
            b. 1st quartile, and
            c. 3rd quartile values
            in that order in an int[].
         */

        chunkify();
        return null;
    }

    public static void chunkify() {
        /*
        Chunklength = Length of chunk searched for + predicted chunk
         */

        // First, get results from GraphActivity.result
        Object[] objChunks = GraphActivity.result.toArray();
        int[] base = new int[objChunks.length];
        for (int i = 0; i < objChunks.length; i++) {
            try {
                base[i] = Integer.parseInt(objChunks[i].toString().split(":")[9].split(",")[0].replaceAll("\\s+", ""));
            } catch (NumberFormatException e) {
                base[i] = 0;
            }
        }
        // Now we have int[] base, length 1000-ish, with all the results (erroneous results are marked as 0).
        // We can break int[] base into int[][] chunks, the first (x-) value being the chunk index and second (y-) values being the chunk coords in n-dimensional space
        int n = 18;
        int[][] chunks = new int[base.length - n][n];
        for (int i = 0; i < (base.length - n) - 1; i++) {
            // for each index, fill chunks
            for (int j = 0; j < n; j++) {
                chunks[i][j] = base[i + j];
            }
        }

        // Find the current chunk
        final double data[] = GraphActivity.methodObject.getDataSGV(GraphActivity.last11,
                GraphActivity.result.get(0));

        // Reorganize the current chunk into [0-11]
        int[] currentchunk = new int[12];
        for (int i = 1; i < 12; i++) // Use 1-11 -> 0-10 + 11
        {
            currentchunk[i - 1] = (int) Math.round(data[i]);
        }
        currentchunk[11] = (int) Math.round(data[0]);
        int[] tempchunk = new int[12];

        // Now find all the distances between the current chunk and other chunks
        double[] distances = new double[base.length - n];
        for (int i = 0; i < chunks.length - 1; i++) {
            // scan each index of chunks

            // get compared chunk
            for (int j = 6; j < 18; j++) // the last 12 values of the n-length chunk (more recent values come first)
            {
                tempchunk[j - 6] = chunks[i][j];
            }

            // apply findDistance between compared chunk and regular chunk
            distances[i] = findDistance(currentchunk, tempchunk);
        }

        // Now we have the distances from current chunk to other chunks in distances[]
        // i is the index of (double)distance between (int)chunks[i][j]'s last 12 values and the current chunk
        // Consolidate all that for the sake of laziness into an array of simple objects

        ConsolidatedDistance[] consolidatedAr = new ConsolidatedDistance[distances.length];
        for (int i = 0; i < distances.length; i++) {
            consolidatedAr[i] = new ConsolidatedDistance();
            consolidatedAr[i].distance = distances[i];
            consolidatedAr[i].chunksindex = i;
            consolidatedAr[i].actualChunk = chunks[i];
        }

        Arrays.sort(consolidatedAr);


        // Take the top prediction and roll with it (for now)
        String aaa = "";
        for (int i = 0; i < consolidatedAr[1].actualChunk.length; i++)
        {
            aaa += consolidatedAr[1].actualChunk[i] + ", ";
        }
        Log.d("Test", aaa);
    }

    //Finds the distance between two n-dimensional points
    private static double findDistance(int[] vector1, int[] vector2) {
        double tempresult = 0;
        for (int i = 0; i < vector1.length; i++) {
            tempresult += (Math.pow((vector2[i] - vector1[i]),2));
        }
        tempresult = Math.sqrt(Math.abs(tempresult));
        return tempresult;
    }
}