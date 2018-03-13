package usr.gustavo6046.pathman;

/**
 * @author gustavo6046
 * 
 *         ConsoleInt. The console interface that allows the access to multiple
 *         PathMan demos at once. Should not be used directly in a common
 *         library environment!
 */
public class ConsoleInt
{
	public static final int			demonum		= 2;

	/**
	 * The demokeys that can be supplied as console arguments to run the demos.
	 */
	public static final String[]	demokeys	= { "castle", "grid" };

	/**
	 * The actual classes the demokeys link to.
	 */
	@SuppressWarnings("rawtypes")
	public static final Class[]		demovals	= { usr.gustavo6046.pathman.PathmanDemo.class,
			usr.gustavo6046.pathman.PMGridPathDemo.class };

	/**
	 * The console interface's entry point.
	 * 
	 * @param args
	 *            Console arguments.
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
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
