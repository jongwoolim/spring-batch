package me.jongwoo.springbatchstudy.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.OnReadError;
import org.springframework.batch.item.file.FlatFileParseException;

public class AccountItemListener {

    private static final Logger logger = LoggerFactory.getLogger(AccountItemListener.class);

    @OnReadError
    public void onReadError(Exception e){

        if(e instanceof FlatFileParseException){
            FlatFileParseException ffpe = (FlatFileParseException) e;
            StringBuilder errorMessage = new StringBuilder();

            errorMessage.append("An error occured while processing the " + ffpe.getLineNumber() +
                    " line of the file. Below was the faulty " +
                    "input \n");
            errorMessage.append(ffpe.getInput() + "\n");

            logger.error(errorMessage.toString(), ffpe);
        }

    }
}
