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

package net.fabricmc.loom.util.metadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.loom.api.metadata.ModJson;
import net.fabricmc.loom.util.fmj.FabricModJsonHelpers;
import net.fabricmc.loom.util.fmj.FabricModJsonFactory;
import net.fabricmc.loom.util.gradle.SourceSetHelper;
import net.fabricmc.loom.util.qmj.QuiltModJsonFactory;

public class ModJsonFactory {
	public static ModJson createFromZip(Path zipPath) {
		if (FabricModJsonFactory.isQuiltMod(zipPath)) {
			return QuiltModJsonFactory.createFromZip(zipPath);
		} else {
			return FabricModJsonFactory.createFromZip(zipPath);
		}
	}

	public static ModJson createFromZipNullable(Path zipPath) {
		if (FabricModJsonFactory.isQuiltMod(zipPath)) {
			return QuiltModJsonFactory.createFromZipNullable(zipPath);
		} else {
			return FabricModJsonFactory.createFromZipNullable(zipPath);
		}
	}

	public static Optional<? extends ModJson> createFromZipOptional(Path zipPath) {
		if (FabricModJsonFactory.isQuiltMod(zipPath)) {
			return QuiltModJsonFactory.createFromZipOptional(zipPath);
		} else {
			return FabricModJsonFactory.createFromZipOptional(zipPath);
		}
	}

	@Nullable
	public static ModJson createFromSourceSetsNullable(Project project, SourceSet... sourceSets) throws IOException {
		File file = SourceSetHelper.findFirstFileInResource(FabricModJsonHelpers.QUILT_MOD_JSON, project, sourceSets);

		if (file != null) {
			return QuiltModJsonFactory.createFromSourceSetsNullable(project, sourceSets);
		} else {
			return FabricModJsonFactory.createFromSourceSetsNullable(project, sourceSets);
		}
	}
}