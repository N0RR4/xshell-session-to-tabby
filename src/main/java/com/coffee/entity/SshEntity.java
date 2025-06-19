package com.coffee.entity;

/**
 * type: ssh
 *     name: '222'
 *     icon: fas fa-desktop
 *     options:
 *       host: 192.168.50.200
 *       algorithms: {}
 *       input: {}
 *     weight: -1
 *     group: 5d075ee0-ee92-4fae-acd5-95cdc67ad379
 *     id: ssh:custom:222:6208bb11-60f4-4e44-9c9c-905366cd4ef6 class
 *
 * @Author LiuJun
 * @Date 2025/6/19 10:06
 */
public class SshEntity {
    private String type="ssh";
    private String name;
    private String icon="fas fa-desktop";
    private SshOptions options;
    private int weight=-1;
    private String group;
    private String id;

    public String getType() {
        return type;
    }

    public SshEntity setType(String type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public SshEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public SshEntity setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public SshOptions getOptions() {
        return options;
    }

    public SshEntity setOptions(SshOptions options) {
        this.options = options;
        return this;
    }

    public int getWeight() {
        return weight;
    }

    public SshEntity setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public SshEntity setGroup(String group) {
        this.group = group;
        return this;
    }

    public String getId() {
        return id;
    }

    public SshEntity setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String toString() {
        return "SshEntity{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", options=" + options +
                ", weight=" + weight +
                ", group='" + group + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
