/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

import blue.endless.jankson.JsonElement;

/**
 * Generic interface for values that may be stored as a Jankson element. Used for serialization of theme styles.
 */
public interface JanksonSerializable {
	JsonElement toJson();
}
