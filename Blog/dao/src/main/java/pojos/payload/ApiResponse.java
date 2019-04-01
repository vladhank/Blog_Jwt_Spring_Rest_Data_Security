package pojos.payload;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

	private String data;
	private Boolean success;

}
