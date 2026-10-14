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
@Table(name = "case_products")
@Getter
@Setter
@NoArgsConstructor
public class CaseProduct extends Product {

    private String caseFormFactor;
    private String supportedMotherboardFormFactors;
    private String maxGpuLength;
    private String maxCpuCoolerHeight;
    private String maxPsuLength;
    private String radiatorSize;

    @Override
    public List<SpecItemDto> getSpecs() {
        List<SpecItemDto> list = new ArrayList<>();
        if (getBrand() != null)
            list.add(new SpecItemDto("Hãng sản xuất", getBrand()));
        if (caseFormFactor != null)
            list.add(new SpecItemDto("Chuẩn case", caseFormFactor));
        if (supportedMotherboardFormFactors != null)
            list.add(new SpecItemDto("Chuẩn bo mạch chủ hỗ trợ", supportedMotherboardFormFactors));
        if (maxGpuLength != null)
            list.add(new SpecItemDto("Chiều dài GPU tối đa", maxGpuLength));
        if (maxCpuCoolerHeight != null)
            list.add(new SpecItemDto("Chiều cao tản nhiệt CPU tối đa", maxCpuCoolerHeight));
        if (maxPsuLength != null)
            list.add(new SpecItemDto("Chiều dài PSU tối đa", maxPsuLength));
        if (radiatorSize != null)
            list.add(new SpecItemDto("Kích thước radiator hỗ trợ", radiatorSize));
        return list;
    }
}
