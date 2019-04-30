package cn.posinda.phoenix.job;

import cn.posinda.phoenix.job.importConcerned.entity.ImportFailure;
import cn.posinda.phoenix.job.importConcerned.service.ImportFailureService;
import cn.posinda.phoenix.tools.UUIDs;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class JobRunnable implements Runnable {

    @Setter
    private TableInfoService tableInfoService;

    @Setter
    private ImportFailureService importFailureService;

    @Setter
    private String startTime;

    @Setter
    private ConcurrentHashMap<String, ScheduledFuture<?>> map;

    @Setter
    private String jobName;

    @Setter
    private String tableName;

    JobRunnable(String startTime, String jobName, ConcurrentHashMap<String, ScheduledFuture<?>> map, String tableName) {
        this.startTime = startTime;
        this.map = map;
        this.jobName = jobName;
        this.tableName = tableName;
    }

    @Override
    public void run() {
        try {
            final JobDetail[] jobs = JobQuery.getAllJobs();
            final JobDetail jobDetail = JobQuery.getJobDetail(jobs, jobName);
            if (jobDetail.getState().equals(JobQuery.Status.SUCCEEDED.toString())) {
                Long inputRecords = JobQuery.getInputRecords(jobDetail.getId());
                stopJob();
                deleteFailure();
                saveToDataBase(inputRecords);
            }
            if (jobDetail.getState().equals(JobQuery.Status.FAILED.toString())) {
                insertImportFailure();
                stopJob();
            }
            if (jobDetail.getState().equals(JobQuery.Status.KILL_WAIT.toString())) {
                insertImportFailure();
                stopJob();
            }
            if (jobDetail.getState().equals(JobQuery.Status.KILLED.toString())) {
                insertImportFailure();
                stopJob();
            }
            if (jobDetail.getState().equals(JobQuery.Status.ERROR.toString())) {
                insertImportFailure();
                stopJob();
            }
        } catch (UnirestException e) {
            stopJob();
            e.printStackTrace();
        }

    }

    private void insertImportFailure() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ImportFailure entity = new ImportFailure();
        entity.setId(UUIDs.getInstance());
        entity.setCheckTime(format.format(new Date()));
        entity.setTaskName(jobName);
        entity.setTableName(tableName);
        importFailureService.insert(entity);
    }

    private void deleteFailure() {
        importFailureService.deleteAllByTableName(tableName);
    }

    private void stopJob() {
        ScheduledFuture<?> scheduledFuture = map.get(startTime);
        if (Objects.nonNull(scheduledFuture)) {
            scheduledFuture.cancel(true);
        }
        map.remove(startTime);
    }

    private void saveToDataBase(Long inputRecords) {
        TableInfo tableInfo = TableInfo.builder().tableName(tableName).build();
        tableInfo.setRecordCount(tableInfo.getRecordCount() + inputRecords);
        tableInfoService.updateRowNum(tableInfo);
    }
}
