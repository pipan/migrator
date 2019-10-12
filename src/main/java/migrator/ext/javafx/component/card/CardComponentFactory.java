package migrator.ext.javafx.component.card;

public interface CardComponentFactory<T> {
    public CardComponent<T> create(Card<T> card);
}