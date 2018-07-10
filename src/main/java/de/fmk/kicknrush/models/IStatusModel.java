package de.fmk.kicknrush.models;

import javafx.beans.property.ObjectProperty;


/**
 * Interface for models with a status.
 *
 * @author FabianK
 */
public interface IStatusModel {
	/**
	 * Status of a operation.
	 * @return the status property.
	 */
	ObjectProperty<Status> statusProperty();
}
