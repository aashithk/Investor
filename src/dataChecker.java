import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.io.File;

/**
 * Created by aashithk on 11/5/2015.
 */
public class dataChecker {

    /*
    * Clean the dataset of unwanted stocks with noway to get sector or exchange information
    * as well as stocks with too little data or inconsistent data.
    * Use this function to also remove stocks no longer traded.
    * Generate the sector and exchange code for the SVM here and log the same in a map file each.
    * Also generate the valid Ticker file to be used for future processing.
     */
    public static void getAllValidTickersWithExchangesAndSectors()
    {
        PrintWriter writer = null;
        HashMap<String,Integer> sec = new HashMap<>();
        HashMap<String,Integer> ex = new HashMap<>();
        int counter = 0;
        try {
        writer = new PrintWriter("data/validTickers.txt", "UTF-8");
        BufferedReader br = new BufferedReader(new FileReader("data/tickers.csv"));
        String sCurrentLine = null;
        while ((sCurrentLine = br.readLine()) != null) {
            counter++;
            String[] arr = sCurrentLine.split(",");
            String ticker = arr[0].substring(1, arr[0].length() - 1);
            int secIndex = arr.length - 1;
            int exIndex = arr.length - 3;
            if(arr[exIndex].equals("") || arr[secIndex].equals("0") || arr[secIndex].equals(""))
            {
                continue;
            }
            System.out.println(counter+" : "+arr[exIndex]+" : "+ arr[secIndex]);
            String exchange = arr[exIndex].substring(1,arr[exIndex].length() -1);
            String sector = arr[secIndex];
            if(sec.containsKey(sector))
            {
                sector = sec.get(sector).toString();
            }
            else
            {
                sec.put(sector,sec.size()+1);
                sector = sec.get(sector).toString();
            }
            if(ex.containsKey(exchange))
            {
                exchange = ex.get(exchange).toString();
            }
            else
            {
                ex.put(exchange,ex.size()+1);
                exchange = ex.get(exchange).toString();
            }
            try {

                File f = new File("data/stockData/" + ticker);
                if(f.exists() && !f.isDirectory()) {
                    writer.println(ticker+","+sector+","+exchange);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
            writer.close();

            System.out.println("Loading ExMap : "+ ex.size()+" & SectorMap : "+sec.size());
            writer = new PrintWriter("data/exchangeMap.txt", "UTF-8");
            for( String s : ex.keySet())
            {
                writer.println(s+","+ex.get(s));
            }
            writer.close();
            writer = new PrintWriter("data/sectorMap.txt", "UTF-8");
            for( String s : sec.keySet())
            {
                writer.println(s+","+sec.get(s));
            }
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if(writer != null)
            {
                writer.close();
            }
        }




    }

    public static void main(String [] args)
    {

        getAllValidTickersWithExchangesAndSectors();


    }




}
