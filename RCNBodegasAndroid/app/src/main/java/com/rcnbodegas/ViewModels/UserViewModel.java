package com.rcnbodegas.ViewModels;

public class UserViewModel {
    public int id ;

    public String name ;

    public String userName ;

    public int roleId ;

    public String roleName ;

    public UserViewModel() {
    }

    public UserViewModel(int id, String name, String userName, int roleId, String roleName) {
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
