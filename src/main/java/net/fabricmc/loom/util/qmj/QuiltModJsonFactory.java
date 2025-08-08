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

import static net.fabricmc.loom.util.fmj.FabricModJsonUtils.readInt;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.loom.LoomGradlePlugin;
import net.fabricmc.loom.util.ZipUtils;
import net.fabricmc.loom.util.gradle.SourceSetHelper;
import net.fabricmc.loom.util.fmj.FabricModJsonSource;

public final class QuiltModJsonFactory {
	private static final String QUILT_MOD_JSON = "quilt.mod.json";
	private static final Logger LOGGER = LoggerFactory.getLogger(QuiltModJsonFactory.class);
	private QuiltModJsonFactory() {
	}

	@VisibleForTesting
	public static QuiltModJson create(JsonObject jsonObject, FabricModJsonSource source) {
		int schemaVersion = 0;

		if (jsonObject.has("schema_version")) {
			// V0 had no schemaVersion key.
			schemaVersion = readInt(jsonObject, "schema_version");
		}

		return switch (schemaVersion) {
		case 1 -> new QuiltModJsonV1(jsonObject, source);
		default -> throw new UnsupportedOperationException(String.format("This version of quilt-loom doesn't support the newer quilt.mod.json schema version of (%s) Please update quilt-loom to be able to read this.", schemaVersion));
		};
	}

	public static QuiltModJson createFromZip(Path zipPath) {
		try {
			return create(ZipUtils.unpackGson(zipPath, QUILT_MOD_JSON, JsonObject.class), new FabricModJsonSource.ZipSource(zipPath));
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read fabric.mod.json file in zip: " + zipPath, e);
		}
	}

	@Nullable
	public static QuiltModJson createFromZipNullable(Path zipPath) {
		JsonObject jsonObject;

		try {
			jsonObject = ZipUtils.unpackGsonNullable(zipPath, QUILT_MOD_JSON, JsonObject.class);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read zip: " + zipPath, e);
		}

		if (jsonObject == null) {
			return null;
		}

		return create(jsonObject, new FabricModJsonSource.ZipSource(zipPath));
	}

	public static Optional<QuiltModJson> createFromZipOptional(Path zipPath) {
		return Optional.ofNullable(createFromZipNullable(zipPath));
	}

	public static QuiltModJson createFromDirectory(Path directory) throws IOException {
		final Path path = directory.resolve(QUILT_MOD_JSON);

		try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			return create(LoomGradlePlugin.GSON.fromJson(reader, JsonObject.class), new FabricModJsonSource.DirectorySource(directory));
		}
	}

	@Nullable
	public static QuiltModJson createFromSourceSetsNullable(Project project, SourceSet... sourceSets) throws IOException {
		final File file = SourceSetHelper.findFirstFileInResource(QUILT_MOD_JSON, project, sourceSets);

		if (file == null) {
			return null;
		}

		try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
			return create(LoomGradlePlugin.GSON.fromJson(reader, JsonObject.class), new FabricModJsonSource.SourceSetSource(project, sourceSets));
		} catch (JsonSyntaxException e) {
			LOGGER.warn("Failed to parse mod json: {}", file.getAbsolutePath());
			return null;
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read " + file.getAbsolutePath(), e);
		}
	}
}