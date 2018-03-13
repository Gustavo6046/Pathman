package usr.gustavo6046.pathman;

import java.security.KeyException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import usr.gustavo6046.pathman.pathing.NodeLink;
import usr.gustavo6046.pathman.pathing.PathNode;
import usr.gustavo6046.pathman.pathing.PathSpace;
import usr.gustavo6046.pathman.planning.Action;
import usr.gustavo6046.pathman.planning.BaseCircumstance;
import usr.gustavo6046.pathman.planning.Circumstance;
import usr.gustavo6046.pathman.planning.Circumstance.Matcher;
import usr.gustavo6046.pathman.planning.Condition;
import usr.gustavo6046.pathman.planning.PlanSpace;

/**
 * @author gustavo6046
 *
 *         The PSM (Plan Space Manager) is an abstraction which goal is to ease
 *         the use of the key PathMan features, that are most likely to appear
 *         in AI projects or even simple games.
 */
public class Manager
{
	protected PlanSpace						planSpace;
	private HashMap<String, Circumstance>	keyCircumstances;

	/**
	 * Constructs a new Manager. Not a special function.
	 */
	public Manager()
	{
		planSpace = new PlanSpace();
		keyCircumstances = new HashMap<>();
	}

	/**
	 * Registers a circumstance to the Plan Space, and may also set it to a key.
	 * 
	 * @param other
	 *            The circumstance to register.
	 * @param key
	 *            A key (if any) to label the circumstance in the manager.
	 */
	public void addCircumstance(Circumstance other, Optional<String> key)
	{
		planSpace.addCircumstance(other);

		if ( key.isPresent() )
			keyCircumstances.put(key.get(), other);
	}

	/**
	 * Creates and registers a BaseCircumstance, i.e., a circumstance that has no
	 * special characteristics or inheritance whatsoever. Only use when you don't
	 * need Circumstance integration, or you do it by exporting the Circumstances
	 * instead of implementing them!
	 * 
	 * @param key
	 *            A key (if any) to label the new circumstance in the manager.
	 * @param conditions
	 *            The Conditions with which to create the new Circumstance. Prefer
	 *            to use Condition.builder() to generate the list.
	 * @return The new Circumstance. It is automatically registered, so you don't
	 *         need to Add it.
	 */
	public BaseCircumstance makeBaseCircumstance(Optional<String> key, Collection<Condition> conditions)
	{
		BaseCircumstance bc = new BaseCircumstance(planSpace);
		bc.addAllConditions(conditions);

		if ( key != null )
			addCircumstance(bc, key);

		return bc;
	}

	/**
	 * Register an Action, a verb that may change a matching Circumstance to a
	 * result one.
	 * 
	 * @param other
	 *            The Action to register.
	 */
	public void addAction(Action other)
	{
		planSpace.addAction(other);
	}

	/**
	 * Creates an Action and registers it.
	 * 
	 * @param prerequisite
	 *            The prerequisite Matcher with which Circumstances must match in
	 *            order to connect outwardly to this Action.
	 * @param result
	 *            The Circumstance that results from "doing" this Action.
	 * @param cost
	 *            The Dijkstra base cost of doing this Action.
	 * @return The action made. It is automatically registered, so you don't need to
	 *         Add it.
	 */
	public Action makeAction(Matcher prerequisite, Circumstance result, double cost)
	{
		Action act = new Action(planSpace, prerequisite, result, cost);

		addAction(act);
		return act;
	}

	/**
	 * Builds the final Action path, using the Path Space. This version uses a key
	 * String and supports only one goal Circumstance.
	 * 
	 * @param beginKey
	 *            The key of the first Circumstance.
	 * @param endKey
	 *            The key of the goal Circumstance.
	 * @return The list of Actions needed to reach the goal Circumstance, or
	 *         Optional.empty() if it can't be reached.
	 * @throws ClassNotFoundException
	 * @throws KeyException
	 */
	public Optional<List<Action>> buildPlan(String beginKey, String endKey) throws ClassNotFoundException, KeyException
	{
		if ( !( keyCircumstances.containsKey(beginKey) || keyCircumstances.containsKey(endKey) ) )
			throw new KeyException("Invalid begin or end key passed to (" + toString() + ").buildPlan !");

		PathSpace pt = new PathSpace(keyCircumstances.get(beginKey));
		LinkedList<PathNode> dests = new LinkedList<>();
		dests.add((PathNode) ( pt.checkResultFor(keyCircumstances.get(endKey)).get() ));
		Optional<List<NodeLink>> links = pt.linkPath(dests);

		if ( !links.isPresent() )
			return Optional.empty();

		LinkedList<Action> acts = new LinkedList<>();

		for ( NodeLink l : links.get() )
			acts.add((Action) l.origin);

		return Optional.of(acts);
	}

	/**
	 * Builds the final Action path, using the Path Space. This version uses a
	 * Matcher and supports multiple goal Circumstances.
	 * 
	 * @param beginKey
	 *            The key of the first Circumstance.
	 * @param goalMatcher
	 *            The Matcher with which match the goal Circumstances.
	 * @return The list of Actions needed to reach the goal Circumstance, or
	 *         Optional.empty() if it can't be reached.
	 * @throws ClassNotFoundException
	 * @throws KeyException
	 */
	public Optional<List<Action>> buildPlan(String beginKey, Circumstance.Matcher goalMatcher)
			throws ClassNotFoundException, KeyException
	{
		LinkedList<Circumstance> goalCircs = new LinkedList<>();

		for ( Circumstance c : planSpace.allCircumstances )
			if ( goalMatcher.matches(c) )
				goalCircs.add(c);

		if ( !keyCircumstances.containsKey(beginKey) || goalCircs.size() < 1 )
			throw new KeyException(
					"Invalid begin key, or no matching goals, passed to (" + toString() + ").buildPlan !");

		LinkedList<PathNode> goalNodes = new LinkedList<>();
		PathSpace pt = new PathSpace(keyCircumstances.get(beginKey));

		for ( Circumstance c : goalCircs )
			goalNodes.add((PathNode) pt.checkResultFor(c).get());

		Optional<List<NodeLink>> links = pt.linkPath(goalNodes);

		if ( !links.isPresent() )
			return Optional.empty();

		LinkedList<Action> acts = new LinkedList<>();

		for ( NodeLink l : links.get() )
			acts.add((Action) l.origin);

		return Optional.of(acts);
	}

	/**
	 * 
	 * Finds the key that sets this Circumstance, if any. Only the first key set to
	 * it counts.
	 * 
	 * @param result
	 *            The Circumstance to which this key is set.
	 * @return The key, or Optional.empty() if none is found.
	 */
	public Optional<String> findKey(Circumstance result)
	{
		if ( keyCircumstances.containsValue(result) )
			for ( HashMap.Entry<String, Circumstance> e : keyCircumstances.entrySet() )
				if ( e.getValue() == result )
					return Optional.of(e.getKey());

		return Optional.empty();
	}

	/**
	 * Gets the Circumstance to which this key is set.
	 * 
	 * @param key
	 *            The key that sets this Circumstance.
	 * @return The Circumstance, or Optional.empty() if none is found.
	 */
	public Optional<Circumstance> findForKey(String key)
	{
		if ( keyCircumstances.containsKey(key) )
			return Optional.of(keyCircumstances.get(key));

		return Optional.empty();
	}
}
