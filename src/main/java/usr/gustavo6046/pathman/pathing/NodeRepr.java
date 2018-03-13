package usr.gustavo6046.pathman.pathing;

/**
 * @author gustavo6046
 *
 *         Any element that represents a Path Node.
 */
public interface NodeRepr extends BaseRepresentative
{
	public PathNode asNode(PathSpace other) throws ClassNotFoundException;
}
