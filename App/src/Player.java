public abstract class Player {
    public String name;
    public int position;

    public Player(String name, int position){
        this.name = name;
        this.position = position;
    }

    public void move(int step){
        position = step + position;
        if(position > 100){
            position = 100;
        }
    }

    // setter and getter
    public int getPosition(){return position;}
    public String getName(){return name;}
    public void setName(String name){this.name = name;}
    public void setPosition(int position){this.position = position;}
}
