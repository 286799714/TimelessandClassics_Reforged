package com.tac.guns.util;

import com.tac.guns.GunMod;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

public final class GetJarResources {
    private GetJarResources() {
    }

    /**
     * 复制本模组的文件到指定文件夹
     *
     * @param filePath jar 里面的文件地址
     * @param destPath 想要复制到的目录
     * @param fileName 复制后的文件名
     */
    public static void copyModFile(String filePath, Path destPath, String fileName) {
        URL url = GunMod.class.getResource(filePath);
        try {
            if (url != null) {
                FileUtils.copyURLToFile(url, destPath.resolve(fileName).toFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public static InputStream readModFile(String filePath) {
        URL url = GunMod.class.getResource(filePath);
        try {
            if (url != null) {
                return url.openStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}