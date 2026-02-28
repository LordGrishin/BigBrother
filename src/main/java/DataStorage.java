import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

// implements pattern Singletone
public class DataStorage {
    private List slots = new ArrayList();
    private List categories = new ArrayList();
    private Hashtable data = new Hashtable();

    private static DataStorage instance = null;

    public static DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    private DataStorage() {
        slots.add("09:00 - 10:00");
        slots.add("10:00 - 11:00");
        slots.add("11:00 - 12:00");
        slots.add("12:00 - 13:00");
        slots.add("13:00 - 14:00");
        slots.add("14:00 - 15:00");
        slots.add("15:00 - 16:00");
        slots.add("16:00 - 17:00");
        slots.add("17:00 - 18:00");
        slots.add("18:00 - 19:00");
        slots.add("19:00 - 20:00");
        slots.add("20:00 - 21:00");

        categories.add("doing homework");
        categories.add("watching TV Shows");
        categories.add("watching youtube");
        categories.add("watching youtube shorts");
        categories.add("listening music");
        categories.add("having shower");
        categories.add("doing activities");
        categories.add("doing nothing");
        categories.add("chatting in telegram");


        for (int i=0; i<slots.size(); i++) {
            data.put(slots.get(i),"");
        }
    }

    public String getCategory(String slot) {
        return (String) data.get(slot);
    }

    public void setCategory(String slot, String category) {
        data.put(slot, category);
    }

    public List getSlots() {
        return slots;
    }
    public List getCategories() {
        return categories;
    }

    public void dataPush() {
        //сделать push data на сервер
    }

    //и вообще надо подумать про то как будет работать система передачи data на сервер. Когда надо будет наоборот из гита
    //скачивать файлы. Тяжело. Очень тяжело

//    public void setInterval(String interval, String begin, String end) {
//        slots.clear();
//
//        while(true){
//
//        }
//
//
//    }

 }
