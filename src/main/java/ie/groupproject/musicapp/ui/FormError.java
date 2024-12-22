package ie.groupproject.musicapp.ui;

import lombok.Getter;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.Queue;

@Getter
public class FormError {
    String fieldValue;
    String fieldName;
    Queue<String> errorMessages;

    public FormError(String fieldName, String fieldValue) {
        this.fieldValue = fieldValue;
        this.fieldName = fieldName;
        errorMessages = new LinkedList<>();
    }

    public FormError(String fieldName, String fieldValue, String errorMessage) {
        this.fieldValue = fieldValue;
        this.fieldName = fieldName;
        errorMessages = new LinkedList<>();
        errorMessages.add(errorMessage);
    }

    public void addErrorMessage(@NonNull String message) {
        errorMessages.add(message);
    }
}
