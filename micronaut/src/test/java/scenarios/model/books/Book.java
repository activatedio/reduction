package scenarios.model.books;

import java.util.Objects;

public class Book {
  private int id;
  private String name;
  private int authorId;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAuthorId() {
    return authorId;
  }

  public void setAuthorId(int authorId) {
    this.authorId = authorId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Book book = (Book) o;
    return id == book.id && authorId == book.authorId && Objects.equals(name, book.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, authorId);
  }

  @Override
  public String toString() {
    return "Book{" + "id=" + id + ", name='" + name + '\'' + ", authorId=" + authorId + '}';
  }
}
