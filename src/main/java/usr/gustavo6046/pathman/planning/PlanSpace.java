package usr.gustavo6046.pathman.planning;

import java.util.LinkedList;

public class PlanSpace
{
	public LinkedList<Action>		allActions;
	public LinkedList<Circumstance>	allCircumstances;

	public PlanSpace()
	{
		allActions = new LinkedList<>();
		allCircumstances = new LinkedList<>();
	}
	
	public void addCircumstance(Circumstance other)
	{
		allCircumstances.add(other);
		other.space = this;
	}

	public void addAction(Action other)
	{
		allActions.add(other);
		other.space = this;
	}
}