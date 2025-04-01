package dat.daos;

import java.util.List;

public interface IDAO<T, I> {
    T create(T t);

    T getById(I id);

    List<T> getAll();

    T update(I id, T t);

    void delete(I id);
}
