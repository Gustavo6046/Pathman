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

/**
 * @author gustavo6046
 *
 *         Any Action that may be performed in order to alter a Circumstance or
 *         set the current one.
 */
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
	/**
	 * Forms a Partial Action, ie, an action that simpĺy changes one or more
	 * Conditins of an origin Circumstance and sets that as a result.
	 * 
	 * @param _space
	 *            The current Plan Space.
	 * @param prerequisite
	 *            The prerequisites to perform this new "Partial" Action.
	 * @param origin
	 *            The origin to change.
	 * @param change
	 *            The change to perform to the origin.
	 * @param cost
	 *            The cost of the new "Partial" Action.
	 * @return The new "Partial" Action. You can get the new node from
	 *         Action.result.
	 */
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

	/**
	 * Forms a Partial Action, ie, an action that simpĺy changes one or more
	 * Conditins of an origin Circumstance and sets that as a result.
	 * 
	 * @param _space
	 *            The current Plan Space.
	 * @param prerequisite
	 *            The prerequisites to perform this new "Partial" Action.
	 * @param origin
	 *            The origin to change.
	 * @param changes
	 *            The Circumstance with the changes to perform to the origin.
	 * @param cost
	 *            The cost of the new "Partial" Action.
	 * @return The new "Partial" Action. You can get the new node from
	 *         Action.result.
	 */
	public static Action partialAction(PlanSpace _space, Matcher prerequisite, Circumstance origin,
			Circumstance changes, double cost)
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

	/* (non-Javadoc)
	 * @see usr.gustavo6046.pathman.pathing.LinkRepr#asLink(usr.gustavo6046.pathman.pathing.PathSpace)
	 */
	@Override
	public NodeLink asLink(PathSpace other) throws ClassNotFoundException
	{
		PathNode resnode = (PathNode) other.convert(result);

		return new NodeLink(resnode, cost);
	}

	/* (non-Javadoc)
	 * @see usr.gustavo6046.pathman.pathing.BaseRepresentative#alsoConvert()
	 */
	@Override
	public List<BaseRepresentative> alsoConvert()
	{
		LinkedList<BaseRepresentative> res = new LinkedList<>();
		res.add(result);
		return res;
	}
}
