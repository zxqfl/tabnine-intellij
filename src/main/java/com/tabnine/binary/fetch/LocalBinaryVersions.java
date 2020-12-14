package com.tabnine.binary.fetch;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.text.SemVer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static com.tabnine.general.StaticConfig.getBaseDirectory;
import static java.util.stream.Collectors.toList;

public class LocalBinaryVersions {
    private BinaryValidator binaryValidator;

    public LocalBinaryVersions(BinaryValidator binaryValidator) {
        this.binaryValidator = binaryValidator;
    }

    @NotNull
    public List<BinaryVersion> listExisting() {
        File[] versionsFolders = Optional.ofNullable(getBaseDirectory().toFile().listFiles()).orElse(new File[0]);

        return Stream.of(versionsFolders).map(File::getName).map(SemVer::parseFromText).filter(Objects::nonNull)
                .sorted(Comparator.reverseOrder())
                .map(SemVer::toString)
                .map(BinaryVersion::new)
                .filter(version -> binaryValidator.isWorking(version.getVersionFullPath())).collect(toList());
    }

    public Optional<BinaryVersion> activeVersion() {
        Path activeFile = getBaseDirectory().resolve(".active");

        if (!activeFile.toFile().exists()) { return Optional.empty(); }

        List<String> lines;
        try {
            lines = Files.readAllLines(activeFile);
        } catch (IOException e) {
            Logger.getInstance(getClass()).warn("Failed to read .active file", e);
            lines = new ArrayList<String>();
        }

        if (lines.size() == 0) { return Optional.empty(); }

        String version = lines.get(0);

        BinaryVersion binaryVersion = new BinaryVersion(version);

        if (!binaryValidator.isWorking(binaryVersion.getVersionFullPath())) {
            Logger.getInstance(getClass()).warn("Version in .active file is not working");
            return Optional.empty();
        }

        return Optional.of(binaryVersion);
    }
}
