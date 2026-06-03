package com.hms.models;

public class Role{
    private int roleID;
    private String roleName;

    //Default constructor
    public Role(){}

    //Parameterized constructor
    public Role(int roleID, String roleName){
        this.roleID = roleID;
        this.roleName = roleName;
    }

    //Getters and setters
    public int getRoleID(){ return roleID; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    //toString - useful for debugging
    @Override
    public String toString(){
        return "Role{" + 
                "roleID=" + roleID +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}