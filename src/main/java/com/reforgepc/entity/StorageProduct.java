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
@Table(name = "storage_products")
@Getter
@Setter
@NoArgsConstructor
public class StorageProduct extends Product {

    private String storageType;
    private String storageInterface;
    private String formFactor;
    private String capacity;
    private String readSpeed;
    private String writeSpeed;

    @Override
    public List<SpecItemDto> getSpecs() {
        List<SpecItemDto> list = new ArrayList<>();
        if (getBrand() != null) list.add(new SpecItemDto("Hãng sản xuất", getBrand()));
        if (storageType != null) list.add(new SpecItemDto("Loại ổ cứng", storageType));
        if (storageInterface != null) list.add(new SpecItemDto("Chuẩn giao tiếp", storageInterface));
        if (formFactor != null) list.add(new SpecItemDto("Kích thước", formFactor));
        if (capacity != null) list.add(new SpecItemDto("Dung lượng", capacity));
        if (readSpeed != null) list.add(new SpecItemDto("Tốc độ đọc", readSpeed));
        if (writeSpeed != null) list.add(new SpecItemDto("Tốc độ ghi", writeSpeed));
        return list;
    }
}
