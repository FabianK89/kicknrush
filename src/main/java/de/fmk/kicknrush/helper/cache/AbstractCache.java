package de.fmk.kicknrush.helper.cache;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * Abstract cache class.
 *
 * @author FabianK
 */
public abstract class AbstractCache implements ICache {
	protected final Map<ICacheKey, Object> values;


	public AbstractCache() {
		values = new HashMap<>();
	}


	/**
	 * Performs the given action for each key in the cache.
	 * @param action The action to be performed for each key.
	 * @throws java.lang.NullPointerException if the specified action is <code>null</code>.
	 */
	public void forEachKey(Consumer<ICacheKey> action) {
		Objects.requireNonNull(action);

		for (ICacheKey key : values.keySet())
			action.accept(key);
	}


	@Override
	public BooleanProperty getBooleanProperty(ICacheKey key) {
		final BooleanProperty property;
		final Object          storedValue;

		if (key == null || key.getValueClass() != Boolean.class)
			throw new IllegalArgumentException("No boolean value found for the key '" + key + "'.");

		storedValue = values.get(key);

		if (storedValue instanceof BooleanProperty)
			return (BooleanProperty) storedValue;

		property = new SimpleBooleanProperty();
		values.put(key, property);

		return property;
	}


	@Override
	public boolean getBooleanValue(ICacheKey key, boolean fallback) {
		if (key == null || key.getValueClass() != Boolean.class || values.get(key) == null)
			return fallback;

		return getBooleanProperty(key).get();
	}


	@Override
	public boolean getBooleanValue(ICacheKey key) {
		return getBooleanValue(key, false);
	}


	@Override
	public DoubleProperty getDoubleProperty(ICacheKey key) {
		final DoubleProperty property;
		final Object          storedValue;

		if (key == null || key.getValueClass() != Double.class)
			throw new IllegalArgumentException("No double value found for the key '" + key + "'.");

		storedValue = values.get(key);

		if (storedValue instanceof DoubleProperty)
			return (DoubleProperty) storedValue;

		property = new SimpleDoubleProperty();
		values.put(key, property);

		return property;
	}


	@Override
	public double getDoubleValue(ICacheKey key, double fallback) {
		if (key == null || key.getValueClass() != Double.class || values.get(key) == null)
			return fallback;

		return getDoubleProperty(key).get();
	}


	@Override
	public double getDoubleValue(ICacheKey key) {
		return getDoubleValue(key, 0.0);
	}


	@Override
	public StringProperty getStringProperty(ICacheKey key) {
		final StringProperty property;
		final Object         storedValue;

		if (key == null || key.getValueClass() != String.class)
			throw new IllegalArgumentException("No string value found for the key '" + key + "'.");

		storedValue = values.get(key);

		if (storedValue instanceof StringProperty)
			return (StringProperty) storedValue;

		property = new SimpleStringProperty();
		values.put(key, property);

		return property;
	}


	@Override
	public String getStringValue(ICacheKey key, String fallback) {
		if (key == null || key.getValueClass() != String.class || values.get(key) == null)
			return fallback;

		return getStringProperty(key).get();
	}


	@Override
	public String getStringValue(ICacheKey key) {
		return getStringValue(key, null);
	}


	@Override
	public String getValueAsString(ICacheKey key) {
		final Object storedValue;

		if (key == null)
			return null;

		storedValue = values.get(key);

		if (key.getValueClass() == Boolean.class && storedValue instanceof Boolean)
			return ((Boolean) storedValue).toString();
		else if (key.getValueClass() == Double.class && storedValue instanceof Double)
			return Double.toString((Double) storedValue);
		else if (key.getValueClass() == String.class && storedValue instanceof String)
			return (String) storedValue;

		return null;
	}


	@Override
	public void parseAndPutStringValue(ICacheKey key, String value) {
		if (key == null || value == null)
			throw new IllegalArgumentException("The key and the value must not be null.");

		if (key.getValueClass() == Boolean.class)
			putBooleanValue(key, Boolean.parseBoolean(value));
		if (key.getValueClass() == Double.class)
			putDoubleValue(key, Double.parseDouble(value));
		if (key.getValueClass() == String.class)
			putStringValue(key, value);
	}


	@Override
	public void putBooleanValue(ICacheKey key, boolean value) {
		if (key == null || key.getValueClass() != Boolean.class)
			throw new IllegalArgumentException("The key '" + key + "' does not store a boolean value.");

		getBooleanProperty(key).setValue(value);
	}


	@Override
	public void putDoubleValue(ICacheKey key, double value) {
		if (key == null || key.getValueClass() != Double.class)
			throw new IllegalArgumentException("The key '" + key + "' does not store a double value.");

		getDoubleProperty(key).setValue(value);
	}


	@Override
	public void putStringValue(ICacheKey key, String value) {
		if (key == null || key.getValueClass() != String.class)
			throw new IllegalArgumentException("The key '" + key + "' does not store a string value.");

		getStringProperty(key).setValue(value);
	}


	@Override
	public <T> void putValue(ICacheKey key, T value) {
		if (key == null || !key.getValueClass().isInstance(value))
			throw new IllegalArgumentException("Type mismatch between key and value.");

		if (key.getValueClass() == Boolean.class)
			getBooleanProperty(key).setValue((Boolean) value);
		else if (key.getValueClass() == Double.class)
			getDoubleProperty(key).setValue((Double) value);
		else if (key.getValueClass() == String.class)
			getStringProperty(key).setValue((String) value);
	}


	@Override
	public void removeValue(ICacheKey key) {
		if (key != null)
			values.remove(key);
	}


	@Override
	public Map<ICacheKey, Object> getValues() {
		return Collections.unmodifiableMap(values);
	}


	@Override
	public boolean isEmpty() {
		return values.isEmpty();
	}
}
