package com.forum.javaforum.utilities.mappers;

import com.forum.javaforum.models.publication.Post;
import com.forum.javaforum.models.publication.PostDto;
import com.forum.javaforum.models.publication.Tag;
import com.forum.javaforum.models.users.User;
import com.forum.javaforum.services.contracts.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PostMapper {

    private final TagService tagService;

    @Autowired
    public PostMapper(TagService tagService) {
        this.tagService = tagService;
    }

    public Post fromDto(PostDto postDto, User user) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setAuthor(user);
        post.setDate(LocalDateTime.now());
        return post;
    }

    public Post fromDto(Post post, PostDto postDto, User user) {
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        updateTags(post, postDto, user);
        return post;
    }
    public PostDto toDto(Post post) {
        PostDto postDto = new PostDto();
        postDto.setTitle(post.getTitle());
        postDto.setContent(post.getContent());
        convertTagsSetToList(post, postDto);
        populateTagFields(postDto, post);
        return postDto;
    }

    public void updateTags(Post post, PostDto postDto, User user) {
        collectTagFields(postDto);
        if (tagsHaveBeenModified(post, postDto)) {
            deleteOldTags(post, user);
            assignNewTags(post.getId(), postDto, user);
        }
    }

    private boolean tagsHaveBeenModified(Post post, PostDto postDto) {
        Set<String> current = post.getTags() == null ? new HashSet<>() :
                post.getTags().stream().map(Tag::getName).collect(Collectors.toSet());
        Set<String> updated = postDto.getTags() == null ? new HashSet<>() :
                new HashSet<>(postDto.getTags());
        if ((!current.isEmpty()) && (updated.isEmpty())) return true;
        else return !(current.equals(updated));
    }

    private void assignNewTags(int postID, PostDto postDto, User user) {
        Set<String> updated = new HashSet<>(postDto.getTags());
        if (!(updated.isEmpty()))
            updated.forEach(tagName -> tagService.addTag(postID, tagName, user));
    }

    private void deleteOldTags(Post post, User user) {
        Set<Tag> current = post.getTags() == null ? new HashSet<>() : post.getTags();
        current.forEach(tag -> tagService.removeTag(post.getId(), tag.getName(), user));
    }

    private void collectTagFields(PostDto postDto) {
        if (postDto.getTags() == null) postDto.setTags(new ArrayList<>());
        addTagIfPresent(postDto, postDto.getTag1());
        addTagIfPresent(postDto, postDto.getTag2());
        addTagIfPresent(postDto, postDto.getTag3());
        addTagIfPresent(postDto, postDto.getTag4());
        addTagIfPresent(postDto, postDto.getTag5());
    }

    private void addTagIfPresent(PostDto post, String tag) {
        if (!(tag == null || tag.isBlank())) post.getTags().add(tag);
    }

    private void populateTagFields(PostDto postDto, Post post) {
        if (post.getTags() == null) post.setTags(new HashSet<>());
        convertTagsSetToList(post, postDto);
        if (postDto.getTags() == null) postDto.setTags(new ArrayList<>());
        if (postDto.getTags().size() > 0) postDto.setTag1(postDto.getTags().get(0));
        if (postDto.getTags().size() > 1) postDto.setTag2(postDto.getTags().get(1));
        if (postDto.getTags().size() > 2) postDto.setTag3(postDto.getTags().get(2));
        if (postDto.getTags().size() > 3) postDto.setTag4(postDto.getTags().get(3));
        if (postDto.getTags().size() > 4) postDto.setTag5(postDto.getTags().get(4));
    }

    private void convertTagsSetToList(Post post, PostDto postDto) {
        postDto.setTags((post.getTags().
                stream().map(Tag::getName).
                sorted().collect(Collectors.toList())));
    }
}
