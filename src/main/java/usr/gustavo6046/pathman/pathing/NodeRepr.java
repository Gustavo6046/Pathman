package usr.gustavo6046.pathman.pathing;

public interface NodeRepr extends BaseRepresentative
{
	public PathNode asNode(PathSpace other) throws ClassNotFoundException;
}
