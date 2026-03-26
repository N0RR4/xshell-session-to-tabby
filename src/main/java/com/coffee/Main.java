package com.coffee;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.coffee.xshell.XshellUtils;

import java.io.BufferedOutputStream;

/**
 * XShell 会话密码解密工具
 * 用法:
 *   xshell-session-to-tabby.exe [选项]
 *
 * 选项:
 *   --xshell-path <路径>   XShell Sessions 目录路径（默认自动检测）
 *   --output <路径>        输出的 txt 文件完整路径（默认：程序所在目录\xshellSession.txt）
 *   --help, -h             显示帮助
 */
public class Main {
    public static void main(String[] args) {
        String xshellPath = null;
        String outputPath = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--xshell-path":
                    if (i + 1 < args.length) xshellPath = args[++i];
                    break;
                case "--output":
                    if (i + 1 < args.length) outputPath = args[++i];
                    break;
                case "--help":
                case "-h":
                    printHelp();
                    return;
                default:
                    System.out.println("未知参数: " + args[i] + "，使用 --help 查看帮助");
            }
        }

        // 确定 XShell Sessions 路径
        String xshellSessionsPath;
        if (StrUtil.isNotEmpty(xshellPath)) {
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
        String resolvedOutput;
        if (StrUtil.isNotEmpty(outputPath)) {
            resolvedOutput = outputPath;
        } else {
            resolvedOutput = System.getProperty("user.dir") + "\\xshellSession.txt";
        }
        System.out.println("输出到: " + resolvedOutput);

        BufferedOutputStream outputStream = FileUtil.getOutputStream(resolvedOutput);
        XshellUtils.scanXshellSessionFiles(new java.io.File(xshellSessionsPath), outputStream);
        IoUtil.close(outputStream);

        System.out.println("\n完成！结果已保存到: " + resolvedOutput);
    }

    private static void printHelp() {
        System.out.println("XShell 会话密码解密工具");
        System.out.println("=".repeat(50));
        System.out.println("用法: xshell-session-to-tabby.exe [选项]");
        System.out.println();
        System.out.println("选项:");
        System.out.println("  --xshell-path <路径>   XShell 配置根目录或 Sessions 目录");
        System.out.println("                         默认自动检测（NetSarang Computer\\7 或 8）");
        System.out.println("  --output <路径>        输出 txt 文件的完整路径");
        System.out.println("                         默认: 程序所在目录\\xshellSession.txt");
        System.out.println("  --help, -h             显示此帮助");
        System.out.println();
        System.out.println("示例:");
        System.out.println("  xshell-session-to-tabby.exe");
        System.out.println("  xshell-session-to-tabby.exe --xshell-path D:\\XShell\\Sessions");
        System.out.println("  xshell-session-to-tabby.exe --output D:\\output\\sessions.txt");
    }
}
