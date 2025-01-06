package ie.groupproject.musicapp.ui;

import lombok.*;

import java.util.*;

/**
 * @author Filip Vojtěch
 */
@NoArgsConstructor
public class Form {
    private final Map<String, FormField> formFields = new HashMap<>();
    @Getter
    private final Queue<String> formErrors = new LinkedList<>();

    /**
     * Add field to the form.
     *
     * @param name  Name of the field (as name HTML attr.).
     * @param value Value of the field.
     * @return Reference to the newly created field.
     */
    public FormField addField(String name, String value) {
        FormField field = new FormField(name, value);
        formFields.put(name, field);
        return field;
    }

    /**
     * Get a field of the form.
     *
     * @param name Name of the field.
     * @return The field with the name or null if the field is not in the form.
     */
    public FormField getField(String name) {
        return formFields.get(name);
    }

    /**
     * Add a validation error for the form
     *
     * @param message Error message
     */
    public void addError(String message) {
        formErrors.add(message);
    }

    /**
     * Add an error to a field
     *
     * @param fieldName Name of the field
     * @param message   Error message
     * @return True if the error was added, false otherwise.
     */
    public boolean addError(String fieldName, String message) {
        var field = formFields.get(fieldName);
        if (field == null) return false;
        field.addError(message);
        return true;
    }

    /**
     * Check that the form is valid.
     *
     * @return True if the form is valid, false if any of the form's fields have an error.
     */
    public boolean isValid() {
        if (!formErrors.isEmpty()) return false;

        for (var field : formFields.values()) {
            if (!field.isValid()) return false;
        }
        return true;
    }

    /**
     * Check is field is valid.
     *
     * @param fieldName Name of the field
     * @return True if the field is valid. False if the field is not valid.
     */
    public boolean isValid(String fieldName) {
        var field = formFields.get(fieldName);
        if (field == null) return true;
        return field.isValid();
    }

    /**
     * Get errors for a field
     *
     * @param fieldName Name of the field
     * @return Collection of all the errors for the field (if any). Null if that field does not exist.
     */
    public Collection<String> getErrors(String fieldName) {
        var field = formFields.get(fieldName);
        if (field == null) return null;
        return field.getErrorMessages();
    }

    /**
     * Check if a field has a value
     *
     * @param fieldName Name of the field
     * @return True if the field has a value. False otherwise.
     */
    public boolean hasValue(String fieldName) {
        var field = formFields.get(fieldName);
        if (field == null) return false;
        return field.value != null;
    }

    /**
     * Get a value from a field.
     *
     * @param fieldName Name of the field.
     * @return Value of the field. Empty string if no value or the field does not exist.
     */
    public String getValue(String fieldName) {
        var field = formFields.get(fieldName);
        if (field == null) return "";
        return field.value;
    }

    /**
     * Object representing the form field.
     */
    @NoArgsConstructor
    @Getter
    public static class FormField {
        /**
         * HTML name attribute
         */
        @Setter
        @NonNull
        private String name;
        @Setter
        private String value = null;
        Queue<String> errorMessages;

        public FormField(@NonNull String name, String value) {
            this.name = name;
            this.value = value;
            errorMessages = new LinkedList<>();
        }

        /**
         * Add an error to the field.
         *
         * @param message Message of the error.
         */
        public void addError(@NonNull String message) {
            errorMessages.add(message);
        }

        /**
         * Check if the field is valid
         *
         * @return True if the field is valid, false otherwise.
         */
        public boolean isValid() {
            return errorMessages.isEmpty();
        }
    }
}
