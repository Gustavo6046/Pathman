package usr.gustavo6046.pathman.planning;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import usr.gustavo6046.pathman.pathing.BaseRepresentative;
import usr.gustavo6046.pathman.pathing.LinkRepr;
import usr.gustavo6046.pathman.pathing.NodeLink;
import usr.gustavo6046.pathman.pathing.PathNode;
import usr.gustavo6046.pathman.pathing.PathSpace;
import usr.gustavo6046.pathman.planning.Circumstance.Matcher;

public class Action implements LinkRepr
{
	public Circumstance.Matcher	prerequisite;
	public Circumstance			result;
	public double				cost;
	public PlanSpace			space;

	public Action(PlanSpace _space, Matcher prerequisite, Circumstance result, double cost)
	{
		super();
		this.prerequisite = prerequisite;
		this.result = result;
		this.cost = cost;
		space = _space;
	}

	// Only supports base circumstances as output.
	public static Action partialAction(PlanSpace _space, Matcher prerequisite, Circumstance origin, Condition change,
			double cost)
	{
		BaseCircumstance newc = new BaseCircumstance(_space);
		boolean changedEx = false;

		for ( Condition c : origin.getConditions() )
			if ( c.key == change.key )
			{
				changedEx = true;
				newc.addCondition(change);
			}

			else
				newc.addCondition(c);

		if ( !changedEx )
			newc.addCondition(change);

		if ( prerequisite == null )
			prerequisite = Matcher.equality(origin);

		return new Action(_space, prerequisite, newc, cost);
	}

	public static Action partialAction(PlanSpace _space, Matcher prerequisite, Circumstance origin, Circumstance changes,
			double cost)
	{
		BaseCircumstance newc = new BaseCircumstance(_space);
		HashSet<Condition> changedEx = new HashSet<>();

		for ( Condition c : origin.getConditions() )
			for ( Condition change : changes.getConditions() )
				if ( c.key == change.key )
				{
					changedEx.add(change);
					newc.addCondition(change);
				}
	
				else
					newc.addCondition(c);

		for ( Condition change : changes.getConditions() )
			if ( !changedEx.contains(change) )
				newc.addCondition(change);

		if ( prerequisite == null )
			prerequisite = Matcher.equality(origin);

		return new Action(_space, prerequisite, newc, cost);
	}

	@Override
	public NodeLink asLink(PathSpace other) throws ClassNotFoundException
	{
		PathNode resnode = (PathNode) other.convert(result);

		return new NodeLink(resnode, cost);
	}

	@Override
	public List<BaseRepresentative> alsoConvert()
	{
		LinkedList<BaseRepresentative> res = new LinkedList<>();
		res.add(result);
		return res;
	}
}
