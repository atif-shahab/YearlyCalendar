import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WeekList  implements Iterable<List<Day>> {
    private final List<Day> arrayList;
    private final int size;
    public WeekList( List<Day> newArray) {
        this.arrayList = newArray;
        this.size = arrayList.size();
    }
    @Override
    public Iterator<List<Day>> iterator() {
        return new Iterator<>() {
            private int currentIndex = 0;
            @Override
            public boolean hasNext() {
                return (currentIndex + 7 <= size);
            }
            @Override
            public List<Day> next() {
                List<Day> week = new ArrayList<>();
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
    }
}
