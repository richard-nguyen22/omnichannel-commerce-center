package com.omnichannel.center.domain.user;

import java.time.Instant;
import java.util.UUID;

public class UserPermissionLink {
    private UUID userGuid;
    private UUID permissionGuid;
    private Instant createdDate;

    public UUID getUserGuid() {
        return userGuid;
    }

    public void setUserGuid(UUID userGuid) {
        this.userGuid = userGuid;
    }

    public UUID getPermissionGuid() {
        return permissionGuid;
    }

    public void setPermissionGuid(UUID permissionGuid) {
        this.permissionGuid = permissionGuid;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }
}
