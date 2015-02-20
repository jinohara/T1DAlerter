package com.example.research;

class DexComReading
{
    private int sgvVal;
    private String direction;    
    private int readTime;
    public DexComReading(int sgvVal_, String slope_, int readTime_)
    {
        sgvVal=sgvVal_;
        direction=slope_;
        readTime=readTime_;
    }
    public int getSgv()
    {
        return sgvVal;
    }
    public double getDoubleSgv()
    {
        return (double)sgvVal;
    }
    public String getSlope()
    {
        return direction;
    }
    public int getTime()
    {
        return readTime;
    }

    public int strToSgvVal(String bigString, int dateStrIdx)
    {
        int sgvIndex = bigString.indexOf("sgv", dateStrIdx);
        int commaIndex = bigString.indexOf(",", sgvIndex);
        int colonIndex = bigString.indexOf(":", sgvIndex);
        int sgvReading = Integer.parseInt(bigString.substring(colonIndex+2, commaIndex-1));
        return sgvReading;
    }
    public String strToDirection(String bigString, int dateStrIdx)
    {
        int dirIdx = bigString.indexOf("direction", dateStrIdx);
        int colonIdx = bigString.indexOf(":", dateStrIdx);
        int commaIdx = bigString.indexOf(",", dateStrIdx); 
        String direction = bigString.substring(colonIdx+3, commaIdx-2);
        return direction;
    }
    public int strToTime(String bigString, int dateStrIdx)
    {
        int firstT = bigString.indexOf("T", dateStrIdx+8);
        int colonPostT = bigString.indexOf(":", firstT);
        String hour = bigString.substring(firstT+1, colonPostT);
        int intHour = Integer.parseInt(hour);
        return intHour;
    }
}
