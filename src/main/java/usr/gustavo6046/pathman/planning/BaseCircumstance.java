package usr.gustavo6046.pathman.planning;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class BaseCircumstance extends Circumstance
{
	private LinkedList<Condition> conditions;
	
	public BaseCircumstance(PlanSpace _space)
	{
		super(_space);
		conditions = new LinkedList<>();
	}

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
