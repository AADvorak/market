package net.thumbtack.school.market.exception;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import org.springframework.graphql.execution.ErrorType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityNotFoundException extends RuntimeException implements GraphQLError {

    private final Map<String, Object> extensions;

    public EntityNotFoundException(String field, int value) {
        super("Entity is not found");
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("field", field);
        extensions.put("value", value);
        this.extensions = extensions;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorClassification getErrorType() {
        return ErrorType.NOT_FOUND;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }
}
