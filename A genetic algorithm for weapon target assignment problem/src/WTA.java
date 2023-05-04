import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.*;


public class WTA {
	
	static class weapon
	{
	    public String Name; 
	    public int    cnt;
	    
	    public  weapon(String Name , int cnt)
		{
			this.Name = Name;
			this.cnt = cnt;
			
		}
	   
	    
	};
	
	
	
	static int weapon_total=0 ;
	static double Total_Treat  =0;
	static int Num_targets  ;
	static int X = 0 ; // number  of weapons types 
	static double[][]  prob = new double[100][100];
	static ArrayList<weapon> Weapons = new ArrayList<weapon>();
    static int[] weapon_list  = new int[100000];
//	static ArrayList<int[]> Pop = new ArrayList<int[]>();
	static Random rand = new Random();


    
    
    public static int[] Make_chromosome()
    {
    	int len = weapon_total; // length of the chromosome
    	int[] c = new int[len];
    	
    	for(int i = 0 ; i < len; ++i)
    	{
    		c[i] = rand.nextInt(Num_targets);  // random number between 0 , Num_targets-1
    	}
    	return c;
    	
    }
	public static ArrayList<int[]> initializ(int size)
	{
		ArrayList<int[]> Pop = new ArrayList<int[]>();
		
         for(int i = 0 ;i < size ; ++i)
         {
        	 int[] chromosome = Make_chromosome();
        	 Pop.add( chromosome );
         }
         return Pop;
	}
	
	
	 
	
	public static double[] fitness(ArrayList<int[]>Pop , int size , double[] threats)
	{
		double[] fit = new double[size];
		double p = 0;
		
		for(int i = 0 ; i < size ; ++i)
		{
			int[] c = Pop.get(i);
			double threat = 0; 
			double [] tmp_threats = Arrays.copyOf(threats, threats.length);
			for(int j = 0 ;j < c.length ; ++j)
            {
				int w = weapon_list[j]; // i-th weapon
				int t = c[j];           // j-th target
				p = prob[w][t];         // probabilty of the i-th weapon to minimize j-th target
				 
				// System.out.print(tmp_threats[t] + " ");
				 System.out.print(t + " ");
				if( (tmp_threats[t] - (tmp_threats[t] * p)) >= 0)
				{
					double xx = (tmp_threats[t] * p);
					tmp_threats[t] = (tmp_threats[t] - xx) ;
				}
				else tmp_threats[t] = 0;
				 
			}
			 System.out.println(i+"");

			for(int  x = 0 ; x <tmp_threats.length ; ++x )
				      threat +=tmp_threats[x];
			
			 //System.out.println(threat);

			
			fit[i] = threat;
		}
		return fit;  // fitness for each chromosome in the population
		
	}
	
	public static int selection(ArrayList<int[]>Pop ,double[] fit ,int size  )
	{
	
		double [] norm = Arrays.copyOf(fit, fit.length);
		
		for(int i = 0 ; i < size ; ++i)
	         norm[i]  = (1/norm[i]);   // inverse the fitness so the highest is the smallest
		
		double sum = DoubleStream.of(norm).sum();
		
		for(int i = 0 ; i < size ; ++i)
	         norm[i] /=sum;
		
		sum = 0;
		double r = rand.nextDouble();
		
		int s  = 0;
		for(int i = 0 ; i < size ; ++i)
		{
			sum += norm[i];
			
			if(r <= sum) s = i;
		}
		
		return s;
		
	}
	
	public static int[] crossOver(ArrayList<int[]>Pop , int s1 , int s2 , double Pc , int k)
	{
		
		int[] p1 = Pop.get(s1) , p2 = Pop.get(s2); // parents
		int[] offspring = new int[p1.length]; 
		 double r = rand.nextDouble();

		
		 if(r <= Pc)
		 {
		 System.arraycopy(offspring, 0, p1, 0, k); 
		 System.arraycopy(offspring, k, p2, k, p2.length - k); 
		 }

		 else System.arraycopy(offspring, 0, p1, 0, p1.length); 
		 
         return offspring;

		
		
	}
	
	public static int[] mutation(int[] c, double Pm  )
	{
		
		for(int i = 0 ; i < c.length ; ++i)
		{
			double r = rand.nextDouble();
			if(r <= Pm) {
				
			 int rc = rand.nextInt(Num_targets);
			 c[i] = rc;
			}
		}
		return c;
		  
	}
	
	public static void print_sol(int [] sol)
	{
		  
		System.out.println("The final WTA solution is:");
		
		for(int i = 0 ; i < sol.length ; ++i)
		{
			int target = sol[i];  // targe assined to the i-th weapon
			int wp = weapon_list[i]; // i-th weapon;
			//weapon w1 = new weapon(Weapons.get(wp).Name ,Weapons.get(wp).cnt );
			weapon w1 = Weapons.get(wp);
			System.out.println(w1.Name + "is assigned to target #" +target );
		}
		
	}
	public static void main(String[] args) {
		
		
		
		
		
		System.out.println("Enter the weapon types and the number of instances of each type: (Enter\n" + 
				"“x” when you’re done)");
		
		String type = "";
		int n ;
	    //weapon w  ;
    	Scanner scan =  new Scanner(System.in);
    
		while( true )
		{  
			
			type = scan.next();
			
			if(type.equals("x"))
			{
				break;
			}
		    n = scan.nextInt();
		   // System.out.print(n);
		    
		    weapon w = new weapon(type,n);
		    
		    Weapons.add(w);
		    weapon_total += n;
		    X++;
		}
		
		//System.out.print(weapon_total);
		
		int k = 0;
		for(int i = 0 ;  i < X ; ++i)
		{
			weapon w1  = Weapons.get(i);
			//System.out.println(w1.cnt);
			for(int j = 0 ; j < w1.cnt ; ++j)
			{
				weapon_list[k] = i;
				k++;
			}
			
			
		}
		
		System.out.println("Enter the number of targets: ");
		
		Num_targets = scan.nextInt();
		
		System.out.println("Enter the threat coefficient of each target: ");
		
		double[] threats = new double[Num_targets];
		
		for(int i = 0 ; i < Num_targets ; ++i)
		{
			threats[i] = scan.nextInt();
			Total_Treat += threats[i];
		}
        
		
		System.out.println("Enter the weapons’ success probabilities matrix: ");

       // double[][]  prob = new double[X][Num_targets]; 
        
        for(int i = 0 ; i < X ; ++i)
        {
        	for(int j = 0 ; j < Num_targets ; ++j)
        	{
        		prob[i][j] = scan.nextDouble();
        	}
        }
        
        
        
		System.out.println("Please wait while running the GA.......");
		
        int gen = 200;
        int size = 100;
        ArrayList<int[]> Pop = new ArrayList<int[]>();
        
        
        Pop = initializ(size);
        
        
        
    

       
        
        
        
      /*  for(int i = 0 ; i < size ; ++i)
        {
        	int arr[] = Pop.get(i);
        	for(int j = 0 ; j < weapon_total ; ++j)
        	{
        	    System.out.print(arr[j] + " ");
        	}
    		System.out.print(" " + fit[i]);
    		System.out.println("");
        	
        }*/
        
        
        while(gen > 0)
        {
        	
        double[] fit = fitness(Pop , size , threats);
    
		int Point =  rand.nextInt(weapon_total); // point of cross over
        
        int s1 = selection(Pop, fit , size);
        int s2 = selection(Pop, fit , size);
        
        double Pc = rand.nextDouble() ; // probability of cross-over
        double Pm = rand.nextDouble() ;  // probability of mutation
        

        
       int[] offspring1 =  crossOver(Pop , s1 , s2 , Pc , Point );
       int[] offspring2 =  crossOver(Pop , s2 , s1 , Pc , Point );
        
       offspring1 = mutation(offspring1 , Pm);
       offspring2 = mutation(offspring2 , Pm);
       
       
       // final step is replacment 
       
       Pop.set(s1, offspring1);
       Pop.set(s2, offspring2);
        	
        	gen--;
        	
       }
        
    double[] fit = fitness(Pop , size , threats);
   
    double mn = fit[0];
    int pos = 0;
    for(int  i = 0 ; i < fit.length; ++i)
    {
    	if(mn < fit[i])
    	{
    		mn = fit[i];
    		pos = i;
    	}
    }
    print_sol(Pop.get(pos));
    
    


		
    
		

		

	}

}
