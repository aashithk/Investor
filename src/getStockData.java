import java.io.*;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by aashithk on 9/26/15.
 */

class Row{
    Double month;
    Double date;
    Double open;
    Double high;
    Double low;
    Double close;
    Double volume;
    Double change;
    Row( Double month,Double date,Double open,Double high,Double low,Double close,Double volume)
    {
        this.date = date;
        this.month = month;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }
    void calculateChange()
    {
        change = close - open;
    }
    @Override
    public String toString()
    {
        return (month+","+date+","+ open+","+high+","+ low+ ","+close+ ","+volume+","+ change);
    }
}


public class getStockData {

    //   http://ichart.finance.yahoo.com/table.csv?s=AAPL&c=1962


    static ArrayList<Row> getStockData(String symbol) {
        ArrayList<Row> res = new ArrayList<Row>();

        try {
            URL myURL = new URL("http://ichart.finance.yahoo.com/table.csv?s="+symbol+"&g=d&c=1962");
            InputStream stream = myURL.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(myURL.openStream()));
            String line=null;
            ArrayList<String> arr = new ArrayList<String>();

            int current = 0;
            while ((line = reader.readLine()) != null) {
                if(current == 0)
                {
                    current++;
                    continue;
                }
                arr.add(line);
            }

            for( String s :  arr) {

                String[] tokens = s.split(",");
                String [] d = tokens[0].split("-");
                Double month=Double.parseDouble(d[1]);
                Double date=Double.parseDouble(d[2]);
                Double open=Double.parseDouble(tokens[1]);
                Double high=Double.parseDouble(tokens[2]);
                Double low=Double.parseDouble(tokens[3]);
                Double close=Double.parseDouble(tokens[4]);
                Double volume=Double.parseDouble(tokens[5]);
                Row each = new Row(month,date,open,high,low,close,volume);
                each.calculateChange();
                res.add(each);
            }
            System.out.print(res.size()+"  ");

        }
        catch(Exception e)
        {

            System.out.print(e.toString());
        }

        return res;
    }



    static void getAllSymbolsData()
    {
        try{
            BufferedReader br = new BufferedReader(new FileReader("data/tickers.csv"));

            String sCurrentLine;
            int record= 0;
            while ((sCurrentLine = br.readLine()) != null) {
                String [] arr = sCurrentLine.split(",");
                String ticker = arr[0].substring(1, arr[0].length() - 1);
                ArrayList<Row> temp = getStockData(ticker);
                if( temp.size()> 90) {
                    PrintWriter writer = new PrintWriter("data/stockData/"+ticker, "UTF-8");
                    for(Row row: temp) {
                        writer.println(row);
                    }
                    writer.close();
                    System.out.println(++record + "  " + arr[0].substring(1, arr[0].length() - 1) + "  " + temp.size());
                }
            }
        }
        catch(Exception e)
        {
            System.out.print("");
        }

    }
    public static void main(String[] args)
    {

        // example.getStockData("AAPL");
        // getAllSymbolsData();


    }
}

