package ru.nikitars.mytasks.db.models;

public class MyList {
    private int id;
    private String listName;
    private int themeType;
    private int themeID;
    private String listOrder;

    public MyList(int id, String listName, int themeType, int themeID, String listOrder) {
        this.id = id;
        this.listName = listName;
        this.themeType = themeType;
        this.themeID = themeID;
        this.listOrder = listOrder;
    }

    public int[] getListOrderArray(){
        String[] splited = listOrder.split("\\s+");
        int counter = 0;
        for(String taskIDstr : splited){
            try{
                if (Integer.parseInt(taskIDstr) >= 0)
                    counter++;
            } catch (NumberFormatException e) {}
        }
        int[] listOrderLinkedList = new int[counter];
        counter = 0;
        for(String taskIDstr : splited){
            try{
                if (Integer.parseInt(taskIDstr) >= 0){
                    listOrderLinkedList[counter] = Integer.parseInt(taskIDstr);
                    counter++;
                }
            } catch (NumberFormatException e) {}
        }
        return listOrderLinkedList;
    }
    public void setTaskPosition(int taskID, int taskPos){
        int[] tasks = getListOrderArray();
        boolean isThere = false;
        for (int task : tasks){
            if (task == taskID) isThere = true;
        }
        if (isThere){
            listOrder = "";
            for (int  i = 0; i < tasks.length; i++){
                if (taskPos == i){
                    listOrder += taskID + " ";
                }
                if (tasks[i] != taskID){
                    listOrder += tasks[i] + " ";
                }
            }
        }
    }
    public void addTaskToOrderList(int taskID){
        int[] tasks = getListOrderArray();
        listOrder = "";
        for (int task : tasks){
            listOrder += task + " ";
        }
        listOrder += taskID + " ";
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getListName() {
        return listName;
    }
    public void setListName(String listName) {
        this.listName = listName;
    }

    public int getThemeType() {
        return themeType;
    }
    public void setThemeType(int themeType) {
        this.themeType = themeType;
    }

    public int getThemeID() {
        return themeID;
    }
    public void setThemeID(int themeID) {
        this.themeID = themeID;
    }

    public String getListOrder() {
        return listOrder;
    }
    public void setListOrder(String listOrder) {
        this.listOrder = listOrder;
    }
}
