package me.jongwoo.springbatchstudy.reader;

import me.jongwoo.springbatchstudy.domain.AccountSummary;
import me.jongwoo.springbatchstudy.domain.Transaction;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;

public class TransactionReader implements ItemStreamReader<Transaction> {

    private ItemStreamReader<FieldSet> fieldSetReader;
    private int recordCount = 0;
    private int expectedRecordCount = 0;
    private StepExecution stepExecution;

    public TransactionReader(ItemStreamReader<FieldSet> fieldSetReader) {
        this.fieldSetReader = fieldSetReader;
    }

    @Override
    public Transaction read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return process(fieldSetReader.read());
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.fieldSetReader.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        this.fieldSetReader.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        this.fieldSetReader.close();
    }

    // 이 방법은 효과적이지만
    // 잡의 트랜지션을 별도로 구성하고 스텝의 ExitStatus를 재정의해야 함..
//    @AfterStep
//    public ExitStatus afterStep(StepExecution execution){
//        if(recordCount == expectedRecordCount){
//            return execution.getExitStatus();
//        }
//
//        return ExitStatus.STOPPED;
//    }

    // AfterStep 대신 BeforeStep 사용하여 StepExecution을 가져온다
    // StepExecution에 접근하여 푸터 레코드를 읽을 때 StepExecution.setTerminateOnly() 메서드를 호출할 수 있다
    @BeforeStep
    public void beforeStep(StepExecution stepExecution){
        this.stepExecution = stepExecution;
    }

    private Transaction process(FieldSet fieldSet) {
        Transaction result = null;

        if(fieldSet != null){
            if(fieldSet.getFieldCount() > 1){
                result = new Transaction();
                result.setAccountNumber(fieldSet.readString(0));
                result.setTimestamp(fieldSet.readDate(1, "yyyy-MM-DD HH:mm:ss"));
                result.setAmount(fieldSet.readDouble(2));

                recordCount++;
            }else{
                expectedRecordCount = fieldSet.readInt(0);

                if(expectedRecordCount != this.recordCount){
                    // 스텝이 완료된 후 스프링 배치가 종료되도록 지사하는 플래그를 설정함.
                   this.stepExecution.setTerminateOnly();
                }
            }
        }
        return result;
    }


}
