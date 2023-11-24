package net.thumbtack.school.market.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorException;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CustomDataFetcherExceptionResolverAdapter extends DataFetcherExceptionResolverAdapter {

    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof AccessDeniedException) {
            return null;
        }
        if (ex instanceof GraphQLError) {
            return (GraphQLError) ex;
        }
        return GraphqlErrorException.newErrorException().message(ex.getMessage()).cause(ex).build();
    }

    protected List<GraphQLError> resolveToMultipleErrors(Throwable ex, DataFetchingEnvironment env) {
        List<GraphQLError> errors = new ArrayList<>();
        if (ex instanceof ConstraintViolationException cvEx) {
            cvEx.getConstraintViolations().forEach(constraintViolation ->
                    errors.add(GraphqlErrorException.newErrorException().message(constraintViolation.getMessage())
                            .path(List.of(constraintViolation.getPropertyPath().toString()))
                            .extensions(createFieldErrorExtensions(constraintViolation))
                            .errorClassification(ErrorType.BAD_REQUEST)
                            .cause(ex).build()));
            return errors;
        }
        GraphQLError error = resolveToSingleError(ex, env);
        if (error == null) {
            return null;
        }
        errors.add(resolveToSingleError(ex, env));
        return errors;
    }

    private Map<String, Object> createFieldErrorExtensions(ConstraintViolation<?> constraintViolation) {
        Map<String, Object> extensions = new HashMap<>();
        String[] pathSplit = constraintViolation.getPropertyPath().toString().split("\\.");
        extensions.put("field", pathSplit[pathSplit.length - 1]);
        return extensions;
    }

}
