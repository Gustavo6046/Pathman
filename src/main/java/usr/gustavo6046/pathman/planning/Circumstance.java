package usr.gustavo6046.pathman.planning;

import java.util.LinkedList;
import java.util.List;

import usr.gustavo6046.pathman.pathing.BaseRepresentative;
import usr.gustavo6046.pathman.pathing.NodeLink;
import usr.gustavo6046.pathman.pathing.NodeRepr;
import usr.gustavo6046.pathman.pathing.PathNode;
import usr.gustavo6046.pathman.pathing.PathSpace;

/**
 * @author gustavo6046
 *
 *         A Circumstance. Or rather, a graph element which stores data about a
 *         possible situation, or as we say, a possible Circumstance. From game
 *         conditions to object-specific properties, the gamma of data that can
 *         be represented is immense.
 */
public abstract class Circumstance implements NodeRepr
{
	/**
	 * @author gustavo6046
	 *
	 *         A Circumstance Matcher. It may be used to match one or more
	 *         Circumstances, using a set of Conditions that are necessary by
	 *         content, existence or inexistence in order to be compatible with this
	 *         Matcher.
	 */
	public static class Matcher
	{
		/**
		 * @author gustavo6046
		 *
		 *         A Matcher Builder.
		 */
		public static class Builder
		{
			private Matcher mt;

			public Builder()
			{
				mt = new Matcher();
			}

			public Matcher build()
			{
				return mt;
			}

			public Builder addEqual(Condition needed)
			{
				mt.needEqual.add(needed);
				return this;
			}

			public Builder addEqual(String nkey, double... nval)
			{
				mt.needEqual.add(Condition.builder().addCondition(nkey, nval).sbuild());
				return this;
			}

			public Builder addKeyEqual(String needed)
			{
				mt.needEqualKey.add(needed);
				return this;
			}

			public Builder addKeyUnequal(String needed)
			{
				mt.needUnequalKey.add(needed);
				return this;
			}
		}

		protected LinkedList<Condition>	needEqual;
		protected LinkedList<String>	needEqualKey;
		protected LinkedList<String>	needUnequalKey;

		public Matcher()
		{
			needEqual = new LinkedList<>();
			needEqualKey = new LinkedList<>();
			needUnequalKey = new LinkedList<>();
		}

		public boolean matches(Circumstance other)
		{
			int hasEqual = 0;
			int hasEqualKey = 0;

			for ( Condition c1 : other.getConditions() )
			{
				for ( String cu : needUnequalKey )
					if ( c1.key == cu )
						return false;

				for ( String ck : needEqualKey )
					if ( c1.key == ck )
						hasEqualKey++;

				for ( Condition ce : needEqual )
					if ( ce.equals(c1) )
						hasEqual++;
			}

			return hasEqual == needEqual.size() && hasEqualKey == needEqualKey.size();
		}

		public static Matcher equality(Circumstance ref)
		{
			Builder b = new Builder();

			for ( Condition c : ref.getConditions() )
				b.addEqual(c);

			return b.build();
		}
	}

	public static Matcher.Builder matcher()
	{
		return new Matcher.Builder();
	}

	public PlanSpace space;

	public Circumstance(PlanSpace _space)
	{
		space = _space;
	}

	/**
	 * Creates a Matcher to which Circumstances must be **identical** by conditions,
	 * their keys and even values, in order to match. Useful for Actions that take
	 * an existing Circumstance as a possible starting prerequisite.
	 * 
	 * @return The resulting Matcher.
	 */
	public Matcher equalMatcher()
	{
		Matcher.Builder b = matcher();

		for ( Condition c : getConditions() )
			b.addEqual(c);

		return b.build();
	}

	public abstract List<Condition> getConditions();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other)
	{
		if ( other.getClass() != Circumstance.class )
			return false;

		int index = 0;
		Circumstance cother = (Circumstance) other;

		for ( Condition cond1 : getConditions() )
		{
			Condition cond2 = cother.getConditions().get(index);

			if ( !cond1.equals(cond2) )
				return false;

			index++;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see usr.gustavo6046.pathman.pathing.NodeRepr#asNode(usr.gustavo6046.pathman.
	 * pathing.PathSpace)
	 */
	@Override
	public PathNode asNode(PathSpace other) throws ClassNotFoundException
	{
		LinkedList<NodeLink> links = new LinkedList<>();

		for ( Action a : space.allActions )
			if ( a.prerequisite.matches(this) )
				links.add((NodeLink) other.convert(a));

		return new PathNode(links);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see usr.gustavo6046.pathman.pathing.BaseRepresentative#alsoConvert()
	 */
	@Override
	public List<BaseRepresentative> alsoConvert()
	{
		LinkedList<BaseRepresentative> links = new LinkedList<>();

		for ( Action a : space.allActions )
			if ( a.prerequisite.matches(this) )
				links.add(a);

		return links;
	}
}
