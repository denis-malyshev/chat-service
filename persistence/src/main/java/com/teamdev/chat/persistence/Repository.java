package com.teamdev.chat.persistence;

import java.util.Collection;

public interface Repository<Entity> {

    Entity findById(long id);
    Collection<Entity> findAll();

    void update(Entity entity);
    void delete(long id);
}
