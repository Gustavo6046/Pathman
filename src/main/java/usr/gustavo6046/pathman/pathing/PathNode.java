package usr.gustavo6046.pathman.pathing;

import java.util.LinkedList;
import java.util.List;

public class PathNode extends PathElement
{
	public LinkedList<NodeLink>	outward;
	public LinkedList<NodeLink>	inward;
	public NodeRepr				origin;

	public PathNode()
	{
		inward = new LinkedList<>();
		outward = new LinkedList<>();
	}
	
	public PathNode(List<NodeLink> out)
	{
		this();
		outward.addAll(out);
	}
}
