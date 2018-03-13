package usr.gustavo6046.pathman.pathing;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

/**
 * @author gustavo6046
 *
 *         The Path Space to which the Plan Space is compiled in order to find
 *         the required action plan. It is **recommended** that the Plan Space
 *         Manager is used to handle it, instead of accessing it directly!
 * 
 *         This is strategically equivalent to formulating the plan itself.
 */
public class PathSpace
{
	public PathNode						beginNode;

	public LinkedList<PathNode>			allNodes;
	public LinkedList<NodeLink>			allLinks;

	private HashSet<BaseRepresentative>	converting;

	/**
	 * The constructor. Simple as that.
	 * 
	 * @param _begin
	 *            The beginning PathNode representative (i.e. Circumstance).
	 * @throws ClassNotFoundException
	 */
	public PathSpace(NodeRepr _begin) throws ClassNotFoundException
	{
		allNodes = new LinkedList<>();
		allLinks = new LinkedList<>();
		converting = new HashSet<>();

		LinkedList<BaseRepresentative> allConvertees = new LinkedList<>();
		LinkedList<BaseRepresentative> open = new LinkedList<>();

		open.add(_begin);

		while ( open.size() > 0 )
		{
			BaseRepresentative br = open.poll();

			if ( !allConvertees.contains(br) )
			{
				open.addAll(br.alsoConvert());
				allConvertees.addFirst(br);
				// addFirst to avoid stack overflows
			}
		}

		for ( BaseRepresentative br : allConvertees )
			convert(br);

		for ( BaseRepresentative br : allConvertees )
		{
			convert(br);
			converting.clear();
		}

		beginNode = _begin.asNode(this);
	}

	/**
	 * Check if this BaseRepresentative (Action or Circumstance) already represents
	 * a PathElement (PathNode or NodeLink), and if so, returns it.
	 * 
	 * @param orig
	 *            The BaseRepresentative to check for
	 * @return The corresponding PathElement (if any)
	 */
	public Optional<PathElement> checkResultFor(BaseRepresentative orig)
	{
		for ( PathNode p : allNodes )
			if ( p.origin == orig )
				return Optional.of(p);

		for ( NodeLink l : allLinks )
			if ( l.origin == orig )
				return Optional.of(l);

		return Optional.empty();
	}

	public PathElement convert(BaseRepresentative br) throws ClassNotFoundException
	{
		Optional<PathElement> pe = checkResultFor(br);

		if ( pe.isPresent() )
			return pe.get();

		if ( !converting.contains(br) )
			converting.add(br);

		else
			return null;

		try
		{
			br.getClass().asSubclass(NodeRepr.class);

			PathNode pn = ( (NodeRepr) br ).asNode(this);
			pn.origin = (NodeRepr) br;
			allNodes.add(pn);
			converting.remove(br);

			return pn;
		}

		catch ( ClassCastException c1 )
		{
			try
			{
				br.getClass().asSubclass(LinkRepr.class);

				NodeLink nl = ( (LinkRepr) br ).asLink(this);
				nl.origin = (LinkRepr) br;
				allLinks.add(nl);
				converting.remove(br);

				return nl;
			}

			catch ( ClassCastException c2 )
			{
				throw new ClassNotFoundException(
						"Invalid BaseRepresentative passed to (" + toString() + ").convert: " + br.toString());
			}
		}
	}

	// =================
	// Dijkstra search
	// =================

	/**
	 * Performs a Dijkstra search to find a path from the beginning node (which is
	 * currently defined as a field in PathSpace) to any of the here supplied
	 * goalNodes.
	 * 
	 * @param goalNodes
	 *            The nodes that must be reached
	 * @return A list of NodeLinks (which Actions 'represent') that form the path,
	 *         or none if no goal can be reached
	 */
	public Optional<List<NodeLink>> linkPath(Collection<PathNode> goalNodes)
	{
		HashSet<NodeLink> closedSet = new HashSet<>();
		HashMap<NodeLink, Integer> rcosts = new HashMap<>();
		HashMap<NodeLink, NodeLink> directions = new HashMap<>();

		boolean goalReached = false;

		class _Comp implements Comparator<NodeLink>
		{
			@Override
			public int compare(NodeLink o1, NodeLink o2)
			{
				return (int) Math.round(cost(o2) * 1000 - cost(o1) * 1000);
			}

			public double cost(NodeLink other)
			{
				if ( beginNode.outward.contains(other) )
					return 0;

				return other.cost + rcosts.get(directions.get(other));
			}
		}

		PriorityQueue<NodeLink> openSet = new PriorityQueue<>(new _Comp());

		for ( NodeLink l : beginNode.outward )
			rcosts.put(l, 0);

		openSet.addAll(beginNode.outward);

		NodeLink lastLink = null;

		while ( openSet.size() > 0 )
		{
			NodeLink currl = openSet.poll();
			PathNode currn = currl.destination;
			closedSet.add(currl);

			if ( currn == null )
				continue;

			for ( NodeLink l : currn.outward )
			{
				if ( !closedSet.contains(l) )
				{
					directions.put(l, currl);

					if ( goalNodes.contains(l.destination) )
					{
						goalReached = true;
						lastLink = l;
						break;
					}

					openSet.add(l);
					rcosts.put(l, rcosts.get(currl) + 1);
				}

			}

			if ( goalReached )
				break;
		}

		if ( !goalReached )
			return Optional.empty();

		LinkedList<NodeLink> linkPath = new LinkedList<>();
		NodeLink currl = lastLink;

		while ( currl != null )
		{
			linkPath.addFirst(currl);
			currl = directions.get(currl);
		}

		return Optional.of(linkPath);
	}
}
