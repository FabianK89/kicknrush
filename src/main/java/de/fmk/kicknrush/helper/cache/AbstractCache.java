package de.fmk.kicknrush.helper.cache;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
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


	AbstractCache() {
		values = new HashMap<>();
	}


	@Override
	public void clear() {
		values.clear();
	}


	@Override
	public BooleanProperty getBooleanProperty(ICacheKey key) {
		return getProperty(BooleanProperty.class, SimpleBooleanProperty.class, Boolean.class, key);
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
		return getProperty(DoubleProperty.class, SimpleDoubleProperty.class, Double.class, key);
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
	public IntegerProperty getIntegerProperty(ICacheKey key) {
		return getProperty(IntegerProperty.class, SimpleIntegerProperty.class, Integer.class, key);
	}


	@Override
	public int getIntegerValue(ICacheKey key) {
		return getIntegerValue(key, 0);
	}


	@Override
	public int getIntegerValue(ICacheKey key, int fallback) {
		if (key == null || key.getValueClass() != Integer.class || values.get(key) == null)
			return fallback;

		return getIntegerProperty(key).get();
	}


	@Override
	public StringProperty getStringProperty(ICacheKey key) {
		return getProperty(StringProperty.class, SimpleStringProperty.class, String.class, key);
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

		if (key.getValueClass() == Boolean.class && storedValue instanceof BooleanProperty)
			return ((BooleanProperty) storedValue).getValue().toString();
		else if (key.getValueClass() == Double.class && storedValue instanceof DoubleProperty)
			return Double.toString(((DoubleProperty) storedValue).doubleValue());
		else if (key.getValueClass() == Integer.class && storedValue instanceof IntegerProperty)
			return Integer.toString(((IntegerProperty) storedValue).intValue());
		else if (key.getValueClass() == String.class && storedValue instanceof StringProperty)
			return ((StringProperty) storedValue).get();

		return null;
	}


	@Override
	public void parseAndPutStringValue(ICacheKey key, String value) {
		if (key == null || value == null)
			throw new IllegalArgumentException("The key and the value must not be null.");

		if (key.getValueClass() == Boolean.class)
			putBooleanValue(key, Boolean.parseBoolean(value));
		else if (key.getValueClass() == Double.class)
			putDoubleValue(key, Double.parseDouble(value));
		else if (key.getValueClass() == Integer.class)
			putIntegerValue(key, Integer.parseInt(value));
		else if (key.getValueClass() == String.class)
			putStringValue(key, value);
	}


	@Override
	public void putBooleanValue(ICacheKey key, boolean value) {
		checkValue(key, Boolean.class);
		getBooleanProperty(key).setValue(value);
	}


	@Override
	public void putDoubleValue(ICacheKey key, double value) {
		checkValue(key, Double.class);
		getDoubleProperty(key).setValue(value);
	}


	@Override
	public void putIntegerValue(ICacheKey key, int value) {
		checkValue(key, Integer.class);
		getIntegerProperty(key).setValue(value);
	}


	@Override
	public void putStringValue(ICacheKey key, String value) {
		checkValue(key, String.class);
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
		else if (key.getValueClass() == Integer.class)
			getIntegerProperty(key).setValue((Integer) value);
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


	private <T> void checkValue(ICacheKey key, Class<T> valueClass) {
		if (key == null || key.getValueClass() != valueClass)
			throw new IllegalArgumentException("The key '" + key + "' does not store a " + valueClass.getName() + " value.");
	}


	private <R extends T, T, X> T getProperty(final Class<T>  propertyClass,
	                                          final Class<R>  simplePropertyClass,
	                                          final Class<X>  typeClass,
	                                          final ICacheKey key) {
		final T      property;
		final Object storedValue;

		if (key == null || key.getValueClass() != typeClass)
			throw new IllegalArgumentException("No integer value found for the key '" + key + "'.");

		storedValue = values.get(key);

		if (propertyClass.isInstance(storedValue))
			return (T) storedValue;

		try {
			property = simplePropertyClass.newInstance();
			values.put(key, property);

			return property;
		}
		catch (InstantiationException | IllegalAccessException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
