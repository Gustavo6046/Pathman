package usr.gustavo6046.pathman.pathing;

public class NodeLink extends PathElement
{
	public PathNode	destination;
	public double	cost;
	public LinkRepr	origin;

	public NodeLink(PathNode dest, double _cost)
	{
		cost = _cost;
		destination = dest;
		
		if ( dest != null )
			dest.inward.add(this);
	}

	public NodeLink(PathNode src, PathNode dest, double _cost)
	{
		this(dest, _cost);
		
		if ( src != null )
			src.outward.add(this);
	}
}
