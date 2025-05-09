package interfaces;

import java.util.List;

public interface Iservice <T>{
    public void addEntite(T t);
    public List<T> listEntite();
    public void updateEntite(T t);
    public void deleteEntite(T t);
}
