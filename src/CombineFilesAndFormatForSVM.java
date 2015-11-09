import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

/**
 * Created by aashithk on 11/7/2015.
 */
public class CombineFilesAndFormatForSVM {

    /*
     * Convert all files to the input format for the Support vector machine.
     * Do this sector wise based on the observation that each sector seems to move together and
     * adhere to specific behavior each season.
     */
    public static void combineAllFilesInSVMInputFormat()
    {
        int counter = 0;
        try {
            String sCurrentLine = null;
            BufferedReader sbr = new BufferedReader(new FileReader("data/predictionProcessingData.txt"));
           // PrintWriter writer = new PrintWriter("data/AllTrainingData", "UTF-8");
            PrintWriter [] writer = new PrintWriter[214];
            for( int i=1;i<=writer.length;i++)
            {
                writer[i-1] = new PrintWriter("data/FinalTrainingData/TrainingFile"+i, "UTF-8");
            }
            while ((sCurrentLine = sbr.readLine()) != null) {
                String[] row = sCurrentLine.split(",");
                String ticker = row[0];
                int sector= Integer.parseInt(row[1]);
                System.out.println(++counter+" : "+ticker);
                try {
                    String dCurrentLine = null;
                    BufferedReader dbr = new BufferedReader(new FileReader("data/cleanedFilteredStockData/"+ticker));
                    while ((dCurrentLine = dbr.readLine()) != null) {
                        String[] arr = dCurrentLine.split(",");
                        StringBuilder sb = new StringBuilder("");
                        sb.append(arr[0]);
                        for( int i=1;i<arr.length;i++)
                        {
                            if(i < 8 )
                            {
                                sb.append(" "+i+":"+arr[i]);
                            }
                            else if(i == 8)
                            {
                                continue;
                            }
                            else
                            {
                                int j= i-1;
                                sb.append(" "+j+":"+arr[i]);
                            }

                        }
                        writer[sector-1].println(sb.toString());

                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
            for( int i=1;i<=writer.length;i++)
            {
                writer[i-1].close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    /*
     * Convert all the test data into the input format for the SVM and keep this stock wise
     * unlike the training data since we do not want to loose information about the actual price
     * after normalization. This makes it quite messy to regain the prices and calculate
     * performance of the automated investor.
     */
    public static void TestFilesInSVMInputFormat()
    {
        int counter = 0;
        try {
            String sCurrentLine = null;
            BufferedReader sbr = new BufferedReader(new FileReader("data/predictionProcessingData.txt"));
            // PrintWriter writer = new PrintWriter("data/AllTrainingData", "UTF-8");
            PrintWriter writer = null;
            while ((sCurrentLine = sbr.readLine()) != null) {
                String[] row = sCurrentLine.split(",");
                String ticker = row[0];
                int sector= Integer.parseInt(row[1]);
                System.out.println(++counter+" : "+ticker);
                try {
                    String dCurrentLine = null;
                    File f = new File("data/cleanedFilteredTestStockData/" + ticker);
                    if(!f.exists()) {
                        continue;
                    }
                    BufferedReader dbr = new BufferedReader(new FileReader("data/cleanedFilteredTestStockData/"+ticker));
                    writer = new PrintWriter("data/FinalTestData/"+ticker, "UTF-8");
                    while ((dCurrentLine = dbr.readLine()) != null) {
                        String[] arr = dCurrentLine.split(",");
                        StringBuilder sb = new StringBuilder("");
                        sb.append(arr[0]);
                        for( int i=1;i<arr.length;i++)
                        {
                            if(i < 8 )
                            {
                                sb.append(" "+i+":"+arr[i]);
                            }
                            else if(i == 8)
                            {
                                continue;
                            }
                            else
                            {
                                int j= i-1;
                                sb.append(" "+j+":"+arr[i]);
                            }

                        }
                        writer.println(sb.toString());

                    }
                    writer.close();

                }
                catch (Exception e)
                {
                    if(writer != null)
                    {
                        writer.close();
                    }
                    e.printStackTrace();
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public static void main(String []args)
    {
       // combineAllFilesInSVMInputFormat();
        TestFilesInSVMInputFormat();

    }
}
