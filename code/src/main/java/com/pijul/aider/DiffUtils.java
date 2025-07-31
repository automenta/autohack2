package com.pijul.aider;

import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.PatchFailedException;

import java.util.Arrays;
import java.util.List;

public class DiffUtils {

    public static String applyPatch(String original, String diff) throws PatchFailedException {
        List<String> originalLines = Arrays.asList(original.split("\n"));
        List<String> diffLines = Arrays.asList(diff.split("\n"));

        Patch<String> patch = UnifiedDiffUtils.parseUnifiedDiff(diffLines);
        List<String> patchedLines = com.github.difflib.DiffUtils.patch(originalLines, patch);

        return String.join("\n", patchedLines);
    }
}