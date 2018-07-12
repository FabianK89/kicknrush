package de.fmk.kicknrush.views;

/**
 * Interface for panes that process data.
 *
 * @author FabianK
 */
public interface IPaneHandler {
	/**
	 * Load data and fill the controls on the pane.
	 */
	void enter();

	/**
	 * Check if the user can leave the pane and buffer data.
	 */
	boolean leave();
}
