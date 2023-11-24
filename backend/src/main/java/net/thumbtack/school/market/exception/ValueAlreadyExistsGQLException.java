package net.thumbtack.school.market.exception;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import org.springframework.graphql.execution.ErrorType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValueAlreadyExistsGQLException extends RuntimeException implements GraphQLError {

    private final Map<String, Object> extensions;

    public ValueAlreadyExistsGQLException(String field, String value) {
        super("Value " + value + " of " + field + " already exists");
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
        return ErrorType.BAD_REQUEST;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }
}
