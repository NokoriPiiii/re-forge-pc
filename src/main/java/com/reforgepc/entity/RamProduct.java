package com.reforgepc.entity;

import com.reforgepc.dto.SpecItemDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ram_products")
@Getter
@Setter
@NoArgsConstructor
public class RamProduct extends Product {

    private String capacity;
    private String memoryType;
    private String memorySpeed;
    private Integer moduleCount;
    private String voltage;
    private String formFactor;

    @Override
    public List<SpecItemDto> getSpecs() {
        List<SpecItemDto> list = new ArrayList<>();
        if (getBrand() != null)
            list.add(new SpecItemDto("Hãng sản xuất", getBrand()));
        if (capacity != null)
            list.add(new SpecItemDto("Dung lượng", capacity));
        if (memoryType != null)
            list.add(new SpecItemDto("Loại bộ nhớ", memoryType));
        if (memorySpeed != null)
            list.add(new SpecItemDto("Tốc độ bộ nhớ", memorySpeed));
        if (moduleCount != null)
            list.add(new SpecItemDto("Số thanh", moduleCount + " thanh"));
        if (voltage != null)
            list.add(new SpecItemDto("Điện áp", voltage));
        if (formFactor != null)
            list.add(new SpecItemDto("Chuẩn bộ nhớ", formFactor));
        return list;
    }
}
