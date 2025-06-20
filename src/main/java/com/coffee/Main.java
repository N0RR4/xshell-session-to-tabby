package com.coffee;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.coffee.tabby.TabbyConfigUtils;
import com.coffee.xshell.XshellUtils;

import java.io.BufferedOutputStream;


public class Main {
    public static void main(String[] args) {
        if (StrUtil.isEmpty(SystemUtil.get("xshellToFile"))) {
            SystemUtil.set("xshellToFile", "true");
        }
        if (StrUtil.isEmpty(SystemUtil.get("xshellToTabby"))) {
            String tabbyConfigPath = TabbyConfigUtils.getTabbyConfigPath();
            if(FileUtil.exist(tabbyConfigPath)){
                SystemUtil.set("xshellToTabby", "true");
            }else {
                System.out.println("can not find tabby config path!");
                SystemUtil.set("xshellToTabby", "false");
            }
        }
        String xshellConfigPath = XshellUtils.getXshellConfigPath()+"\\Xshell\\Sessions";
        if (StrUtil.isNotEmpty(xshellConfigPath)) {
            String workingDirectory = TabbyConfigUtils.getWorkingDirectory();
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


}