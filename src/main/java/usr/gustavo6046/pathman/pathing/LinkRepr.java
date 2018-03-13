package usr.gustavo6046.pathman.pathing;

public interface LinkRepr extends BaseRepresentative
{
	public NodeLink asLink(PathSpace other) throws ClassNotFoundException;
}
