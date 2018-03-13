package usr.gustavo6046.pathman;

import java.security.KeyException;
import java.util.List;
import java.util.Optional;

import usr.gustavo6046.pathman.exceptions.InfiiniteLoopException;
import usr.gustavo6046.pathman.planning.Action;
import usr.gustavo6046.pathman.planning.Circumstance;
import usr.gustavo6046.pathman.planning.Condition;

public class PathmanDemo
{
	public static void main(String[] args) throws ClassNotFoundException, KeyException, InfiiniteLoopException
	{
		Manager 			man			= new Manager();
		Condition.Builder	cbuilder	= Condition.builder();
		
		// We begin invading no castle flank (0), and we must pick
		// one flank that will best bring us victory.
		
		// Background:
		// "Informers tell the flank with the least defense is
		// the 3rd one, but it goes through a longer and narrower
		// path until reaching the Throne Room, so it might be more
		// dangerous. That's why some of us are considering the 2nd
		// flank, that has a few more troops, but nearly no path until
		// the Throne Room - it's almost like placing a ladder, going
		// through the window, and done."
		
		// In normal game code conditions, you want to make your game's
		// current scene to inherit Circumstance and do partial changes
		// to that using Action.partialAction, following the actual
		// possible actions your army can take, but since this is just
		// a demo, we will instead just create our own arbitrary
		// circumstances and actions.
		
		// Follows the preferred (quickest and easiest) way to
		// make a circumstance, when not using a custom
		// inheriting class.
		Circumstance beginning = man.makeBaseCircumstance(
				Optional.of("beginning"),
				cbuilder
				.addCondition("castleFlank", 0)
				.addCondition("distance", 50)
				.addCondition("dead", 0)
				.addCondition("invading", 0)
				.build()
		);
		
		Circumstance f1 = man.makeBaseCircumstance( // You can also store these!
			Optional.empty(), // Entering Flank 1. Not all circumstances need names.
			cbuilder
				.addCondition("castleFlank", 1)
				.addCondition("distance", 5)
				.addCondition("dead", 1)
				.addCondition("invading", 1)
				.build()
		);
		
		Circumstance f2 = man.makeBaseCircumstance(
			Optional.of("invade flank 2"), // Entering Flank 2.
			cbuilder
				.addCondition("castleFlank", 2)
				.addCondition("distance", 1.75)
				.addCondition("dead", 0)
				.addCondition("invading", 1)
				.build()
		);
		
		Circumstance f3 = man.makeBaseCircumstance(
			Optional.of("invade flank 3"), // Entering Flank 3.
			cbuilder
				.addCondition("castleFlank", 3)
				.addCondition("distance", 7)
				.addCondition("dead", 0)
				.addCondition("invading", 1)
				.build()
		);
		
		Circumstance f4 = man.makeBaseCircumstance(
			Optional.of("invade flank 4"), // Entering Flank 4.
			cbuilder
				.addCondition("castleFlank", 4)
				.addCondition("distance", 12)
				.addCondition("dead", 1)
				.addCondition("invading", 1)
				.build()
		);
		
		Circumstance change = man.makeBaseCircumstance(
				// Providing 'null' instead of an empty (or any) Optional
				// ensures the Circumstance isn't added to the plan space,
				// so you can reuse it without fear.
				null,
				cbuilder
					.addCondition("distance", 0)
					.addCondition("invading", 1)
					.build()
		);
		
		// This is where we put those stored circumstances at use.
		Action f2_post = Action.partialAction(
			man.planSpace,
			null, // use origin equality as matcher
			f2, 
			change,
			2.5
		);
			
		Action f3_post = Action.partialAction(
			man.planSpace,
			null, // use origin equality as matcher
			f3, 
			change,
			7
		);
		
		man.addAction(f2_post);
		man.addAction(f3_post);
		
		man.addCircumstance(
			f2_post.result, // Getting the modified Circumstance
							// from the partial action
			Optional.of("victory! (flank 2)") // Going through Flank 2
		);

		// Don't worry with there being multiple victory
		// paths; our Dijkstra algorithm will pick the
		// best one :)
		man.addCircumstance(
			f3_post.result,
			Optional.of("victory! (flank 3)") // Going through Flank 3
		);
		
		// Now, let's connect the beginning action to the
		// four flank circumstances we defined earlier.
		// Our smart manager automatically registers these
		// connections!
		man.makeAction(Circumstance.Matcher.equality(beginning), f1, 8);
		man.makeAction(Circumstance.Matcher.equality(beginning), f2, 5);
		man.makeAction(Circumstance.Matcher.equality(beginning), f3, 4);
		man.makeAction(Circumstance.Matcher.equality(beginning), f4, 8);
		
		// Almost done, now we must define the goal.
		// (remember that Matcher's classnames begin uppercase,
		// but Matcher builders are lowercase!)
		Circumstance.Matcher goal = Circumstance.matcher()
				.addEqual("distance", 0)
				.build();
		
		// And now, we can finally use the manager to find the actions
		// we need. It 'manages' all the internal search details for us!
		List<Action> acts = man.buildPlan("beginning", goal).get();
		
		// Let's print to the terminal the steps we need!
		System.out.println(man.findKey(beginning).get()); // "beginning"
		
		for ( Action a : acts )
			System.out.println(man.findKey(a.result).get());
	}
}
