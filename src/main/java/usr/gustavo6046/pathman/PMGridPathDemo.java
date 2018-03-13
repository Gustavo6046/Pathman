package usr.gustavo6046.pathman;

import java.security.KeyException;
import java.util.List;
import java.util.Optional;

import usr.gustavo6046.pathman.exceptions.InfiiniteLoopException;
import usr.gustavo6046.pathman.planning.Action;
import usr.gustavo6046.pathman.planning.Circumstance;
import usr.gustavo6046.pathman.planning.Condition;

public class PMGridPathDemo extends DemoClass
{
	// Relatively simple grid-based Dijkstra search
	// using PathMan. Note that this is NOT advised!
	// You should do the search FIRST, and THEN in-
	// corporate the resulting path as a possible
	// action to do in order to integrate with
	// PathMan.

	public static final int[] neighCoord = { 1, 0, 0, 1, -1, 0, 0, -1, };

	public static void main(String[] args) throws ClassNotFoundException, KeyException, InfiiniteLoopException
	{
		Manager man = new Manager();
		Condition.Builder cbuilder = Condition.builder();

		/*
		 * Circumstance origin = man.makeBaseCircumstance( Optional.of("0,0"), cbuilder
		 * .addCondition("coord", 0) .build() );
		 */

		Circumstance[] grid = new Circumstance[900];

		for ( int y = 0; y < 30; y++ )
			for ( int x = 0; x < 30; x++ )
				grid[y * 30 + x] = man.makeBaseCircumstance(Optional.of(x + "," + y),
						cbuilder.addCondition("coord", x, y).build());

		for ( int y = 0; y < 30; y++ )
			for ( int x = 0; x < 30; x++ )
				for ( int i = 0; i < 3; i++ )
				{
					int nx = neighCoord[i * 2] + x;
					int ny = neighCoord[i * 2 + 1] + y;

					if ( nx < 0 || nx > 29 || ny < 0 || ny > 29 )
						continue;

					man.makeAction(Circumstance.matcher().addEqual("coord", x, y).build(),
							man.findForKey(nx + "," + ny).get(), 1);
				}

		List<Action> acts = man.buildPlan("13,2", "3,21").get();

		System.out.print("13,2");

		for ( Action a : acts )
			System.out.print(" -> " + man.findKey(a.result).get());
		
		System.out.println();
	}
}
