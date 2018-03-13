package usr.gustavo6046.pathman;

public class ConsoleInt
{
	public static final int demonum = 2;
	
	public static final String[] demokeys = {
			"castle",
			"grid"
	};
	
	@SuppressWarnings("rawtypes")
	public static final Class[] demovals = {
			usr.gustavo6046.pathman.PathmanDemo.class,
			usr.gustavo6046.pathman.PMGridPathDemo.class
	};
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws NoSuchMethodException, SecurityException
	{
		for ( int i = 0; i < demonum; i++ )
			if ( demokeys[i] == args[2].toLowerCase() )
			{
				demovals[i].getMethod("main", String[].class);
				return;
			}
		
		System.out.println("No such demo found! Possible demokeys:");
		System.out.print(demokeys[0]);
		
		for ( int i = 1; i < demonum; i++ )
			System.out.print(" - " + demokeys[i]);
		
		System.out.println();
	}
}
