package usr.gustavo6046.pathman.pathing;

/**
 * @author gustavo6046
 *
 *         Any element that represents a Pathnode Link.
 */
public interface LinkRepr extends BaseRepresentative
{
	public NodeLink asLink(PathSpace other) throws ClassNotFoundException;
}
