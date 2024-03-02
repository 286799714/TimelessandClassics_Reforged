package com.tac.guns.client.resource.loader;

import com.google.common.collect.Maps;
import com.tac.guns.GunMod;
import com.tac.guns.client.resource.ClientAssetManager;
import net.minecraft.locale.Language;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LanguageLoader {
    private static final Marker MARKER = MarkerManager.getMarker("LanguageLoader");
    private static final Pattern LANG_PATTERN = Pattern.compile("^\\w+/lang/(\\w+)\\.json$");

    @Nullable
    public static boolean load(ZipFile zipFile, String zipPath) {
        Matcher matcher = LANG_PATTERN.matcher(zipPath);
        if (matcher.find()) {
            String languageCode = matcher.group(1);
            ZipEntry entry = zipFile.getEntry(zipPath);
            if (entry == null) {
                GunMod.LOGGER.warn(MARKER, "{} file don't exist", zipPath);
                return false;
            }
            try (InputStream zipEntryStream = zipFile.getInputStream(entry)) {
                Map<String, String> languages = Maps.newHashMap();
                Language.loadFromJson(zipEntryStream, languages::put);
                ClientAssetManager.INSTANCE.putLanguage(languageCode, languages);
                return true;
            } catch (IOException ioe) {
                GunMod.LOGGER.warn(MARKER, "Failed to load language file: {}", zipPath);
                ioe.printStackTrace();
            }
        }
        return false;
    }

    public static void load(File root) throws IOException {
        Path filePath = root.toPath().resolve("lang");
        if (!Files.isDirectory(filePath)) {
            return;
        }
        File[] subFiles = filePath.toFile().listFiles((dir, name) -> true);
        if (subFiles == null) {
            return;
        }
        for (File file : subFiles) {
            String name = file.getName();
            if (!name.endsWith(".json")) {
                continue;
            }
            String languageCode = name.substring(0, name.length() - 5);
            try (InputStream inputStream = Files.newInputStream(file.toPath())) {
                Map<String, String> languages = Maps.newHashMap();
                Language.loadFromJson(inputStream, languages::put);
                ClientAssetManager.INSTANCE.putLanguage(languageCode, languages);
            } catch (IOException ioe) {
                GunMod.LOGGER.warn(MARKER, "Failed to load language file: {}", file);
                ioe.printStackTrace();
            }
        }
    }
}
