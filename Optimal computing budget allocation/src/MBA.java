import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.DoubleStream;
import java.io.*;




public class MBA {

    // Markting Budget Allocation Problem
	
	static class Channel
	{
		String name ;
	    double roi ;
	    double upper , lower;
	    
	    public Channel(String name , double roi)
	    {
	    	this.name = name;
	    	this.roi  = roi;
	    }
	    public Channel(double upper, double lower)
	    {
	    	this.upper = upper;
	    	this.lower = lower;
	    }
	}
	
	static ArrayList<Channel>  channels = new  ArrayList<Channel>();
	
	public static double[] Make_chromosome(int len , double budget)
	{
		Random r = new Random();
		
		double[] gene = new double[len];
		double tmp = 0;
		 double randomValue;
		for(int i = 0 ; i < len ; ++i)
		{
		Channel c = channels.get(i);
		
		if(c.upper <= budget)
		  randomValue = c.lower + (c.upper - c.lower) * r.nextDouble();
		
		else if(budget > c.lower) {
			
			  randomValue = c.lower + (budget - c.lower) * r.nextDouble();
		}
		else  randomValue = c.lower + (c.upper - c.lower) * r.nextDouble();
		
		tmp = randomValue;
		budget-= tmp;
		
		gene[i] = randomValue;
		}
		return gene;
	}
	
	public static ArrayList<double[]> initializ(int size , int channels_num , double budget)
	{
		ArrayList<double[]> Pop = new ArrayList<double[]>();
		
		for(int i = 0 ; i < size; ++i)
		{
			double[] chromosome = Make_chromosome(channels_num , budget);
			Pop.add(chromosome);
		}
		
		return Pop;
	}

	
	
	
	public static double calc_fitness(double[] c , double budget )
	{
		double income = 0;
		double check = 0;
		for(int i = 0 ; i < c.length ; ++i)
		{
			check += c[i];
		}
		if(check > budget)
			return 0;   // infeasible solution
		
		for(int i = 0 ; i < c.length ; ++i)
		{
			double ROI = channels.get(i).roi;
			income += (c[i]*(ROI/100));
		}
		
		return income;
	}
	
	public static double[]  Fitness_evaluation(ArrayList<double[]> Pop , int size , double budget)
	{
		double [] fit =  new double[size];
		
		for(int i = 0 ; i < size ; ++i)
		{
			fit[i] = calc_fitness(Pop.get(i) , budget);
		}
		return fit;
		
	}
	
	public static int selection(ArrayList<double[]>Pop ,double[] fit ,int size)
	{
		// tournament selection method
		
		Random rand = new Random();
		int k = 3 ; // three way tournament
		
		int[] arr =  new int [k];
		
		for(int i = 0 ;i < k ; ++i)
			arr[i] = rand.nextInt(size);
		
		double max = fit[arr[0]];
		int pos = 0;
		for(int i = 0 ;i < k ; ++i)
		{
			if(max < arr[i])
			{
				max = arr[i];
				pos = i;
			}
		}
		return pos;

	}
	public static double[] crossOver(double[] p1 , double[] p2 , double Pc , int k)
	{
		
		Random rand = new Random();
		double[] offspring = new double[p1.length];
		double r = rand.nextDouble();
		
		if(r <= Pc)
		{
			 System.arraycopy(offspring, 0, p1, 0, k); 
			 System.arraycopy(offspring, k, p2, k, k);
			 System.arraycopy(offspring, 2*k, p1, 2*k, p1.length - 2*k);

		}
		
		else return p1;
		
		
		return offspring ;
		
	}
	
	
	public static double[] uniformMutaion(double[] ch , double Pm)
	{
		Random rand = new Random();	
		double x = 0;
		for(int i = 0 ; i < ch.length ; ++i)
		{
			double r1 = rand.nextDouble();
			if(r1 <= Pm)
			{
				double lx = ch[i] - channels.get(i).lower;
				double ux = channels.get(i).upper - ch[i];
				double r2 = rand.nextDouble();
				
				if(r2 <= .5)x = -lx;
				else x = ux;
				
				double r3 = x*rand.nextDouble();
				ch[i] = ch[i] + x;
			}
		}
		
		return ch;
	}
	
	public static double[] non_uniformMutaion(double[] ch , double Pm , int t , int T , double b)
	{
		
		Random rand = new Random();	
		double x = 0;
		for(int i = 0 ; i < ch.length ; ++i)
		{
			double r1 = rand.nextDouble();
			if(r1 <= Pm)
			{
				double lx = ch[i] - channels.get(i).lower;
				double ux = channels.get(i).upper - ch[i];
				double r2 = rand.nextDouble();
				
				if(r2 <= .5)x = -lx;
				else x = ux;
				
				double r3 =  rand.nextDouble();
				double  y = x * (1 - Math.pow(r3, Math.pow((1-(t/T)), b)));
				ch[i] = ch[i] + y ;
			}
		}
		
		return ch;
	}
	
	
    public static ArrayList<double[]>  replacement(ArrayList<double[]>Pop , double fit[] , double[] offspring , double budget)
    {
    	double off_fitness = calc_fitness( offspring, budget);
    	
    	for(int i = 0 ; i < Pop.size() ; ++i)
    	{
    		if(fit[i] < off_fitness)
    		{
    			Pop.set(i, offspring);
    			fit[i] = off_fitness;
    			break;
    		}
    	}
    	
    	return Pop;
    	
    }
	
	public static void FileWrite(double[] best, double profit ,  String fileName)
	{

	        try {
	            // Assume default encoding.
	            FileWriter fileWriter =
	                new FileWriter(fileName);

	            BufferedWriter bufferedWriter =
	                new BufferedWriter(fileWriter);

	      
           	 //bufferedWriter.write("The final marketing budget allocation is: ");
	         //bufferedWriter.newLine();
		       // bufferedWriter.newLine();
	            for(int i = 0 ; i < best.length ; ++i)
	            {
		            bufferedWriter.write(String.format("%.4f", best[i]) + " ");

	            }
	            bufferedWriter.write(String.format("%.4f", profit) + "\n");
	            bufferedWriter.close();
	        }
	        catch(IOException ex) {
	            System.out.println(
	                "Error writing to file '"
	                + fileName + "'");
	            // Or we could just do this:
	            // ex.printStackTrace();
	        }
   }
	
	public static double[] FileReader(String fileName)
	{
		double [] sol =  new double[20];

		ArrayList<double[]> p = new ArrayList<double[]>();
		ArrayList<Double> c = new ArrayList<Double>();

        String line = null;
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            int I = 0;
            while((line = bufferedReader.readLine()) != null)  {
            	
              // double ans = Double.parseDouble(line.substring(0, line.length()-1));
        	    char[] str = line.toCharArray();
        	    String num = "";
        	    
            	for(int i =0 ; i < line.length() ; ++i)
            	{
            	    
            		if(str[i] == ' ' )
            		{
            			c.add(Double.parseDouble(num));
            			num = "";
            		}
            		else if (str[i] == '\n')
            		{
            			sol[I] = Double.parseDouble(num);
            		}
            		else num+=str[i];
            	}
            	
               if(I<20) I++;
               else break;
               
            }   

            // Always close files.
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }
		
		
		return sol;
		
	}

		
		
	public static void main(String[] args) {
		
		Scanner scan = new Scanner(System.in);

		
		System.out.print("Enter the marketing budget (in thousands): ");
		double budget = scan.nextDouble();
		
		System.out.print("Enter the number of marketing channels: ");
		int channels_num = scan.nextInt();
		
		System.out.println("Enter the name and ROI (in %) of each channel separated by space: ");
		int I = 0;
		
		while(I < channels_num)
		{
			String name  = scan.next();
			double roi  = scan.nextDouble();
			Channel c = new Channel(name , roi);
			channels.add(c);
			I++;
		}
		System.out.println("Enter the lower (k) and upper bounds (%) of investment in each channel:\n" + 
				"(enter x if there is no bound)");
		
		I = 0;
		while(I < channels_num)
		{
			Channel c = channels.get(I);
			String  l = scan.next();
			String u = scan.next();
			
			if(!l.equals("x"))
			{
				c.lower = Double.parseDouble(l);
			}
			else c.lower = 0;
			
			if(!u.equals("x"))
			{
				c.upper = Double.parseDouble(u);
			}
			else c.upper = budget;
			
			channels.set(I, c);
			I++;
		}
		
		System.out.println("Please wait while running the GA…");
		
		ArrayList<double[]> Pop = initializ(100 , 4 , budget);
		
		/*for(int i = 0  ; i < 100 ; ++i)
		{
			double [] d = Pop.get(i);
			
			for(int j = 0 ;j < d.length ; ++j )
				System.out.print(d[j] + " ");
			
			System.out.println(" " + fit[i]);
		}*/
		Random rand = new Random();	
	    int G = 200;
	    int g = 1;
        double b = -.5 + (5 - (-.5)) * rand.nextDouble();
        double Pm = rand.nextDouble();
        double Pc = rand.nextDouble();
        
	    while(g <= G )
	    {
			double [] fit  = Fitness_evaluation(Pop , 100 , budget);
			int s1 = selection(Pop,fit , 100);
			int s2 = selection(Pop,fit , 100);
			
			
			int Point =  2 ;//rand.nextInt(channels_num); // point of cross over
			
			double[] off1 = crossOver(Pop.get(s1) , Pop.get(s2) , Pc , Point);
			double[] off2 = crossOver(Pop.get(s2) , Pop.get(s1)  , Pc , Point);

           // off1 = uniformMutaion(off1 , Pm);
            //off2 = uniformMutaion(off2 , Pm);
            
            
    		
            off1 = non_uniformMutaion(off1 , Pm ,g , G ,b);
            off2 =non_uniformMutaion(off2 , Pm, g ,G , b);
             
            
           Pop =  replacement(Pop ,  fit ,  off1 ,  budget);
           Pop =  replacement(Pop ,  fit ,  off2 ,  budget);
	       g++;
	    	
	    }
		double [] fit  = Fitness_evaluation(Pop , 100 , budget);
		
		int pos = 0;
		double max = 0;
		for(int i = 0 ; i < fit.length ; ++i)
		{
			if(fit[i] > max)
			{
				pos = i ;
				max = fit[i];
			}
		}
		
		
		
		
		
		System.out.println("The final marketing budget allocation is:" );

		double [] best = Pop.get(pos);
		for(int i = 0 ; i  < channels_num ; ++i)
		{
			System.out.println(channels.get(i).name + "  ---> " + best[i] );
		}
		double income =  calc_fitness(best , budget);
		
		System.out.println("The total profit is : " + String.format("%.4f", income) +"k");
        
		FileWrite(best , income , "Non_uniform.txt");
		

		
		
		
	}
	
	

}







