package services.impl;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojos.Tag;
import repositories.TagRepository;
import services.TagService;
import services.exceptions.NotFoundException;
import services.exceptions.ServiceException;

import java.util.List;
import java.util.Optional;

@Service("tagService")
@Transactional
@NoArgsConstructor
public class TagServiceImpl extends BaseService<Tag> implements TagService {

    @Autowired
    private TagRepository tagRepository;

    public Tag findOrCreateTagByName(String tagName) {
        return Optional.ofNullable(tagRepository.findByName(tagName))
                .orElseGet(() -> tagRepository.save(new Tag(tagName)));
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public Tag findByName(String name) {
        return tagRepository.findByName(name);
    }

    public void deleteTagByName(String tagName) {
        Optional.ofNullable(tagRepository.findByName(tagName))
                .orElseThrow(()->new NotFoundException("Could't find tag with name {"+tagName+"}"));
        tagRepository.deleteTagByName(tagName);
    }

}
