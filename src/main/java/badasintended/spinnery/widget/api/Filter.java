/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

import java.util.HashMap;
import java.util.Map;

public class Filter {
	private static final Map<Class<?>, WInputFilter<?>> ENTRIES = new HashMap<>();

	public static void register(Class<?> clazz, WInputFilter<?> filter) {
		ENTRIES.put(clazz, filter);
	}

	public static <T> T asValue(Class<?> clazz, String text) {
		return (T) ENTRIES.get(clazz).toValue(text);
	}

	public static <T> WInputFilter<T> get(Class<?> clazz) {
		return (WInputFilter<T>) ENTRIES.get(clazz);
	}

	/**
	 * Filter for strings.
	 */
	public static final WInputFilter<String> STRING_FILTER = new WInputFilter<String>() {
		@Override
		public String toString(String value) {
			return value;
		}

		@Override
		public String toValue(String text) {
			return text;
		}

		@Override
		public boolean accepts(String character, String text) {
			return true;
		}
	};

	/**
	 * Filter for decimal integers and hexadecimal integers.
	 */
	public static final WInputFilter<Integer> INTEGER_FILTER = new WInputFilter<Integer>() {
		@Override
		public String toString(Integer value) {
			return String.valueOf(value);
		}

		@Override
		public Integer toValue(String text) {
			try {
				return Integer.valueOf(text);
			} catch (Exception exception) {
				return 0;
			}
		}

		@Override
		public boolean accepts(String character, String text) {
			return "0123456789".contains(character) || (character.equals("x") && !text.contains("x"));
		}
	};

	/**
	 * Filter for decimal longs and hexadecimal longs.
	 */
	public static final WInputFilter<Long> LONG_FILTER = new WInputFilter<Long>() {
		@Override
		public String toString(Long value) {
			return String.valueOf(value);
		}

		@Override
		public Long toValue(String text) {
			try {
				return Long.valueOf(text);
			} catch (Exception exception) {
				return 0L;
			}
		}

		@Override
		public boolean accepts(String character, String text) {
			return "0123456789".contains(character) || (character.equals("x") && !text.contains("x"));
		}
	};

	/**
	 * Filter for floats.
	 */
	public static final WInputFilter<Float> FLOAT_FILTER = new WInputFilter<Float>() {
		@Override
		public String toString(Float value) {
			return String.valueOf(value);
		}

		@Override
		public Float toValue(String text) {
			try {
				return Float.valueOf(text);
			} catch (Exception exception) {
				return 0f;
			}
		}

		@Override
		public boolean accepts(String character, String text) {
			return "0123456789".contains(character) || (character.equals(".") && !text.contains("."));
		}
	};

	/**
	 * Filter for doubles.
	 */
	public static final WInputFilter<Double> DOUBLE_FILTER = new WInputFilter<Double>() {
		@Override
		public String toString(Double value) {
			return String.valueOf(value);
		}

		@Override
		public Double toValue(String text) {
			try {
				return Double.valueOf(text);
			} catch (Exception exception) {
				return 0d;
			}
		}

		@Override
		public boolean accepts(String character, String text) {
			return "0123456789".contains(character) || (character.equals(".") && !text.contains("."));
		}
	};

	public static final WInputFilter<Boolean> BOOLEAN_FILTER = new WInputFilter<Boolean>() {
		@Override
		public String toString(Boolean value) {
			return Boolean.toString(value);
		}

		@Override
		public Boolean toValue(String text) {
			try {
				return Boolean.valueOf(text);
			} catch (Exception exception) {
				return false;
			}
		}

		@Override
		public boolean accepts(String character, String text) {
			return ("true".contains(character) && !text.contains(character)) || ("false".contains(character) && !text.contains(character)) && text.length() < 5;
		}
	};

	static {
		register(String.class, Filter.STRING_FILTER);
		register(Integer.class, Filter.INTEGER_FILTER);
		register(Long.class, Filter.LONG_FILTER);
		register(Float.class, Filter.FLOAT_FILTER);
		register(Double.class, Filter.DOUBLE_FILTER);
		register(Boolean.class, Filter.BOOLEAN_FILTER);
	}
}
