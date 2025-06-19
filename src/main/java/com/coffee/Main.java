package com.coffee;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.coffee.xshell.XshellUtils;

import java.io.BufferedOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (StrUtil.isEmpty(SystemUtil.get("xshellToFile"))) {
            SystemUtil.set("xshellToFile", "true");
        }
        if (StrUtil.isEmpty(SystemUtil.get("xshellToTabby"))) {
            SystemUtil.set("xshellToTabby", "true");
        }
        String xshellConfigPath = XshellUtils.getXshellConfigPath()+"\\Xshell\\Sessions";
        if (StrUtil.isNotEmpty(xshellConfigPath)) {
            String workingDirectory = getWorkingDirectory();
            String filePath = workingDirectory + "\\xshellSession.txt";
            System.out.println("read xshell session path : "+xshellConfigPath);
            BufferedOutputStream outputStream = null;
            if (StrUtil.equals(SystemUtil.get("xshellToFile"),"true")) {
                System.out.println("xshell session save path: " + filePath);
                outputStream = FileUtil.getOutputStream(filePath);
            }
            XshellUtils.scanXshellSessionFiles(new java.io.File(xshellConfigPath), outputStream);
            IoUtil.close(outputStream);
        }else {
            System.out.println("can not find xshell session path!");
        }
    }

    public static String getCurrentDir() {
        try {
            // 这种方法在普通JVM和native-image中都有效
            Path path = Paths.get("").toAbsolutePath();
            return path.toString();
        } catch (Exception e) {
            // 备用方案
            return System.getProperty("user.dir");
        }
    }

    public static String getWorkingDirectory() {
        return System.getProperty("user.dir");
    }
}