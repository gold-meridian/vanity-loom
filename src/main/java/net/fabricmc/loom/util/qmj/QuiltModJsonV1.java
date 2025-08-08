/*
 * This file is part of fabric-loom, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2022 FabricMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.fabricmc.loom.util.qmj;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.loom.util.fmj.ModEnvironment;
import net.fabricmc.loom.util.fmj.FabricModJsonSource;
import net.fabricmc.loom.util.fmj.FabricModJsonUtils;

public final class QuiltModJsonV1 extends QuiltModJson {
	QuiltModJsonV1(JsonObject jsonObject, FabricModJsonSource source) {
		super(jsonObject, source);
	}

	@Override
	public String getId() {
		return FabricModJsonUtils.readString(loader, "id");
	}

	@Override
	public String getModVersion() {
		return FabricModJsonUtils.readString(loader, "version");
	}

	@Override
	public String getModName() {
		if (loader.has("metadata")) {
			JsonObject metadata = loader.getAsJsonObject("metadata");

			if (metadata.has("name")) {
				return FabricModJsonUtils.readString(metadata, "name");
			}
		}

		return null;
	}

	public @Nullable JsonElement getCustom(String key) {
		return jsonObject.get(key);
	}

	@Override
	public List<String> getMixinConfigurations() {
		final JsonElement mixins = jsonObject.get("mixin");

		if (mixins == null) {
			return Collections.emptyList();
		} else if (mixins.isJsonArray()) {
			return StreamSupport.stream(mixins.getAsJsonArray().spliterator(), false)
					.map(QuiltModJsonV1::readMixinElement)
					.collect(Collectors.toList());
		} else if (mixins.isJsonPrimitive() && mixins.getAsJsonPrimitive().isString()) {
			return Collections.singletonList(mixins.getAsJsonPrimitive().getAsString());
		} else {
			throw new RuntimeException("Incorrect QMJ format; expected 'mixin' to be a string or array");
		}
	}

	private static String readMixinElement(JsonElement jsonElement) {
		if (jsonElement instanceof JsonPrimitive str) {
			return str.getAsString();
		} else if (jsonElement instanceof JsonObject obj) {
			return obj.get("config").getAsString();
		} else {
			throw new RuntimeException("Expected mixin element to be an object or string");
		}
	}

	@Override
	public Map<String, ModEnvironment> getClassTweakers() {
		final JsonElement aws = jsonObject.get("access_widener");

		if (aws != null) {
			if (aws.isJsonArray()) {
				JsonArray array = aws.getAsJsonArray();

				if (array.size() > 1) {
					throw new UnsupportedOperationException("Loom does not support more than one access widener per mod. Sorry!");
				} else if (array.size() == 1) {
					return Map.of(array.get(0).getAsString(), ModEnvironment.UNIVERSAL);
				}
			} else if (aws.isJsonPrimitive() && aws.getAsJsonPrimitive().isString()) {
				return Map.of(aws.getAsString(), ModEnvironment.UNIVERSAL);
			}
		}

		return Collections.emptyMap();
	}

	@Override
	public @Nullable JsonElement getInjectedInterfaces() {
		final JsonElement loom = getCustom("quilt_loom");

		if (loom != null) {
			return loom.getAsJsonObject().get("injected_interfaces");
		} else {
			return null;
		}
	}

	@Override
	public String getProvidedJavadocPath() {
		final JsonElement loom = getCustom("quilt_loom");

		if (loom != null) {
			return FabricModJsonUtils.readStringOrNull(loom.getAsJsonObject(), "provided_javadoc");
		} else {
			return null;
		}
	}

	@Override
	public JsonObject stripNestedJars(JsonObject json) {
		JsonObject loader = json.has("quilt_loader") ? json.get("quilt_loader").getAsJsonObject() : new JsonObject();
		loader.remove("jars");
		return json;
	}

	@Override
	public JsonObject addNestedJars(JsonObject json, List<String> files) {
		JsonObject loader = json.has("quilt_loader") ? json.get("quilt_loader").getAsJsonObject() : new JsonObject();
		JsonArray nestedJars = loader.has("jars") ? json.get("jars").getAsJsonArray() : new JsonArray();

		for (String nestedJarPath : files) {
			for (JsonElement nestedJar : nestedJars) {
				if (nestedJarPath.equals(nestedJar.getAsString())) {
					throw new IllegalStateException("Cannot nest 2 jars at the same path: " + nestedJarPath);
				}
			}

			nestedJars.add(nestedJarPath);
		}

		loader.add("jars", nestedJars);

		return json;
	}
}