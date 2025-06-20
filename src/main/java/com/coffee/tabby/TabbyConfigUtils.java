package com.coffee.tabby;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.yaml.YamlUtil;
import com.coffee.entity.SshEntity;
import com.coffee.entity.SshOptions;
import org.yaml.snakeyaml.DumperOptions;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 读取Tabby配置文件
 *
 * @Author LiuJun
 * @Date 2025/6/18 16:38
 */

public class TabbyConfigUtils {

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

    public static String getTabbyConfigPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return System.getenv("APPDATA") + "\\Tabby\\config.yaml";
        } else {
            return System.getProperty("user.home") + "/.tabby/config.yaml";
        }
    }

    public  static void backConfig(String configPath) {
        long time = DateUtil.date().getTime();
        String workingDirectory = TabbyConfigUtils.getWorkingDirectory();

        String path = workingDirectory+ FileUtil.FILE_SEPARATOR + FileNameUtil.getName(configPath) +".bak" + time;
        System.out.println("正在备份Tabby配置文件到 :" + path);
        FileUtil.copy(configPath, path, true);
    }

    public  static Map<String, Object> readConfig(String configPath) {
        if(StrUtil.isEmpty(configPath)){
            configPath = getTabbyConfigPath();
        }
        if(!FileUtil.exist(configPath)){
            System.out.println("Tabby配置文件不存在 可能不兼容 即将结束本程序 ");
            throw new RuntimeException("Tabby配置文件不存在");
        }
        Map<String, Object> yamlMap = YamlUtil.loadByPath(configPath);
        //System.out.println(yamlMap);
        return yamlMap;
    }

    public static void writeConfig(Map<String, Object> yamlMap, String configPath) {
        if(StrUtil.isEmpty(configPath)){
            configPath = getTabbyConfigPath();
        }
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // 使用块风格（默认）
        options.setIndent(4); // 缩进 2 空格
        options.setIndicatorIndent(2); // `-` 前面缩进 2 空格
        options.setPrettyFlow(false); // 美化流式风格（如 {} 和 []）
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN); // 纯文本风格（不添加引号）
        backConfig(configPath);
        YamlUtil.dump(yamlMap, FileUtil.getWriter(configPath, Charset.defaultCharset(),false), options);
    }

    public static List<Map<String, Object>> getGroups(Map<String, Object> yamlMap) {
        List<Map<String, Object>> groups = (List<Map<String, Object>>) yamlMap.get("groups");
        return groups;
    }


    public static  void  removeGroup(Map<String, Object> yamlMap,String groupName) {
        List<Map<String, Object>> groups = getGroups(yamlMap);
        //判断是否存在
        for (Map<String, Object> group : groups) {
            if (group.get("name").equals(groupName)) {
               groups.remove(group);
               return;
            }
        }
    }

    public static  void  addGroup(Map<String, Object> yamlMap,  String id,String groupName) {
        List<Map<String, Object>> groups = getGroups(yamlMap);
        //判断是否存在
        for (Map<String, Object> group : groups) {
            if (group.get("name").equals(groupName)) {
                return;
            }
        }
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("name", groupName);
        groups.add(map);
    }

    public static List<SshEntity> getSshEntities(Map<String, Object> yamlMap){
        Object profilesObj = yamlMap.get("profiles");
        if(profilesObj == null){
            return CollUtil.newArrayList();
        }
        if(profilesObj instanceof List){
            List<Map<String, Object>> profiles = (List<Map<String, Object>>) profilesObj;
            return profiles.stream()
                    .filter(profile -> profile.get("type").equals("ssh"))
                    .map(profile -> {
                SshEntity ssh = BeanUtil.toBean(profile, SshEntity.class);
                return ssh;
            }).collect(Collectors.toList());
        }else {
            return CollUtil.newArrayList();
        }
    }

    public static void addSshEntity(Map<String, Object> yamlMap, SshEntity sshEntity) {
        List<Map<String, Object>> profiles = (List<Map<String, Object>>) yamlMap.get("profiles");
        if(profiles == null){
            profiles = CollUtil.newArrayList();
            yamlMap.put("profiles", profiles);
        }
        //判断是否存在
        String key = sshEntity.getOptions().getHost() + "_" + sshEntity.getOptions().getPort() + "_" + sshEntity.getOptions().getUser();
        List<Map<String, Object>> sshProfiles = profiles.stream().filter(profile -> profile.get("type").equals("ssh")).collect(Collectors.toList());
        for (Map<String, Object> profile : sshProfiles) {
            //以 host  port user 为 唯一键 判断
            Map<String, Object> options = (Map<String, Object>) profile.get("options");
            String profileKey =  options.get("host")+"_" + options.get("port")+"_"+options.get("user");
            if(profileKey.equals(key)){
                return;
            }
        }
        Map<String, Object> map = BeanUtil.beanToMap(sshEntity);
        map.put("options", BeanUtil.beanToMap(sshEntity.getOptions()));
        profiles.add(map);
    }


    public static void main(String[] args) {
        Map<String, Object> yamlMap = readConfig(null);
        addGroup(yamlMap, IdUtil.fastUUID(), "test2");
//        removeGroup(yamlMap, "test2");
//        writeConfig(yamlMap, null);
        List<SshEntity> sshEntities = getSshEntities(yamlMap);
        for (SshEntity sshEntity : sshEntities) {
            System.out.println(sshEntity);
        }
        SshEntity sshEntity = new SshEntity();
        String name =   "test";
        String id = "ssh:custom:"+name+":"+IdUtil.fastUUID();
        sshEntity.setId(id);
        sshEntity.setName(name);
        sshEntity.setGroup("3ff38f32-74e5-4e03-95a1-93a7c5599cc9");
        sshEntity.setOptions(new SshOptions().setHost("192.168.1.1").setPort(22));
        addSshEntity(yamlMap, sshEntity);
        writeConfig(yamlMap, null);
    }


}
