package services;

import pojos.Tag;

import java.util.List;

public interface TagService extends IService<Tag> {

    Tag findOrCreateTagByName(String name);

    List<Tag> findAll();

    Tag findByName(String name);

    void deleteTagByName(String name);

}
