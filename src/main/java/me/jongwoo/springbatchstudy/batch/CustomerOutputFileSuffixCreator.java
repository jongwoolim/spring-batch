package me.jongwoo.springbatchstudy.batch;

import org.springframework.batch.item.file.ResourceSuffixCreator;
import org.springframework.stereotype.Component;

@Component
public class CustomerOutputFileSuffixCreator implements ResourceSuffixCreator {

    @Override
    public String getSuffix(int i) {
        // 파일 이름 끝에 붙힐 접미사
        return i + ".xml";
    }
}
