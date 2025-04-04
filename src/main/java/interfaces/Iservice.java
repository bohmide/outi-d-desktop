package interfaces;

import java.util.List;

public interface Iservice <T>{

    public void addEntity(T t);
    public void updateEntityById(T t);
    public void deleteEntityById(T t);
    public List<T> listEntity();
}
