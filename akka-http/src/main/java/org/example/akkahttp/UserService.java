package org.example.akkahttp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {

  private final static List<User> users = new ArrayList<>();

  static {
    users.add(new User(1L, "Alice"));
    users.add(new User(2L, "Bob"));
    users.add(new User(3L, "Chris"));
    users.add(new User(4L, "Dick"));
    users.add(new User(5L, "Eve"));
    users.add(new User(6L, "Finn"));
  }

  public Optional<User> getUser(Long id) {
    return users.stream()
            .filter(user -> user.getId()
                    .equals(id))
            .findFirst();
  }

  public void createUser(User user) {
    users.add(user);
  }

  public List<User> getUsers(){
    return users;
  }

}
