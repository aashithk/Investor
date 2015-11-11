import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by aashithk on 11/10/2015.
 */
class Compare{
    double high;
    double close;
}
public class TestAlgorithm {


    public static  double testDataWithAlgorithm1(String ticker, PrintWriter eachRow)
    {
        // Create 5 seperate dollar investments to prevent over compounding.
        double [] investment = new double[5];
        double result = 0;
        for ( int i=0;i<investment.length;i++)
        {
            investment[i]= 1.0;
        }
        int investmentCount = 0;
        double missCount =0;
        double successCount = 0;
        try {
            File f = new File("data/TestStockData/" + ticker);
            if (!f.exists()) {
                result = 0;
                for( int i =0;i<investment.length;i++)
                {
                    result += investment[i];
                }
                result = result/5;
                eachRow.println(ticker +","+result+","+successCount+","+missCount);

                return result;

            }
            BufferedReader sbr = new BufferedReader(new FileReader("data/TestStockData/" + ticker));
            String sCurrentLine;
            ArrayList<Compare> current = new ArrayList<>();
            while ((sCurrentLine = sbr.readLine()) != null) {
                String[] row = sCurrentLine.split(",");
                Compare in = new Compare();
                in.high = Double.parseDouble(row[3]);
                in.close = Double.parseDouble(row[5]);
                current.add(in);
            }
            f = new File("data/FinalPredictions/" + ticker+".output");
            if(!f.exists()) {
                result = 0;
                for( int i =0;i<investment.length;i++)
                {
                    result += investment[i];
                }
                result = result/5;
                eachRow.println(ticker +","+result+","+successCount+","+missCount);

                return result;

            }
            BufferedReader dbr = new BufferedReader(new FileReader("data/FinalPredictions/" + ticker+".output"));
            String dCurrentLine = null;
            int count = -1;

            while((dCurrentLine = dbr.readLine()) != null)
            {
                count++;
                String [] arr = dCurrentLine.split(",");
                double target = Double.parseDouble(arr[0]);
                double today = current.get(count+5).close;
                // Quick check for share splits or combine outliers which might throw off the testing.
                if( (today*.45) > current.get(count).close || (today*2) < current.get(count).close)
                {
                    continue;
                }
                // Quick check for bankruptcy with price hitting 0 .
                if( today <= 0.0 )
                {
                    result = 0;
                    for( int i =0;i<investment.length;i++)
                    {
                        result += investment[i];
                    }
                    result = result/5;
                    eachRow.println(ticker +","+result+","+successCount+","+missCount);

                    return result;

                }
                double predictedProfit = ((target - today)/today)*100;
                if(predictedProfit < 0)
                {
                continue;
                }
                // Check from the next day if the target price is hit.
                boolean hit = false;
                for( int i = count+4 ;i >= count ;i--)
                {
                    if(current.get(i).high > target)
                    {
                        hit = true;
                        investment[investmentCount] *= (1 + ( predictedProfit/100 ));
                        investmentCount = (investmentCount + 1) % 5;
                        successCount++;
                        break;
                    }

                }
                if( ! hit)
                {
                    missCount++;
                    investment[investmentCount] *= (1 + ((current.get(count).close - today)/today));
                    investmentCount = (investmentCount + 1) % 5;
                }

            }
        } catch (Exception e) {
                e.printStackTrace();
            }
        /*
         * For each processed row compare the five prices earlier in the arraylist which is the prices for the
         * next five days.
         */
        result = 0;
        for( int i =0;i<investment.length;i++)
        {
            result += investment[i];
        }
        result = result/5;
        eachRow.println(ticker +","+result+","+successCount+","+missCount);

        return result;


        }
    public static void main(String []args)
    {
        try {
            double totalProfit = 0.0;
            PrintWriter eachRow  = new PrintWriter("data/Algorithm1TestResults.output","UTF-8");
            BufferedReader sbr = new BufferedReader(new FileReader("data/predictionProcessingData.txt"));
            String sCurrentLine = null;
            int count=0;
            while(( sCurrentLine = sbr.readLine()) != null) {
                String[] arr = sCurrentLine.split(",");
                String ticker = arr[0];
            //    String ticker = "SAX.BE";
                totalProfit+=testDataWithAlgorithm1(ticker,eachRow);
                System.out.println(++count + " : " + ticker + " : " + totalProfit);
            }
            eachRow.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
