package database;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Storage {
    public Map<String, List<String>> map;
    public void searchUser(String id, String group) {
        if (map.get(id) == null) {
            addNewUser(id, group);
        } else {
            addInfo2User(id,group);
        }
    }
    public Storage() {
        map = new HashMap();
    }
    private void addNewUser(String id, String group) {
        List<String> newList = new LinkedList<>();
        newList.add(group);
        map.put(id, newList);
    }
    private void addInfo2User(String id, String group) {
        List<String> updatedList = map.get(id);
        updatedList.add(group);
        map.put(id,updatedList);
    }


    public static void main(String[] args){
        Storage temp = new Storage();
        temp.searchUser("Abc","1");
        temp.searchUser("Abc","2");
        System.out.println(temp.map.get("Abc"));
    }
}

