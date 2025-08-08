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

import java.util.Objects;

import com.google.gson.JsonObject;

import net.fabricmc.loom.api.metadata.ModJson;
import net.fabricmc.loom.util.fmj.FabricModJsonSource;

public abstract sealed class QuiltModJson implements ModJson permits QuiltModJsonV1 {
	protected final JsonObject jsonObject;
	protected final JsonObject loader;
	private final FabricModJsonSource source;

	protected QuiltModJson(JsonObject jsonObject, FabricModJsonSource source) {
		this.jsonObject = Objects.requireNonNull(jsonObject);
		this.source = Objects.requireNonNull(source);
		this.loader = jsonObject.getAsJsonObject("quilt_loader");
	}

	@Override
	public final FabricModJsonSource getSource() {
		return source;
	}

	@Override
	public final String toString() {
		return getClass().getName() + "[id=%s]".formatted(getId());
	}

	@Override
	public final int hashCode() {
		return Objects.hash(getId());
	}
}