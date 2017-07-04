package br.com.mirabilis.schedulesample.schedule.model;

/**
 * Created by rodrigosimoesrosa
 */
public enum Period{
    MORNING("MORNING",1),AFTERNOON("AFTERNOON",2),NIGHT("NIGHT",3);

    private final String value;
    private final int id;

    Period(String value, int id) {
        this.value = value;
        this.id = id;
    }

    @Override
    public String toString() {
        return value;
    }

    public int getId() {
        return id * 1000;
    }

    public static Period[] toArray(String[] periods) {
        Period[] temp = new Period[periods.length];
        for(int i =0; i< periods.length; i++){
            temp[i] = Period.valueOf(periods[i].toString());
        }
        return temp;
    }

    public static String[] toArray(Period... periods) {
        String[] temp = new String[periods.length];
        for(int i =0; i< periods.length; i++){
            temp[i] = periods[i].toString();
        }
        return temp;
    }
}

