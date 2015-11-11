import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

/**
 * Created by aashithk on 11/8/2015.
 */
public class getActualPriceFromPredictions {

    /*
     * Convert all prices into their actual prices by removing preprocessing.
     * Use this data to calculate actual performance of the predictor in terms of hit ratio of the prediction.
     * Store the actual mean squared error too for reporting.
     * Write the actual performance calculation after writing the over all performance engine.
     * Presume investment if and only if prediction sees an x% gain.
     * Later calculate a gain in the stock.
     */
    public static void getPrices(String ticker, Double price1,Double price2)
    {

        try {
            String dCurrentLine = null;
            File f = new File("data/TempPredictions/" + ticker+".output");
            if(!f.exists()) {
                return;
            }
            BufferedReader dbr = new BufferedReader(new FileReader("data/TempPredictions/" + ticker +".output"));
            PrintWriter writer = new PrintWriter("data/FinalPredictions/" + ticker+".output", "UTF-8");
            while ((dCurrentLine = dbr.readLine()) != null) {
                double result = ((Double.parseDouble(dCurrentLine))*(price2 - price1))+price1;
                writer.println(result);
            }
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void main(String []args)
    {
        try {


            BufferedReader sbr = new BufferedReader(new FileReader("data/predictionProcessingData.txt"));
            String sCurrentLine = null;
            int count=0;
            while(( sCurrentLine = sbr.readLine()) != null) {
                String[] arr = sCurrentLine.split(",");
                String ticker = arr[0];
                Double price1 = Double.parseDouble(arr[3]);
                Double price2 = Double.parseDouble(arr[4]);
                getPrices(ticker, price1, price2);
                System.out.println(++count+" : "+ticker);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
