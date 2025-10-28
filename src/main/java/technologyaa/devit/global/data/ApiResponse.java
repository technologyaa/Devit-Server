package technologyaa.devit.global.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.netty.channel.unix.Errors;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    int status,
    T data
    ) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), data);
    }
}
