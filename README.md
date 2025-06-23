# XShell 会话转 Tabby 导入工具
XShell 会话 密码查看工具

**[English](#english-version)** | [中文](#)

本项目旨在帮助用户将 XShell 的会话文件（`.xsh`）转换为 [Tabby](https://tabby.sh/) 可导入的格式（only test v1.0.223），支持 **XShell 7 和 XShell 8** 的会话文件。  
此外，该工具还能够 **解密加密的密码字段**，并在程序运行目录下生成一个包含所有会话信息（包括密码）的文件。

## 🧩 功能特性

- ✅ 支持读取和解析 XShell 7 和 XShell 8 的 `.xsh` 会话文件
- 🔐 支持解密加密的密码字段
- 📥 导出到 Tabby 的配置文件 config.yaml 格式
- 📁 在执行目录下生成包含所有会话信息（含密码）的文件，方便查看或备份

## 🛠 使用方法

1. 下载jar  找一个目录执行：`java -jar xshell-to-tabby.jar`
2. 程序会自动解析 `.xsh` 文件并自动写入到 Tabby 的配置文件（写入前会备份原配置文件,到程序执行目录，若出现异常，请使用该配置文件还原）。
3. 打开 Tabby，查看会话是否正常导入。

## 📄 输出文件说明

- [xshellSession.txt](file://D:\project\study\xshell-session-to-tabby\xshell-session-to-tabby\xshellSession.txt): 所有会话的完整信息，包括主机名、IP、用户名、密码等。
- [config.yaml.bak**](file://D:\project\study\xshell-session-to-tabby\xshell-session-to-tabby\config.yaml.bak1750409611636): 备份的tabby配置文件,若出现异常，请使用该配置文件还原（C:\Users\用户名\AppData\Roaming\tabby\config.yaml）。

## ⚠️ 注意事项

- 请妥善保管输出文件中的敏感信息（尤其是密码），建议使用后及时清理。
- 当前仅支持 Windows 平台下的 `.xsh` 文件解析。
- 若需要 支持更多版本欢迎 pr
- 解密算法参考：
- https://peirs.net/2020/11/xshell-session-password-retrieval/
- https://github.com/dzxs/Xdecrypt

---

<a id="english-version"></a>
# XShell Session to Tabby Importer Tool
XShell Session Password Viewer Tool

This project aims to help users convert XShell session files (`.xsh`) into [Tabby](https://tabby.sh/) importable format (tested with v1.0.223). It supports **XShell 7 and XShell 8** session files.  
Additionally, the tool can **decrypt encrypted password fields** and generates a file containing all session information (including passwords) in the program's execution directory.

## 🧩 Features

- ✅ Supports reading and parsing `.xsh` session files from XShell 7 and 8
- 🔐 Decrypts encrypted password fields
- 📥 Exports to Tabby's configuration file format (`config.yaml`)
- 📁 Generates session information files (with passwords) in the execution directory for easy viewing or backup

## 🛠 Usage

1. Download the JAR file and execute in any directory:  
   `java -jar xshell-to-tabby.jar`
2. The program will automatically parse `.xsh` files and write to Tabby's configuration file (original config is backed up to the execution directory before writing. If errors occur, use the backup to restore).
3. Open Tabby to verify successful session import.

## 📄 Output Files

- [xshellSession.txt](file://D:\project\study\xshell-session-to-tabby\xshell-session-to-tabby\xshellSession.txt): Complete session information including hostname, IP, username, password, etc.
- [config.yaml.bak**](file://D:\project\study\xshell-session-to-tabby\xshell-session-to-tabby\config.yaml.bak1750409611636): Backup of Tabby's configuration file (original location: `C:\Users\USERNAME\AppData\Roaming\tabby\config.yaml`). Use to restore if needed.

## ⚠️ Important Notes

- Securely handle sensitive information in output files (especially passwords). Recommended to delete after use.
- Currently only supports `.xsh` file parsing on Windows platforms.
- PRs welcome for additional version support.
- Decryption algorithm references:  
  [XShell Session Password Retrieval](https://peirs.net/2020/11/xshell-session-password-retrieval/)  
  [Xdecrypt GitHub Repository](https://github.com/dzxs/Xdecrypt)
