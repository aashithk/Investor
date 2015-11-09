import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import weka.associations.Apriori;
import weka.classifiers.functions.MultilayerPerceptron;

/**
 * Created by aashithk on 10/30/2015.
 */
class SvmInput {
    Double month;
    Double date;
    Double open;
    Double high;
    Double low;
    Double close;
    Double volume;
    Double sector;
    Double exchange;
    Double highAve5;
    Double lowAve5;
    Double closeAve5;
    Double highAve30;
    Double lowAve30;
    Double closeAve30;

    Double output;

    public String toString()
    {
        return (output+","+month+","+date+","+open+","+high+","+low+","+close+","+
                volume+","+sector+","+exchange+","+highAve5+","+lowAve5+","+closeAve5+","+highAve30+","+
                lowAve30+","+closeAve30);
    }

/* Last stopped here :
Next Steps :
>Calculate the output variable and also each of the trailing averages and add rows into respective files.
>After this, append the each file into a single file.
>Generate the model with all this data and calculate the mean squared error as well as hits on
several current cases with the respective data.
>Use target hit to measure accuracy of the product as well as over all profit with assumption of transaction close at end of week.
If possible:
>Modify above to get stop loss of each stock as well and now check the hit ratio of stop loss/ profit along with actual profit
 */
}


    /*
    * Clean and Normalize the entire datset by finding the maximum and the minimum
    * of each column and ensuring all values are in the 0 to 1 range before processing.
    */
public class CleanAndFormat {



    //Function read from file
    //Format :  (month+","+date+","+ open+","+high+","+ low+ ","+close+ ","+volume+","+ change)

    static boolean cleanAndNormalizeData()
    {
        PrintWriter writer = null;
        int count = 0;
        try {
            writer = new PrintWriter("data/predictionProcessingData.txt","UTF-8");
            BufferedReader br = new BufferedReader(new FileReader("data/validTickers.txt"));
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                String[] arr = sCurrentLine.split(",");
                String ticker = arr[0];
                Double sector = Double.parseDouble(arr[1]);
                Double exchange = Double.parseDouble(arr[2]);
                PrintWriter eachRow = new PrintWriter("data/cleanedFilteredStockData/"+ticker,"UTF-8");

                try {
                    BufferedReader sbr = new BufferedReader(new FileReader("data/stockData/" + ticker));
                    String dCurrentLine;
                    System.out.println(++count+" : "+ ticker + " started");
                    ArrayList<SvmInput> current = new ArrayList<>();
                    ArrayList<SvmInput> finList = new ArrayList<>();

                    while ((dCurrentLine = sbr.readLine()) != null) {
                        String[] row = dCurrentLine.split(",");
                        SvmInput in = new SvmInput();
                        in.month = Double.parseDouble(row[0]);
                        in.date = Double.parseDouble(row[1]);
                        in.open = Double.parseDouble(row[2]);
                        in.high = Double.parseDouble(row[3]);
                        in.low = Double.parseDouble(row[4]);
                        in.close = Double.parseDouble(row[5]);
                        in.volume = Double.parseDouble(row[6]);
                        in.sector = sector;
                        in.exchange = exchange;
                        current.add(in);
                    }
                    //Double tmp
                  // calculate the averages here & assign sector code and the number code of exchange

                    for(int i=5;i<current.size()-30;i++)
                    {
                        Double tmpHighAve5 = 0.0;
                        Double tmpLowAve5 = 0.0;
                        Double tmpCloseAve5 = 0.0;
                        Double tmpHighAve30 = 0.0;
                        Double tmpLowAve30 = 0.0;
                        Double tmpCloseAve30 = 0.0;
                        Double tmpOutput = 0.0;

                        SvmInput curr = current.get(i);
                        tmpOutput = current.get(i-5).high;
                        for( int j =i-4;j<i;j++)
                        {
                            tmpOutput = Math.max(current.get(j).high,tmpOutput);
                        }

                        for( int j =i+1;j<=i+30;j++)
                        {
                            tmpCloseAve30 += current.get(j).close;
                            tmpLowAve30 += current.get(j).low;
                            tmpHighAve30 += current.get(j).high;
                        }

                        for( int j =i+1;j<=i+5;j++)
                        {
                            tmpCloseAve5 += current.get(j).close;
                            tmpLowAve5 += current.get(j).low;
                            tmpHighAve5 += current.get(j).high;
                        }

                        curr.closeAve30 = tmpCloseAve30/30;
                        curr.highAve30 = tmpHighAve30/30;
                        curr.lowAve30 = tmpLowAve30/30;

                        curr.highAve5 = tmpHighAve5/5;
                        curr.lowAve5 = tmpLowAve5/5;
                        curr.closeAve5 = tmpCloseAve5/5;

                        curr.output = tmpOutput;

                        finList.add(curr);
                    }

                    //Normalize the dataset

                    Double monthMin = Double.MAX_VALUE,monthMax= Double.MIN_VALUE;
                    Double dateMin = Double.MAX_VALUE, dateMax= Double.MIN_VALUE;
                    Double openMin = Double.MAX_VALUE,openMax= Double.MIN_VALUE;
                    Double highMin= Double.MAX_VALUE,highMax= Double.MIN_VALUE;
                    Double lowMin= Double.MAX_VALUE,lowMax= Double.MIN_VALUE;
                    Double closeMin= Double.MAX_VALUE,closeMax= Double.MIN_VALUE;
                    Double volumeMin= Double.MAX_VALUE,volumeMax= Double.MIN_VALUE;
                    Double highAve5Min= Double.MAX_VALUE,highAve5Max= Double.MIN_VALUE;
                    Double lowAve5Min= Double.MAX_VALUE,lowAve5Max= Double.MIN_VALUE;
                    Double closeAve5Min= Double.MAX_VALUE,closeAve5Max= Double.MIN_VALUE;
                    Double highAve30Min= Double.MAX_VALUE,highAve30Max= Double.MIN_VALUE;
                    Double lowAve30Min= Double.MAX_VALUE,lowAve30Max= Double.MIN_VALUE;
                    Double closeAve30Min= Double.MAX_VALUE,closeAve30Max= Double.MIN_VALUE;

                    Double outputMin = Double.MAX_VALUE,outputMax = Double.MIN_VALUE;

                    // Sector and Exchange Min and Max are global and not specific to a stock.
                    Double sectorMin= 1.0 ,sectorMax= 214.0;
                    Double exchangeMin= 1.0,exchangeMax= 38.0;


                    for(SvmInput in : finList)
                    {
                        monthMin = Math.min(in.month,monthMin);monthMax= Math.max(in.month,monthMax);
                        dateMin = Math.min(in.date,dateMin); dateMax= Math.max(in.date,dateMax);
                        openMin = Math.min(in.open,openMin);openMax= Math.max(in.open,openMax);
                        highMin= Math.min(in.high,highMin);highMax= Math.max(in.high,highMax);
                        lowMin= Math.min(in.low,lowMin);lowMax= Math.max(in.low,lowMax);
                        closeMin= Math.min(in.close,closeMin);closeMax= Math.max(in.close,closeMax);
                        volumeMin= Math.min(in.volume,volumeMin);volumeMax= Math.max(in.volume,volumeMax);
                        highAve5Min= Math.min(in.highAve5,highAve5Min);highAve5Max= Math.max(in.highAve5,highAve5Max);
                        lowAve5Min= Math.min(in.lowAve5,lowAve5Min);lowAve5Max= Math.max(in.lowAve5,lowAve5Max);
                        closeAve5Min= Math.min(in.closeAve5,closeAve5Min);closeAve5Max= Math.max(in.closeAve5,closeAve5Max);
                        highAve30Min= Math.min(in.highAve30,highAve30Min);highAve30Max= Math.max(in.highAve30,highAve30Max);
                        lowAve30Min= Math.min(in.lowAve30,lowAve30Min);lowAve30Max= Math.max(in.lowAve30,lowAve30Max);
                        closeAve30Min= Math.min(in.closeAve30,closeAve30Min);closeAve30Max= Math.max(in.closeAve30,closeAve30Max);

                        outputMin=Math.min(in.output,outputMin);outputMax = Math.max(in.output,outputMax);
                    }

                    // Adjust for 0 volume entries
                    if(volumeMax == volumeMin )
                    {
                        volumeMax++; // Divisor is not 1 and prevents devide by 0 while ensuring final number is either 0 or
                    }

                    for(int i=0;i<finList.size();i++)
                    {
                        SvmInput adj = finList.get(i);

                        adj.month = (adj.month - monthMin )/ (monthMax - monthMin);
                        adj.date = (adj.date - dateMin )/ (dateMax - dateMin);
                        adj.open = (adj.open - openMin )/ (openMax - openMin);
                        adj.high = (adj.high - highMin )/ (highMax - highMin);
                        adj.low = (adj.low - lowMin )/ (lowMax - lowMin);
                        adj.close = (adj.close - closeMin )/ (closeMax - closeMin);
                        adj.volume = (adj.volume - volumeMin )/ (volumeMax - volumeMin);
                        adj.sector = (adj.sector - sectorMin )/ (sectorMax - sectorMin);
                        adj.exchange = (adj.exchange - exchangeMin )/ (exchangeMax - exchangeMin);
                        adj.highAve5 = (adj.highAve5 - highAve5Min )/ (highAve5Max - highAve5Min);
                        adj.lowAve5 = (adj.lowAve5 - lowAve5Min )/ (lowAve5Max - lowAve5Min);
                        adj.closeAve5 = (adj.closeAve5 - closeAve5Min )/ (closeAve5Max - closeAve5Min);
                        adj.highAve30 = (adj.highAve30 - highAve30Min )/ (highAve30Max - highAve30Min);
                        adj.lowAve30 = (adj.lowAve30 - lowAve30Min )/ (lowAve30Max - lowAve30Min);
                        adj.closeAve30 = (adj.closeAve30 - closeAve30Min )/ (closeAve30Max - closeAve30Min);
                        adj.output = (adj.output - outputMin )/ (outputMax - outputMin);

                        eachRow.println(adj);

                        //Next Step :
                        // Get all the ranges used for normalizing and store them for processing test data.


                    }


                    writer.println(sCurrentLine+","+outputMin + "," + outputMax +"," + monthMin + "," + monthMax + "," +
                            dateMin + "," + dateMax + "," + openMin + "," +openMax + "," + highMin + "," + highMax + "," +
                            lowMin + "," + lowMax + "," + closeMin + "," + closeMax + "," +volumeMin +"," + volumeMax+"," +
                            sectorMin + "," + sectorMax + "," + exchangeMin + "," + exchangeMax + "," +
                            highAve5Min + "," + highAve5Max +"," + lowAve5Min + "," + lowAve5Max +"," +
                            closeAve5Min + "," +closeAve5Max + "," + highAve30Min + "," +highAve30Max + "," +
                            lowAve30Min + ","+lowAve30Max + ","+closeAve30Min +","+ closeAve30Max);


                    System.out.println(ticker+" done.");

                }
                catch (Exception e)
                {
                    System.out.print(" File not found or Double convert error" + e.getLocalizedMessage());
                }
                eachRow.close();
            }
            writer.close();

        }
        catch(Exception e)
        {
            if(writer !=null)
            {
                writer.close();
            }
            System.out.print(" Error in main ticker read");
            e.printStackTrace();
        }

        return true;
    }


        /*
        * Clean and Normalize the test dataset using the minimum and maximum values stored in the
        * predictionProcessingData.txt file.
         */
    static boolean cleanAndNormalizeTestData()
    {
        int count = 0;
        PrintWriter eachRow = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader("data/predictionProcessingData.txt"));
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                String[] arr = sCurrentLine.split(",");
                String ticker = arr[0];
                try {
                    File f = new File("data/TestStockData/" + ticker);
                    if(!f.exists()) {
                        continue;
                    }
                    Double sector = Double.parseDouble(arr[1]);
                    Double exchange = Double.parseDouble(arr[2]);
                    BufferedReader sbr = new BufferedReader(new FileReader("data/TestStockData/" + ticker));
                    String dCurrentLine;
                    System.out.println(++count+" : "+ ticker + " started");
                    ArrayList<SvmInput> current = new ArrayList<>();
                    ArrayList<SvmInput> finList = new ArrayList<>();

                    while ((dCurrentLine = sbr.readLine()) != null) {
                        String[] row = dCurrentLine.split(",");
                        SvmInput in = new SvmInput();
                        in.month = Double.parseDouble(row[0]);
                        in.date = Double.parseDouble(row[1]);
                        in.open = Double.parseDouble(row[2]);
                        in.high = Double.parseDouble(row[3]);
                        in.low = Double.parseDouble(row[4]);
                        in.close = Double.parseDouble(row[5]);
                        in.volume = Double.parseDouble(row[6]);
                        in.sector = sector;
                        in.exchange = exchange;
                        current.add(in);
                    }
                    //Double tmp
                    // calculate the averages here & assign sector code and the number code of exchange

                    for(int i=5;i<current.size()-30;i++)
                    {
                        Double tmpHighAve5 = 0.0;
                        Double tmpLowAve5 = 0.0;
                        Double tmpCloseAve5 = 0.0;
                        Double tmpHighAve30 = 0.0;
                        Double tmpLowAve30 = 0.0;
                        Double tmpCloseAve30 = 0.0;
                        Double tmpOutput = 0.0;

                        SvmInput curr = current.get(i);
                        tmpOutput = current.get(i-5).high;
                        for( int j =i-4;j<i;j++)
                        {
                            tmpOutput = Math.max(current.get(j).high,tmpOutput);
                        }

                        for( int j =i+1;j<=i+30;j++)
                        {
                            tmpCloseAve30 += current.get(j).close;
                            tmpLowAve30 += current.get(j).low;
                            tmpHighAve30 += current.get(j).high;
                        }

                        for( int j =i+1;j<=i+5;j++)
                        {
                            tmpCloseAve5 += current.get(j).close;
                            tmpLowAve5 += current.get(j).low;
                            tmpHighAve5 += current.get(j).high;
                        }

                        curr.closeAve30 = tmpCloseAve30/30;
                        curr.highAve30 = tmpHighAve30/30;
                        curr.lowAve30 = tmpLowAve30/30;

                        curr.highAve5 = tmpHighAve5/5;
                        curr.lowAve5 = tmpLowAve5/5;
                        curr.closeAve5 = tmpCloseAve5/5;

                        curr.output = tmpOutput;

                        finList.add(curr);
                    }

                    //Normalize the Test dataset using stored values in predictionProcessingData file.

                    /*(ticker+sector+exchange+outputMin+outputMax+monthMin+ monthMax +dateMin +dateMax +openMin +openMax +
                     highMin + highMax +lowMin + lowMax +closeMin + closeMax +volumeMin +volumeMax+sectorMin +sectorMax +
                      exchangeMin +exchangeMax +highAve5Min + highAve5Max + lowAve5Min + lowAve5Max + closeAve5Min +
                       closeAve5Max + highAve30Min +highAve30Max + lowAve30Min + lowAve30Max +closeAve30Min +closeAve30Max)
                    */
                    Double monthMin = Double.parseDouble(arr[5]),monthMax= Double.parseDouble(arr[6]);
                    Double dateMin =Double.parseDouble(arr[7]), dateMax= Double.parseDouble(arr[8]);
                    Double openMin = Double.parseDouble(arr[9]),openMax= Double.parseDouble(arr[10]);
                    Double highMin= Double.parseDouble(arr[11]),highMax= Double.parseDouble(arr[12]);
                    Double lowMin= Double.parseDouble(arr[13]),lowMax= Double.parseDouble(arr[14]);
                    Double closeMin= Double.parseDouble(arr[15]),closeMax= Double.parseDouble(arr[16]);
                    Double volumeMin=Double.parseDouble(arr[17]),volumeMax= Double.parseDouble(arr[18]);
                    Double highAve5Min= Double.parseDouble(arr[23]),highAve5Max= Double.parseDouble(arr[24]);
                    Double lowAve5Min= Double.parseDouble(arr[25]),lowAve5Max= Double.parseDouble(arr[26]);
                    Double closeAve5Min= Double.parseDouble(arr[27]),closeAve5Max= Double.parseDouble(arr[28]);
                    Double highAve30Min= Double.parseDouble(arr[29]),highAve30Max= Double.parseDouble(arr[30]);
                    Double lowAve30Min= Double.parseDouble(arr[31]),lowAve30Max= Double.parseDouble(arr[32]);
                    Double closeAve30Min= Double.parseDouble(arr[33]),closeAve30Max= Double.parseDouble(arr[34]);

                    Double outputMin = Double.parseDouble(arr[3]),outputMax = Double.parseDouble(arr[4]);

                    // Sector and Exchange Min and Max are global and not specific to a stock.
                    Double sectorMin= 1.0 ,sectorMax= 214.0;
                    Double exchangeMin= 1.0,exchangeMax= 38.0;


                    // Adjust for 0 volume entries
                    if(volumeMax == volumeMin )
                    {
                        volumeMax++; // Divisor is not 1 and prevents devide by 0 while ensuring final number is either 0 or
                    }

                    if(finList.size() == 0)
                    {
                        continue;
                    }
                    eachRow = new PrintWriter("data/cleanedFilteredTestStockData/"+ticker,"UTF-8");

                    for(int i=0;i<finList.size();i++)
                    {
                        SvmInput adj = finList.get(i);

                        adj.month = (adj.month - monthMin )/ (monthMax - monthMin);
                        adj.date = (adj.date - dateMin )/ (dateMax - dateMin);
                        adj.open = (adj.open - openMin )/ (openMax - openMin);
                        adj.high = (adj.high - highMin )/ (highMax - highMin);
                        adj.low = (adj.low - lowMin )/ (lowMax - lowMin);
                        adj.close = (adj.close - closeMin )/ (closeMax - closeMin);
                        adj.volume = (adj.volume - volumeMin )/ (volumeMax - volumeMin);
                        adj.sector = (adj.sector - sectorMin )/ (sectorMax - sectorMin);
                        adj.exchange = (adj.exchange - exchangeMin )/ (exchangeMax - exchangeMin);
                        adj.highAve5 = (adj.highAve5 - highAve5Min )/ (highAve5Max - highAve5Min);
                        adj.lowAve5 = (adj.lowAve5 - lowAve5Min )/ (lowAve5Max - lowAve5Min);
                        adj.closeAve5 = (adj.closeAve5 - closeAve5Min )/ (closeAve5Max - closeAve5Min);
                        adj.highAve30 = (adj.highAve30 - highAve30Min )/ (highAve30Max - highAve30Min);
                        adj.lowAve30 = (adj.lowAve30 - lowAve30Min )/ (lowAve30Max - lowAve30Min);
                        adj.closeAve30 = (adj.closeAve30 - closeAve30Min )/ (closeAve30Max - closeAve30Min);
                        adj.output = (adj.output - outputMin )/ (outputMax - outputMin);

                        eachRow.println(adj);

                        //Next Step :
                        // Get all the ranges used for normalizing and store them for processing test data.
                    }
                    eachRow.close();
                    System.out.println(ticker+" done.");
                }
                catch (Exception e)
                {
                    if(eachRow != null)
                    {
                        eachRow.close();
                    }
                    System.out.print(" File not found or Double convert error"+e.getLocalizedMessage());
                }
            }
        }
        catch(Exception e)
        {
            System.out.print(" Error in main ticker read");
            e.printStackTrace();
        }

        return true;
    }

    public static void main(String []args)
    {
        // cleanAndNormalizeData();
        // cleanAndNormalizeTestData();

    }
}
