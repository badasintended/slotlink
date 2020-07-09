/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.common.configuration.data;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ConfigurationHolder<T> {
	T t;
	String namespace;
	String path;

	public ConfigurationHolder(T t) {
		setValue(t);
	}

	public T getValue() {
		return t;
	}

	public void setValue(T t) {
		this.t = t;
	}

	public Text getText() {
		return new TranslatableText("text." + namespace + ".configuration." + path);
	}

	public String getNamespace() {
		return namespace;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
}
