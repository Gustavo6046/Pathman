package usr.gustavo6046.pathman.planning;

import java.util.LinkedList;

/**
 * @author gustavo6046
 *
 *         A Condition. Or rather, a property specific to any object or aspect
 *         of a Circumstance. Stores a labeled list of doubles to represent any
 *         numerically exportable data (but not necessary importable). This
 *         theoretically means anything, since everything in a computer must be
 *         represented by a number, or more specifically, by base-2 numbers
 *         (sequence of bits).
 */
public class Condition
{
	public static class Builder
	{
		private LinkedList<Condition> conditions;

		public Builder()
		{
			conditions = new LinkedList<>();
		}

		public Builder addCondition(String key, double... values)
		{
			LinkedList<Double> lvalues = new LinkedList<>();

			for ( double v : values )
				lvalues.add(v);

			conditions.add(new Condition(key, lvalues));

			return this;
		}

		public LinkedList<Condition> build()
		{
			LinkedList<Condition> res = conditions;
			conditions = new LinkedList<>();
			return res;
		}

		public Condition sbuild()
		{
			return conditions.poll();
		}
	}

	public String				key;
	public LinkedList<Double>	values;

	public Condition(String _key, LinkedList<Double> _values)
	{
		key = _key;
		values = _values;
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public boolean equals(Object other)
	{
		if ( other.getClass() != Condition.class )
			return false;

		Condition cond2 = (Condition) other;

		if ( key != cond2.key || values.size() != cond2.values.size() )
			return false;

		int vind = 0;

		for ( double v : values )
		{
			if ( v != cond2.values.get(vind) )
				return false;

			vind++;
		}

		return true;
	}
}
