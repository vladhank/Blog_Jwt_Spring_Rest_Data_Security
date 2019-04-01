package services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class ArticleInputException extends RuntimeException {

    private String title;
    private String text;
    private String tagName;

    public ArticleInputException(String title, String text, String tagName) {
        String.format("Data entered incorrectly:", title + " " + text + " " + tagName);
        this.title = title;
        this.text = text;
        this.tagName = tagName;
    }
}
