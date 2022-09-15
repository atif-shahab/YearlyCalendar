import java.util.ArrayList;
import java.util.Iterator;

public class WeekList  implements Iterable {
    private ArrayList<Day> arrayList;
    private int size;
    public WeekList( ArrayList<Day> newArray) {
        this.arrayList = newArray;
        this.size = arrayList.size();
    }
    @Override
    public Iterator iterator() {
        Iterator it = new Iterator() {
            private int currentIndex = 0;
            @Override
            public boolean hasNext() {
                return (currentIndex + 7 <= size);
            }
            @Override
            public ArrayList<Day> next() {
                ArrayList<Day> week = new ArrayList<>();
                for(int i = 0; i<7; i++) {
                    week.add(arrayList.get(currentIndex++));
                }
                return week;
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }
}
