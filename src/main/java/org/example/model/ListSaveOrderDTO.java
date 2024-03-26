package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListSaveOrderDTO {
    private List<ErrorDTO> listErrors;
    private List<SaveOrderDTO> listOrder;
}
