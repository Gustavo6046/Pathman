package usr.gustavo6046.pathman.pathing;

import java.util.List;

/**
 * @author gustavo6046
 * 
 *         Any element that may represent a Path Element, ie, a PathSpace
 *         element.
 */
public interface BaseRepresentative
{
	/**
	 * The list of BaseRepresentatives that this one depends on.
	 * 
	 * @return The list above ._.
	 */
	public List<BaseRepresentative> alsoConvert();
}
