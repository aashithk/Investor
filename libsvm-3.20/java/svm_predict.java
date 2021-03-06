import libsvm.*;
import java.io.*;
import java.util.*;

public class svm_predict {
	private static svm_print_interface svm_print_null = new svm_print_interface()
	{
		public void print(String s) {}
	};

	private static svm_print_interface svm_print_stdout = new svm_print_interface()
	{
		public void print(String s)
		{
			System.out.print(s);
		}
	};

	private static svm_print_interface svm_print_string = svm_print_stdout;

	static void info(String s) 
	{
		svm_print_string.print(s);
	}

	private static double atof(String s)
	{
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(String s)
	{
		return Integer.parseInt(s);
	}

	private static void predict(BufferedReader input, DataOutputStream output, svm_model model, int predict_probability,PrintWriter writer) throws IOException
	{
		int correct = 0;
		int total = 0;
		double error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

		int svm_type=svm.svm_get_svm_type(model);
		int nr_class=svm.svm_get_nr_class(model);
		double[] prob_estimates=null;

		if(predict_probability == 1)
		{
			if(svm_type == svm_parameter.EPSILON_SVR ||
			   svm_type == svm_parameter.NU_SVR)
			{
				System.out.println(svm_type);
				svm_predict.info("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="+svm.svm_get_svr_probability(model)+"\n");
			}
			else
			{
				int[] labels=new int[nr_class];
				svm.svm_get_labels(model,labels);
				prob_estimates = new double[nr_class];
				output.writeBytes("labels");
				for(int j=0;j<nr_class;j++)
					output.writeBytes(" "+labels[j]);
				output.writeBytes("\n");
			}
		}
		while(true)
		{
			String line = input.readLine();
			if(line == null) break;

			StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");

			double target = atof(st.nextToken());
			int m = st.countTokens()/2;
			svm_node[] x = new svm_node[m];
			for(int j=0;j<m;j++)
			{
				x[j] = new svm_node();
				x[j].index = atoi(st.nextToken());
				x[j].value = atof(st.nextToken());
			}

			double v;
			if (predict_probability==1 && (svm_type==svm_parameter.C_SVC || svm_type==svm_parameter.NU_SVC))
			{
				v = svm.svm_predict_probability(model,x,prob_estimates);
				output.writeBytes(v+" ");
				for(int j=0;j<nr_class;j++)
					output.writeBytes(prob_estimates[j]+" ");
				output.writeBytes("\n");
			}
			else
			{
				v = svm.svm_predict(model,x);
				output.writeBytes(v+"\n");
			}
			// System.out.println("Prediction: "+ v+" Target: "+ target);

			if(v == target)
				++correct;
			error += (v-target)*(v-target);
			sumv += v;
			sumy += target;
			sumvv += v*v;
			sumyy += target*target;
			sumvy += v*target;
			++total;
		}
		if(svm_type == svm_parameter.EPSILON_SVR ||
		   svm_type == svm_parameter.NU_SVR)
		{
			Double MSE = error/total;
			Double SCC =  ((total*sumvy-sumv*sumy)*(total*sumvy-sumv*sumy))/
					((total*sumvv-sumv*sumv)*(total*sumyy-sumy*sumy));
			writer.println(MSE+","+total+","+SCC);
			svm_predict.info("Mean squared error = "+error/total+" (regression)\n");
			svm_predict.info("Squared correlation coefficient = "+
				 ((total*sumvy-sumv*sumy)*(total*sumvy-sumv*sumy))/
				 ((total*sumvv-sumv*sumv)*(total*sumyy-sumy*sumy))+
				 " (regression)\n");
		}
		else
			svm_predict.info("Accuracy = "+(double)correct/total*100+
				 "% ("+correct+"/"+total+") (classification)\n");
	}

	private static void exit_with_help()
	{
		System.err.print("usage: svm_predict [options] test_file model_file output_file\n"
		+"options:\n"
		+"-b probability_estimates: whether to predict probability estimates, 0 or 1 (default 0); one-class SVM not supported yet\n"
		+"-q : quiet mode (no outputs)\n");
		System.exit(1);
	}

	public static void main(String argv[]) throws IOException {
		int i, predict_probability = 0;
		svm_print_string = svm_print_stdout;

		// parse options
		for (i = 0; i < argv.length; i++) {
			if (argv[i].charAt(0) != '-') break;
			++i;
			switch (argv[i - 1].charAt(1)) {
				case 'b':
					predict_probability = atoi(argv[i]);
					break;
				case 'q':
					svm_print_string = svm_print_null;
					i--;
					break;
				default:
					System.err.print("Unknown option: " + argv[i - 1] + "\n");
					exit_with_help();
			}
		}

		System.out.println("*************Here****************");

		/*
		if(i>=argv.length-2)
		exit_with_help();

		String test_file = argv[i];
		String model_file = argv[i+1];
		String output_file = argv[i+2];
		*/
		//String test_file = "testfile";
		//String model_file = "test.model";
		//String output_file = "test.output";

		// Next step : Write code to run prediction on all the test files
		// Then ensure that code in getActualPrice converts all files into their actual predicted value.
		// Use this predicted value to see if the target was hit in the next 5 days.
		// If so, count as profit. Otherwise take the closing price on 5th day - closing price on the given day as profit/loss.

		BufferedReader sbr = new BufferedReader(new FileReader("data/predictionProcessingData.txt"));
		PrintWriter writer = new PrintWriter("data/AccuracyMeasure.txt", "UTF-8");
		String sCurrentLine = null;
		while ((sCurrentLine = sbr.readLine()) != null) {

			String[] arr = sCurrentLine.split(",");
			String ticker = arr[0];
			String sector = arr[1];
			File f = new File("data/FinalTestData/" + ticker);
			if(!f.exists()) {
				continue;
			}
			String test_file = "FinalTestData/"+ticker;
			String model_file = "PredictionModel/Sector"+sector+"StockPrediction.model";
			String output_file = "TempPredictions/"+ticker+".output";
			try {
				BufferedReader input = new BufferedReader(new FileReader("data/" + test_file));
				DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("data/" + output_file)));
				svm_model model = svm.svm_load_model("data/" + model_file);
				if (model == null) {
					System.err.print("can't open model file " + argv[i + 1] + "\n");
					System.exit(1);
				}
				if (predict_probability == 1) {
					if (svm.svm_check_probability_model(model) == 0) {
						System.err.print("Model does not support probability estimates\n");
						System.exit(1);
					}
				} else {
					if (svm.svm_check_probability_model(model) != 0) {
						svm_predict.info("Model supports probability estimates, but disabled in prediction.\n");
					}
				}
				writer.print(ticker+",");
				System.out.println(ticker);
				predict(input, output, model, predict_probability, writer);
				input.close();
				output.close();
			} catch (FileNotFoundException e) {
				System.out.println(ticker);
				exit_with_help();
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println(ticker);
				exit_with_help();
			}


		}
		writer.close();
	}
}
