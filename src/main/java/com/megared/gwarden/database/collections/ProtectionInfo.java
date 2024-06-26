package com.megared.gwarden.database.collections;

public class ProtectionInfo {
    private int currentKills = 0, requiredKills = 0;
    private long warExpireTime = 0, shieldExpireTime = 0;

    public ProtectionInfo() {}

    public long getShieldExpireTime() {
        return shieldExpireTime;
    }

    public void setShieldExpireTime(long shieldExpireTime) {
        this.shieldExpireTime = shieldExpireTime;
    }

    public long getWarExpireTime() {
        return warExpireTime;
    }

    public void setWarExpireTime(long warExpireTime) {
        this.warExpireTime = warExpireTime;
    }

    public int getCurrentKills() {
        return currentKills;
    }

    public void setCurrentKills(int currentKills) {
        this.currentKills = currentKills;
    }

    public int getRequiredKills() {
        return requiredKills;
    }

    public void setRequiredKills(int requiredKills) {
        this.requiredKills = requiredKills;
    }
}
