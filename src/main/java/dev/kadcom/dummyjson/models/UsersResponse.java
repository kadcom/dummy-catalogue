package dev.kadcom.dummyjson.models;

import java.util.List;

public class UsersResponse {
    private List<User> users;
    private int total;
    private int skip;
    private int limit;
    
    public List<User> getUsers() { return users; }
    public int getTotal() { return total; }
    public int getSkip() { return skip; }
    public int getLimit() { return limit; }
    
    public boolean hasMore() {
        return skip + limit < total;
    }
    
    public int getCurrentPage() {
        return limit > 0 ? (skip / limit) + 1 : 1;
    }
    
    public int getTotalPages() {
        return limit > 0 ? (int) Math.ceil((double) total / limit) : 1;
    }
    
    @Override
    public String toString() {
        return "UsersResponse{" +
                "total=" + total +
                ", skip=" + skip +
                ", limit=" + limit +
                ", usersCount=" + (users != null ? users.size() : 0) +
                '}';
    }
}