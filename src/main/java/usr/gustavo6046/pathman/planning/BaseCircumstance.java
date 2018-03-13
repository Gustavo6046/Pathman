package usr.gustavo6046.pathman.planning;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author gustavo6046
 *
 *         A generic Circumstance that does not integrate or depend in, or store
 *         data about a specific game or context.
 */
public class BaseCircumstance extends Circumstance
{
	private LinkedList<Condition> conditions;
	
	public BaseCircumstance(PlanSpace _space)
	{
		super(_space);
		conditions = new LinkedList<>();
	}

	/* (non-Javadoc)
	 * @see usr.gustavo6046.pathman.planning.Circumstance#getConditions()
	 */
	@Override
	public List<Condition> getConditions()
	{
		return conditions;
	}
	
	public void addCondition(Condition other)
	{
		conditions.add(other);
	}

	public void addAllConditions(Collection<Condition> other)
	{
		conditions.addAll(other);
	}
}
