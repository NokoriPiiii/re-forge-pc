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
@Table(name = "gpu_products")
@Getter
@Setter
@NoArgsConstructor
public class GpuProduct extends Product {

    private String gpuCapacity;
    private String memoryType;
    private String memoryInterface;
    private String gpuType;
    private String gpuClock;
    private String pciInterface;
    private String powerConsumption;
    private String powerConnector;
    private String gpuLength;

    @Override
    public List<SpecItemDto> getSpecs() {
        List<SpecItemDto> list = new ArrayList<>();
        if (getBrand() != null)
            list.add(new SpecItemDto("Hãng sản xuất", getBrand()));
        if (gpuType != null)
            list.add(new SpecItemDto("Loại GPU", gpuType));
        if (gpuCapacity != null)
            list.add(new SpecItemDto("Dung lượng", gpuCapacity));
        if (memoryType != null)
            list.add(new SpecItemDto("Kiểu bộ nhớ", memoryType));
        if (memoryInterface != null)
            list.add(new SpecItemDto("Giao diện bộ nhớ", memoryInterface));
        if (gpuClock != null)
            list.add(new SpecItemDto("GPU clock", gpuClock));
        if (pciInterface != null)
            list.add(new SpecItemDto("Giao diện kết nối", pciInterface));
        if (powerConsumption != null)
            list.add(new SpecItemDto("Công suất tiêu thụ", powerConsumption));
        if (powerConnector != null)
            list.add(new SpecItemDto("Đầu cấp nguồn", powerConnector));
        if (gpuLength != null)
            list.add(new SpecItemDto("Chiều dài", gpuLength));
        return list;
    }
}
