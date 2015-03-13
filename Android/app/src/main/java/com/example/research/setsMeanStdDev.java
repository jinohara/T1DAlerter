import java.util.Vector;
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
