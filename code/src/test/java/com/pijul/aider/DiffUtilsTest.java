package com.pijul.aider;

import com.github.difflib.patch.PatchFailedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DiffUtilsTest {

    @Test
    public void testApplyPatch() throws PatchFailedException {
        String original = "Hello, World!\nThis is the original file.";
        String diff = "--- a/original\n+++ b/patched\n@@ -1,2 +1,2 @@\n Hello, World!\n-This is the original file.\n+This is the patched file.\n";
        String expected = "Hello, World!\nThis is the patched file.";

        String patched = DiffUtils.applyPatch(original, diff);
        assertEquals(expected, patched);
    }

    @Test
    public void testApplyPatch_noChanges() throws PatchFailedException {
        String original = "Hello, World!\nThis is the original file.";
        String diff = "--- a/original\n+++ b/patched\n@@ -1,2 +1,2 @@\n Hello, World!\n This is the original file.\n";

        String patched = DiffUtils.applyPatch(original, diff);
        assertEquals(original, patched);
    }

    @Test
    public void testApplyPatch_addition() throws PatchFailedException {
        String original = "Hello, World!";
        String diff = "--- a/original\n+++ b/patched\n@@ -1 +1,2 @@\n Hello, World!\n+This is a new line.\n";
        String expected = "Hello, World!\nThis is a new line.";

        String patched = DiffUtils.applyPatch(original, diff);
        assertEquals(expected, patched);
    }

    @Test
    public void testApplyPatch_deletion() throws PatchFailedException {
        String original = "Hello, World!\nThis is a line to be deleted.";
        String diff = "--- a/original\n+++ b/patched\n@@ -1,2 +1 @@\n Hello, World!\n-This is a line to be deleted.\n";
        String expected = "Hello, World!";

        String patched = DiffUtils.applyPatch(original, diff);
        assertEquals(expected, patched);
    }

    @Test
    public void testApplyPatch_invalidPatch() throws PatchFailedException {
        String original = "Hello, World!";
        String diff = "invalid diff";
        String patched = DiffUtils.applyPatch(original, diff);
        assertEquals(original, patched);
    }
}
