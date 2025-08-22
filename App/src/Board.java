import java.util.*;

public class Board {
    public List<BoardItem> items = new ArrayList<>();

    public Board(){
        generateItems();
    }

    private void generateItems() {

//        int[] snake_start = {17,62,54,87,64,98,95,93};
//        int[] snake_end = {17,62,54,87,64,98,95,93};
//        int[] snake_start = {17,62,54,87,64,98,95,93};
//        int[] snake_start = {17,62,54,87,64,98,95,93};

        items.add(new Ladder(1,38));
        items.add(new Ladder(4,14));
        items.add(new Ladder(9,31));
        items.add(new Ladder(21,42));
        items.add(new Ladder(28,84));
        items.add(new Ladder(51,67));
        items.add(new Ladder(80,99));
        items.add(new Ladder(72,91));


        items.add(new Snake(17,7));
        items.add(new Snake(62,19));
        items.add(new Snake(54,34));
        items.add(new Snake(87,36));
        items.add(new Snake(64,60));
        items.add(new Snake(98,79));
        items.add(new Snake(95,75));
        items.add(new Snake(93,73));

    }

    public List<BoardItem> getItems(){
        return items;
    }

    public int checkItem(int pos){
        for(BoardItem item : items){
            if(item.getStart() == pos) return item.getEnd();
        }
        return pos;
    }

    public int getstrat(int pos){
        for(BoardItem item : items){
            if(item.getStart() == pos) return item.getStart();
        }
        return pos;
    }

    public int getRow(int pos){
        return (pos - 1)/10;
    }
}
