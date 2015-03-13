package com.example.research;

import java.util.Vector;
import net.sf.javaml.core.Dataset;
class setsMeanStdDev 
{
    public Vector<Dataset> sets;
    public double stdDev;
    public double mean;
    public setsMeanStdDev(Vector<Dataset> setsIn, double meanIn, double stdDevIn)
    {
        this.sets = setsIn;
        this.mean = meanIn;
        this.stdDev = stdDevIn;
    }
}
