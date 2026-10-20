package org.workswap.storage.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class ImageFormatRegistry {

    private static final Map<String, String> MIME_TO_EXT;
    private static final Map<String, String> EXT_TO_MIME;

    static {
        Map<String, String> m2e = new HashMap<>();

        // canonical mapping (MIME -> extension)
        m2e.put("image/jpeg", "jpg");
        m2e.put("image/png",  "png");
        m2e.put("image/webp", "webp");
        m2e.put("image/gif",  "gif");
        m2e.put("image/avif", "avif");
        m2e.put("image/bmp",  "bmp");
        m2e.put("image/tiff", "tiff");
        m2e.put("image/svg+xml", "svg");

        MIME_TO_EXT = Collections.unmodifiableMap(m2e);

        Map<String, String> e2m = new HashMap<>();
        for (Map.Entry<String, String> e : m2e.entrySet()) {
            e2m.put(e.getValue(), e.getKey());
        }

        // aliases / normalization
        e2m.put("jpeg", "image/jpeg"); // alias -> canonical MIME
        e2m.put("tif",  "image/tiff");

        EXT_TO_MIME = Collections.unmodifiableMap(e2m);
    }

    private ImageFormatRegistry() {}

    public static String extensionFromMime(String mime) {
        String key = normalizeMime(mime);
        String ext = MIME_TO_EXT.get(key);
        if (ext == null) {
            throw new IllegalArgumentException("Unsupported mime: " + mime);
        }
        return ext;
    }

    public static String mimeFromExtension(String extension) {
        String key = normalizeExt(extension);
        String mime = EXT_TO_MIME.get(key);
        if (mime == null) {
            throw new IllegalArgumentException("Unsupported extension: " + extension);
        }
        return mime;
    }

    public static boolean isSupportedMime(String mime) {
        return MIME_TO_EXT.containsKey(normalizeMime(mime));
    }

    public static boolean isSupportedExtension(String extension) {
        return EXT_TO_MIME.containsKey(normalizeExt(extension));
    }

    private static String normalizeMime(String mime) {
        if (mime == null || mime.isBlank()) {
            throw new IllegalArgumentException("mime is null/blank");
        }
        return mime.toLowerCase(Locale.ROOT).trim();
    }

    private static String normalizeExt(String ext) {
        if (ext == null || ext.isBlank()) {
            throw new IllegalArgumentException("extension is null/blank");
        }
        String e = ext.toLowerCase(Locale.ROOT).trim();
        if (e.startsWith(".")) {
            e = e.substring(1);
        }
        return e;
    }
}