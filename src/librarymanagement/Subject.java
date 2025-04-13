package librarymanagement;

public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers(Book book);
}
