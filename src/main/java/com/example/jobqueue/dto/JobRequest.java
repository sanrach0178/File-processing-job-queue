import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JobRequest {
    @NotBlank(message = "Payload cannot be empty")
    @Size(max = 255, message = "Payload is too long")
    private String payload;
}
