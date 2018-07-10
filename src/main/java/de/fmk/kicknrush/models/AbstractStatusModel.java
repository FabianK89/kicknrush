package de.fmk.kicknrush.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;


/**
 * Abstract class for a model with a status.
 *
 * @author FabianK
 */
public abstract class AbstractStatusModel implements IStatusModel {
	/** The status property. */
	protected final ObjectProperty<Status> statusProperty;


	/**
	 * Protected constructor.
	 */
	protected AbstractStatusModel() {
		statusProperty = new SimpleObjectProperty<>(Status.INITIAL);
	}


	@Override
	public ObjectProperty<Status> statusProperty() {
		return statusProperty;
	}
}
