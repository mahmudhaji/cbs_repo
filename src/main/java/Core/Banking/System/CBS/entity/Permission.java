package Core.Banking.System.CBS.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    ADMIN_READ("Admin :read"),

    ADMIN_UPDATE("Admin :update"),
    ADMIN_CREATE("Admin :create"),
    ADMIN_DELETE("Admin :delete"),


    USER_READ("User:read"),
    USER_UPDATE("User:read"),
    USER_DELETE("User:delete"),
    USER_CREATE("User:create")


    ;
    @Getter
    private final String permission;
}

