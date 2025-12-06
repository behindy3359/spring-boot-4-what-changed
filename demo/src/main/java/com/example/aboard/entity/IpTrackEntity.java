package com.example.aboard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class IpTrackEntity extends BaseTimeEntity {

    @Column(nullable = false, length = 45)
    protected String ipAddress;

    public String getMaskedIp() {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return "Unknown";
        }

        String[] parts = ipAddress.split("\\.");
        if (parts.length == 4) {
            return parts[0] + ".xxx.xxx." + parts[3];
        }
        return ipAddress;
    }

    public String getIpColorCode() {
        if (ipAddress == null) return "#808080";

        int hash = ipAddress.hashCode();
        int r = Math.max((hash & 0xFF0000) >> 16, 100);
        int g = Math.max((hash & 0x00FF00) >> 8, 100);
        int b = Math.max(hash & 0x0000FF, 100);

        return String.format("#%02X%02X%02X", r, g, b);
    }
}
