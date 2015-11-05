import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import weka.associations.Apriori;
import weka.classifiers.functions.MultilayerPerceptron;

/**
 * Created by aashithk on 10/30/2015.
 */
public class CleanAndFormat {

    Double month;
    Double date;
    Double open;
    Double high;
    Double low;
    Double close;
    Double volume;
    Double change;

    //Function read from file
    //Format :  (month+","+date+","+ open+","+high+","+ low+ ","+close+ ","+volume+","+ change)

    static boolean readFile()
    {
        try {
            BufferedReader br = new BufferedReader(new FileReader("data/tickers.csv"));

            String sCurrentLine;
            int limit = -1;
            while ((sCurrentLine = br.readLine()) != null) {
                limit++;
                {
                    if(limit == 5 )
                    {
                        return true;
                    }
                }
                String[] arr = sCurrentLine.split(",");
                String ticker = arr[0].substring(1, arr[0].length() - 1);
                BufferedReader sbr = new BufferedReader(new FileReader("data/stockData/" + ticker));
                String dCurrentLine;
                System.out.println(ticker+" started");
                while ((dCurrentLine = sbr.readLine()) != null) {
                    String[] row = dCurrentLine.split(",");
                        for(String temp: row)
                        {
                            System.out.print(temp+" ");
                        }
                    System.out.println();
                }

            }
        }
        catch(Exception e)
        {
            System.out.print("");
        }

        return true;
    }

    public static void main(String []args)
    {
        readFile();
    }
}
