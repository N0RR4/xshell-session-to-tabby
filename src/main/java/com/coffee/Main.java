package com.coffee;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.coffee.tabby.TabbyConfigUtils;
import com.coffee.xshell.XshellUtils;

import java.io.BufferedOutputStream;

/**
 * 支持命令行参数自定义路径
 * 用法:
 *   xshell-session-to-tabby.exe [选项]
 *
 * 选项:
 *   --xshell-path <路径>   XShell Sessions 目录路径（默认自动检测）
 *   --tabby-config <路径>  Tabby config.yaml 路径（默认自动检测）
 *   --output <路径>        输出的 xshellSession.txt 路径（默认：程序所在目录）
 *   --no-tabby             不写入 Tabby 配置，仅导出 txt
 *   --no-file              不生成 txt，仅写入 Tabby 配置
 *   --help                 显示帮助
 */
public class Main {
    public static void main(String[] args) {
        // 解析命令行参数
        String xshellPath = null;
        String tabbyConfig = null;
        String outputPath = null;
        boolean noTabby = false;
        boolean noFile = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--xshell-path":
                    if (i + 1 < args.length) xshellPath = args[++i];
                    break;
                case "--tabby-config":
                    if (i + 1 < args.length) tabbyConfig = args[++i];
                    break;
                case "--output":
                    if (i + 1 < args.length) outputPath = args[++i];
                    break;
                case "--no-tabby":
                    noTabby = true;
                    break;
                case "--no-file":
                    noFile = true;
                    break;
                case "--help":
                case "-h":
                    printHelp();
                    return;
                default:
                    System.out.println("未知参数: " + args[i] + "，使用 --help 查看帮助");
            }
        }

        // xshellToFile
        if (!noFile) {
            SystemUtil.set("xshellToFile", "true");
        } else {
            SystemUtil.set("xshellToFile", "false");
        }

        // xshellToTabby
        if (!noTabby) {
            String resolvedTabbyConfig = (tabbyConfig != null) ? tabbyConfig : TabbyConfigUtils.getTabbyConfigPath();
            if (FileUtil.exist(resolvedTabbyConfig)) {
                SystemUtil.set("xshellToTabby", "true");
                if (tabbyConfig != null) {
                    SystemUtil.set("tabbyConfigPath", tabbyConfig);
                }
            } else {
                System.out.println("找不到 Tabby 配置文件: " + resolvedTabbyConfig);
                System.out.println("将跳过写入 Tabby 配置（仅导出 txt）");
                SystemUtil.set("xshellToTabby", "false");
            }
        } else {
            SystemUtil.set("xshellToTabby", "false");
        }

        // 确定 XShell Sessions 路径
        String xshellSessionsPath;
        if (StrUtil.isNotEmpty(xshellPath)) {
            // 用户自定义路径：直接使用（允许指定到 Sessions 子目录或上级目录）
            if (xshellPath.endsWith("Sessions") || xshellPath.endsWith("Sessions\\")) {
                xshellSessionsPath = xshellPath;
            } else {
                xshellSessionsPath = xshellPath + "\\Xshell\\Sessions";
            }
        } else {
            String basePath = XshellUtils.getXshellConfigPath();
            if (StrUtil.isEmpty(basePath)) {
                System.out.println("无法自动检测到 XShell 配置路径，请使用 --xshell-path 指定！");
                return;
            }
            xshellSessionsPath = basePath + "\\Xshell\\Sessions";
        }

        if (!FileUtil.exist(xshellSessionsPath)) {
            System.out.println("XShell Sessions 目录不存在: " + xshellSessionsPath);
            System.out.println("请使用 --xshell-path 指定正确路径");
            return;
        }

        System.out.println("读取 XShell 会话目录: " + xshellSessionsPath);

        // 确定输出文件路径
        BufferedOutputStream outputStream = null;
        if (StrUtil.equals(SystemUtil.get("xshellToFile"), "true")) {
            String resolvedOutput;
            if (StrUtil.isNotEmpty(outputPath)) {
                resolvedOutput = outputPath;
            } else {
                resolvedOutput = TabbyConfigUtils.getWorkingDirectory() + "\\xshellSession.txt";
            }
            System.out.println("会话信息输出到: " + resolvedOutput);
            outputStream = FileUtil.getOutputStream(resolvedOutput);
        }

        XshellUtils.scanXshellSessionFiles(new java.io.File(xshellSessionsPath), outputStream);
        IoUtil.close(outputStream);

        System.out.println("\n完成！");
        if (StrUtil.equals(SystemUtil.get("xshellToTabby"), "true")) {
            System.out.println("已写入 Tabby 配置文件。");
        }
    }

    private static void printHelp() {
        System.out.println("XShell 会话转 Tabby 工具 - 使用说明");
        System.out.println("=".repeat(50));
        System.out.println("用法: xshell-session-to-tabby.exe [选项]");
        System.out.println();
        System.out.println("选项:");
        System.out.println("  --xshell-path <路径>   XShell 配置根目录或 Sessions 目录");
        System.out.println("                         默认自动检测（NetSarang Computer\\7 或 8）");
        System.out.println("  --tabby-config <路径>  Tabby config.yaml 完整路径");
        System.out.println("                         默认: %APPDATA%\\tabby\\config.yaml");
        System.out.println("  --output <路径>        输出 txt 文件的完整路径");
        System.out.println("                         默认: 程序所在目录\\xshellSession.txt");
        System.out.println("  --no-tabby             不写入 Tabby 配置，仅导出 txt");
        System.out.println("  --no-file              不生成 txt，仅写入 Tabby 配置");
        System.out.println("  --help, -h             显示此帮助");
        System.out.println();
        System.out.println("示例:");
        System.out.println("  xshell-session-to-tabby.exe");
        System.out.println("  xshell-session-to-tabby.exe --xshell-path D:\\XShell\\Sessions");
        System.out.println("  xshell-session-to-tabby.exe --tabby-config D:\\tabby\\config.yaml");
        System.out.println("  xshell-session-to-tabby.exe --output D:\\output\\sessions.txt --no-tabby");
    }
}
