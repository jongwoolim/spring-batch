package me.jongwoo.springbatchstudy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

@RestController
@RequiredArgsConstructor
public class JobLauncherController {

    private final JobLauncher jobLauncher;
    private final ApplicationContext context;

    @PostMapping("/run")
    public ExitStatus runJob(@RequestBody JobLaunchRequest request) throws Exception{

        Job job = this.context.getBean(request.getName(), Job.class);

        return this.jobLauncher.run(job, request.getJobParameters()).getExitStatus();
    }

    public static class JobLaunchRequest{
        private String name;

        private Properties jobParameters;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Properties getJobParamsProperties() {
            return jobParameters;
        }

        public void setJobParameters(Properties jobParameters) {
            this.jobParameters = jobParameters;
        }

        public JobParameters getJobParameters() {
            Properties properties = new Properties();
            properties.putAll(this.jobParameters);
            return new JobParametersBuilder(properties)
                    .toJobParameters();
        }

    }
}
