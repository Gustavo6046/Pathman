package usr.gustavo6046.pathman;

import java.security.KeyException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import usr.gustavo6046.pathman.exceptions.InfiiniteLoopException;
import usr.gustavo6046.pathman.pathing.NodeLink;
import usr.gustavo6046.pathman.pathing.PathNode;
import usr.gustavo6046.pathman.pathing.PathSpace;
import usr.gustavo6046.pathman.planning.Action;
import usr.gustavo6046.pathman.planning.BaseCircumstance;
import usr.gustavo6046.pathman.planning.Circumstance;
import usr.gustavo6046.pathman.planning.Circumstance.Matcher;
import usr.gustavo6046.pathman.planning.Condition;
import usr.gustavo6046.pathman.planning.PlanSpace;

public class Manager
{
	protected PlanSpace						planSpace;
	private HashMap<String, Circumstance>	keyCircumstances;

	public Manager()
	{
		planSpace = new PlanSpace();
		keyCircumstances = new HashMap<>();
	}

	public void addCircumstance(Circumstance other, Optional<String> key)
	{
		planSpace.addCircumstance(other);

		if ( key.isPresent() )
			keyCircumstances.put(key.get(), other);
	}

	public BaseCircumstance makeBaseCircumstance(Optional<String> key, Collection<Condition> conditions)
	{
		BaseCircumstance bc = new BaseCircumstance(planSpace);
		bc.addAllConditions(conditions);

		if ( key != null )
			addCircumstance(bc, key);
		
		return bc;
	}

	public void addAction(Action other)
	{
		planSpace.addAction(other);
	}

	public Action makeAction(Matcher prerequisite, Circumstance result, double cost)
	{
		Action act = new Action(planSpace, prerequisite, result, cost);
		
		addAction(act);
		return act;
	}

	public Optional<List<Action>> buildPlan(String beginKey, String endKey) throws ClassNotFoundException, KeyException, InfiiniteLoopException
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

	public Optional<List<Action>> buildPlan(String beginKey, Circumstance.Matcher goalMatcher)
			throws ClassNotFoundException, KeyException, InfiiniteLoopException
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

	public Optional<String> findKey(Circumstance result)
	{
		if ( keyCircumstances.containsValue(result) )
			for ( HashMap.Entry<String, Circumstance> e : keyCircumstances.entrySet() )
				if ( e.getValue() == result )
					return Optional.of(e.getKey());
		
		return Optional.empty();
	}
}
