package in.ankitdaksh.billingsoftware.io;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ItemRequest {

    private String name;
    private BigDecimal price;
    private String categoryId;
    private String description;
}
